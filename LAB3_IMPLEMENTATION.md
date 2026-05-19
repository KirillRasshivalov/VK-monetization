# Лабораторная работа 3 — асинхронная обработка, кластер, планировщик, JCA/EIS

## 1. Цель и охват

Доработано приложение VK Monetisation (на базе лаб. 1–2) в соответствии с `task.md` (строки 61–87):

| Требование | Реализация |
|------------|------------|
| Асинхронность, очередь сообщений | RabbitMQ |
| Отправка через STOMP | `StompCampaignEventPublisher` (WebSocket STOMP → RabbitMQ) |
| Получение через JMS | `@JmsListener` + `rabbitmq-jms` |
| Два независимых узла | Профили `node-1` / `node-2` через свойства кластера |
| Планировщик | `ContentEngagementScheduler` (`@Scheduled`) |
| Интеграция EIS через JCA | DaData (ИНН) + SMS.ru, Jakarta Connectors |

---

## 2. Бизнес-сценарии (что и зачем асинхронно)

### 2.1. Пост-активация кампании (основной async-прецедент)

**Когда:** после успешной JTA-транзакции активации кампании при загрузке первого контента (`AuthorContentService.uploadContentForCampaign`).

**Почему асинхронно:** проверка контрагента в DaData и отправка SMS — внешние вызовы с задержкой и возможными сбоями. Их нельзя держать в синхронном HTTP-запросе загрузки медиа: пользователь получит ответ сразу после commit транзакции, а фоновые узлы обработают событие.

**Поток:**

```
HTTP upload → JTA (списание, ACTIVE, content, ledger) → commit
    → afterCommit → STOMP publish (2 очереди)
        → JMS @JmsListener node-1: DaData JCA
        → JMS @JmsListener node-2: SMS.ru JCA
```

Очереди:

- `vk.campaign.compliance` — проверка ИНН рекламодателя (DaData)
- `vk.campaign.notifications` — SMS рекламодателю (SMS.ru)

### 2.2. Завершение кампании (второй async-прецедент)

**Когда:** планировщик переводит кампанию в `COMPLETED` и фиксирует `finalViews` / `finalLikes`.

**Почему асинхронно:** итоговое SMS с финальной статистикой — отдельная задача узла уведомлений, не блокирует tick планировщика.

### 2.3. Планировщик статистики (синхронный внутри узла)

`ContentEngagementScheduler` каждые 60 с (настраивается):

- для всех `ACTIVE` кампаний и **всего** связанного контента (`OneToMany`): `views += 1`, каждые 5 просмотров `likes += 1`;
- при `endDate <= now`: фиксация финальной статистики, статус `COMPLETED`, после commit — STOMP в очередь уведомлений.

---

## 3. Архитектура компонентов

```
┌─────────────────┐     STOMP      ┌──────────────┐     JMS      ┌─────────────┐
│  App node-1/2   │ ──────────────►│   RabbitMQ   │───────────►│  Listeners  │
│  (publisher)    │  /queue/...    │  (2 queues)  │            │  + JCA EIS  │
└─────────────────┘                └──────────────┘            └─────────────┘
        │                                                              │
        │ @Scheduled                                                   ▼
        ▼                                                         PostgreSQL
 ContentEngagementScheduler                                    eis_integration_log
```

### Ключевые классы

| Компонент | Путь |
|-----------|------|
| STOMP publisher | `messaging/StompCampaignEventPublisher.java` |
| JMS listeners | `messaging/CampaignComplianceJmsListener.java`, `CampaignNotificationJmsListener.java` |
| Событие | `messaging/CampaignLifecycleEvent.java` |
| Планировщик | `scheduler/ContentEngagementScheduler.java` |
| JCA DaData | `eis/jca/dadata/*` |
| JCA SMS.ru | `eis/jca/smsru/*` |
| Resource Adapter | `eis/jca/VkEisResourceAdapter.java`, `META-INF/ra.xml` |
| Лог EIS | `entities/EisIntegrationLog.java` |
| Диагностика | `GET /ops/cluster`, `GET /ops/eis-logs/campaign/{id}` |

---

## 4. Два узла кластера

Разделение по **ролям слушателя**, а не по разным приложениям:

| Узел | `app.cluster.node-id` | Compliance (DaData) | Notifications (SMS.ru) | Порт (пример) |
|------|----------------------|---------------------|------------------------|---------------|
| node-1 | `node-1` | `true` | `false` | 8080 |
| node-2 | `node-2` | `false` | `true` | 8081 |

Файл-пример: `application-node2.properties`.

Оба узла могут принимать HTTP и публиковать STOMP; обрабатывает сообщения только включённый listener.

---

## 5. EIS и JCA (российские сервисы)

### 5.1. DaData — проверка ИНН

- **Зачем:** compliance рекламодателя при активации кампании.
- **API:** `POST https://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/party`
- **JCA:** `DadataManagedConnectionFactory` → `DadataConnection.validateInn()`

### 5.2. SMS.ru — уведомления

- **Зачем:** SMS при активации и при завершении кампании.
- **API:** `GET https://sms.ru/sms/send?api_id=...&to=...&msg=...&json=1`
- **JCA:** `SmsRuManagedConnectionFactory` → `SmsRuConnection.sendSms()`

### 5.3. Stub-режим (по умолчанию)

`app.eis.stub-mode=true` — без ключей API запросы не уходят в интернет, в лог пишется stub-ответ. Для боевого режима:

```bash
export EIS_STUB_MODE=false
export DADATA_TOKEN=ваш_токен
export SMSRU_API_ID=ваш_api_id
```

Результаты всех вызовов сохраняются в таблицу `eis_integration_log`.

### 5.4. Деплой JCA на WildFly (опционально для сдачи)

1. Собрать RAR с классами адаптера и `META-INF/ra.xml` (классы уже в WAR; для WildFly можно вынести в отдельный модуль).
2. `jboss-cli`: deploy `vk-eis-connector.rar`
3. Настроить connection factory в подсистеме resource-adapters и JNDI `java:/eis/DadataConnectionFactory`, `java:/eis/SmsRuConnectionFactory`
4. В Spring-профиле `wildfly` заменить `EisJcaConfiguration` на lookup через `@Resource`

Локально и на embedded Tomcat используется programmatic JCA (`EisJcaConfiguration` создаёт `ManagedConnectionFactory` в JVM).

---

## 6. Конфигурация

### 6.1. RabbitMQ (Docker)

```bash
docker compose up -d rabbitmq
```

Порты: `5672` (AMQP/JMS), `15674` (Web STOMP), `61613` (STOMP), `15672` (management UI).

### 6.2. Основные свойства (`application.properties`)

```properties
app.messaging.stomp.broker-url=ws://localhost:15674/ws
app.messaging.queue.compliance=vk.campaign.compliance
app.messaging.queue.notifications=vk.campaign.notifications
app.cluster.node-id=node-1
app.cluster.compliance-listener.enabled=true
app.cluster.notification-listener.enabled=true
app.scheduler.engagement.fixed-rate-ms=60000
app.eis.stub-mode=true
```

### 6.3. Переменные окружения

| Переменная | Назначение |
|------------|------------|
| `URL`, `USERNAME`, `PSQL_PASSWORD` | PostgreSQL |
| `RABBITMQ_HOST`, `RABBITMQ_PORT` | RabbitMQ |
| `CLUSTER_NODE_ID` | Идентификатор узла |
| `COMPLIANCE_LISTENER` / `NOTIFICATION_LISTENER` | `true`/`false` |
| `DADATA_TOKEN`, `SMSRU_API_ID` | Ключи EIS |
| `EIS_STUB_MODE` | `true` — без реальных HTTP к EIS |

---

## 7. Тестирование

### 7.1. Подготовка

```bash
# 1. PostgreSQL + переменные БД
export URL=jdbc:postgresql://localhost:5432/studs
export USERNAME=s413047
export PSQL_PASSWORD=...

# 2. RabbitMQ
docker compose up -d rabbitmq

# 3. Сборка
mvn -DskipTests package
```

### 7.2. Запуск двух узлов

**Терминал 1 (node-1 — compliance + scheduler):**

```bash
export CLUSTER_NODE_ID=node-1
export COMPLIANCE_LISTENER=true
export NOTIFICATION_LISTENER=false
java -jar target/VK_monetisation-0.0.1-SNAPSHOT.jar
```

**Терминал 2 (node-2 — notifications):**

```bash
export CLUSTER_NODE_ID=node-2
export COMPLIANCE_LISTENER=false
export NOTIFICATION_LISTENER=true
java -jar target/VK_monetisation-0.0.1-SNAPSHOT.jar \
  --spring.config.additional-location=classpath:application-node2.properties
```

### 7.3. Сценарий end-to-end (curl)

```bash
BASE=http://localhost:8080

# Регистрация модератора / логин (если ещё нет пользователя)
curl -s -X POST "$BASE/auth/register" -H 'Content-Type: application/json' \
  -d '{"email":"mod@test.ru","password":"mod123","role":"MODERATOR"}'

TOKEN=$(curl -s -X POST "$BASE/auth/login" -H 'Content-Type: application/json' \
  -d '{"email":"mod@test.ru","password":"mod123"}' | jq -r .token)

AUTH="Authorization: Bearer $TOKEN"

# 1. Рекламодатель + компания + контакты + пополнение
curl -s -X POST "$BASE/persons" -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"name":"Иван","surname":"Иванов","email":"adv@test.ru"}'

# ... legal entity, company INN, contacts, wallet/topup ...

# 2. Создать кампанию DRAFT
curl -s -X POST "$BASE/orders" -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"title":"Тест Lab3","budget":1000,"personId":1,...}'

# 3. Загрузить контент → активация + async
curl -s -X POST "$BASE/author/content?campaignId=1" -H "$AUTH" \
  -F "image=@test.jpg"

# 4. Проверить кластер и логи EIS (через ~5–10 с)
curl -s "$BASE/ops/cluster" -H "$AUTH" | jq .

curl -s "$BASE/ops/eis-logs/campaign/1" -H "$AUTH" | jq .

# 5. Подождать 4+ мин (activation duration) или уменьшить app.tx.campaign-activation-duration-minutes=1
# Планировщик завершит кампанию → SMS COMPLETED на node-2

# 6. Статистика кампании
curl -s "$BASE/orders/1/stats" -H "$AUTH" | jq .
```

### 7.4. Что проверить на защите

1. **STOMP:** в логах node-1 после upload: `STOMP: событие ACTIVATED отправлено в /queue/vk.campaign.compliance`
2. **JMS node-1:** `JMS compliance: campaignId=..., inn=...`
3. **JMS node-2:** `JMS notification: type=ACTIVATED`
4. **Таблица `eis_integration_log`:** записи `DADATA` / `SMSRU` со статусом `SUCCESS` (stub)
5. **Планировщик:** рост `views`/`likes`, затем `COMPLETED` и `finalViews`
6. **RabbitMQ UI:** `http://localhost:15672` (guest/guest) — очереди и потребление

---

## 8. Деплой на Helios

### 8.1. RabbitMQ

На сервере или отдельной VM:

```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15674:15674 \
  -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3.13-management
# включить stomp и web-stomp внутри контейнера
```

Указать `RABBITMQ_HOST` / `STOMP_BROKER_URL=ws://<host>:15674/ws` в окружении приложения.

### 8.2. Два инстанса приложения

```bash
# instance 1
nohup java -jar VK_monetisation-0.0.1-SNAPSHOT.jar \
  --server.port=18080 \
  --app.cluster.node-id=node-1 \
  --app.cluster.compliance-listener.enabled=true \
  --app.cluster.notification-listener.enabled=false \
  > node1.log 2>&1 &

# instance 2
nohup java -jar VK_monetisation-0.0.1-SNAPSHOT.jar \
  --server.port=18081 \
  --app.cluster.node-id=node-2 \
  --app.cluster.compliance-listener.enabled=false \
  --app.cluster.notification-listener.enabled=true \
  > node2.log 2>&1 &
```

### 8.3. WildFly (WAR)

```bash
mvn -DskipTests package
cp target/VK_monetisation-0.0.1-SNAPSHOT.war $WILDFLY/standalone/deployments/
```

Профиль `wildfly` + JTA из лаб. 2 сохраняется. Для Lab 3 добавьте системные свойства RabbitMQ и cluster в `standalone.xml` или через `-D` при запуске.

SSH-туннель (как в лаб. 1–2):

```bash
ssh -p 2222 -L 18080:localhost:18080 s413047@se.ifmo.ru
```

---

## 9. Соответствие заданию (чеклист)

- [x] Модель «очередь сообщений» — RabbitMQ
- [x] Отправка STOMP — `StompCampaignEventPublisher`
- [x] Получение JMS `@JmsListener` — compliance + notifications
- [x] Два независимых узла — разные профили слушателей
- [x] Планировщик `@Scheduled` — автоинкремент views/likes + завершение кампании
- [x] JCA Jakarta Connectors — ManagedConnectionFactory, ResourceAdapter, `ra.xml`
- [x] EIS Россия — DaData + SMS.ru
- [x] REST для проверки — `/ops/cluster`, `/ops/eis-logs/campaign/{id}`
- [x] Изменения учтены в документации и сценариях тестирования

---

## 10. Ограничения и замечания

1. **STOMP при недоступном RabbitMQ:** публикация логируется как warning, HTTP-активация не откатывается (событие best-effort после commit).
2. **Распределённая транзакция JMS+БД:** на embedded Tomcat listener использует локальную `@Transactional` для записи в `eis_integration_log`; полный XA JMS возможен на WildFly с XA connection factory.
3. **Длительность кампании:** по умолчанию 4 минуты (`app.tx.campaign-activation-duration-minutes`) для быстрой демонстрации завершения планировщиком.
