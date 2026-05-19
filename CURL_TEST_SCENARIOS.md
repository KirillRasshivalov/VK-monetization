# CURL test scenarios

Ниже набор `curl`-сценариев для текущего API.
Формат:
- Happy path
- Негативные кейсы (валидация/404/плохой pageNum/пустые данные)

## 0) Переменные окружения

```bash
export BASE_URL="http://127.0.0.1:8080"

# Заполняются по результатам create-запросов/из БД:
export PERSON_ID=1
export CAMPAIGN_ID=1
export CONTENT_ID=1
export CONTACTS_ID=1
export LEGAL_ID=1

export PAGE_NUM=0

# Файлы для multipart:
export IMG_PATH="/absolute/path/to/test-image.png"
export VIDEO_PATH="/absolute/path/to/test-video.mp4"
```

---

## 1) Ping

### 1.1 Success
```bash
curl -sS "$BASE_URL/ping"
```

---

## 2) Company registration

Endpoint: `POST /register/company`

### 2.1 Success
```bash
curl -sS -X POST "$BASE_URL/register/company" \
  -H "Content-Type: application/json" \
  -d '{
    "personInfoDTO": {
      "name": "Ivan",
      "surname": "Ivanov",
      "lastName": "Petrovich",
      "balance": 0.0
    },
    "companyInfoDTO": {
      "inn": "123456789012",
      "nameOfCompany": "Test Company",
      "ogrnip": "312345678901234"
    },
    "legalEntityDTO": {
      "postalIndex": 190000,
      "region": "SPB",
      "town": "Saint-Petersburg",
      "street": "Nevsky",
      "address": "10",
      "apartmentNumber": 5
    },
    "contactsDTO": {
      "contactPerson": "+79991234567",
      "contactNumber": "+79997654321"
    }
  }'
```

### 2.2 Invalid INN
```bash
curl -sS -X POST "$BASE_URL/register/company" \
  -H "Content-Type: application/json" \
  -d '{
    "personInfoDTO": { "name":"Ivan", "surname":"Ivanov", "lastName":"Petrovich", "balance":0.0 },
    "companyInfoDTO": { "inn":"123", "nameOfCompany":"Bad", "ogrnip":"312345678901234" },
    "legalEntityDTO": { "postalIndex":190000, "region":"SPB", "town":"SPB", "street":"Nevsky", "address":"10", "apartmentNumber":5 },
    "contactsDTO": { "contactPerson":"+79991234567", "contactNumber":"+79997654321" }
  }'
```

---

## 3) Person CRUD

Base: `/persons`

### 3.1 Create person (success)
```bash
curl -sS -X POST "$BASE_URL/persons/create_person" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Petr",
    "surname":"Petrov",
    "lastName":"Petrovich",
    "balance":5000
  }'
```

### 3.2 Create person (validation error)
```bash
curl -sS -X POST "$BASE_URL/persons/create_person" \
  -H "Content-Type: application/json" \
  -d '{
    "name":null,
    "surname":null,
    "lastName":"X",
    "balance":100
  }'
```

### 3.3 Get person (success)
```bash
curl -sS "$BASE_URL/persons/get_person/$PERSON_ID"
```

### 3.4 Get person (not found)
```bash
curl -sS "$BASE_URL/persons/get_person/999999"
```

### 3.5 Update person (success)
```bash
curl -sS -X PUT "$BASE_URL/persons/update_person/$PERSON_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "name":"Petr",
    "surname":"Sidorov",
    "lastName":"Petrovich",
    "balance":7000
  }'
```

### 3.6 Delete person
```bash
curl -sS -X DELETE "$BASE_URL/persons/delete_person/$PERSON_ID"
```

---

## 4) Legal entity CRUD

Base: `/legal_entities`

### 4.1 Create legal entity
```bash
curl -sS -X POST "$BASE_URL/legal_entities/create_leegal_entities" \
  -H "Content-Type: application/json" \
  -d '{
    "postalIndex": 190000,
    "region": "SPB",
    "town": "SPB",
    "street": "Nevsky",
    "address": "10",
    "apartmentNumber": 5
  }'
```

### 4.2 Get legal entity
```bash
curl -sS "$BASE_URL/legal_entities/get_legal_entity/$LEGAL_ID"
```

### 4.3 Update legal entity
```bash
curl -sS -X PUT "$BASE_URL/legal_entities/update_legal_entity/$LEGAL_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "postalIndex": 191000,
    "region": "SPB",
    "town": "SPB",
    "street": "Liteiny",
    "address": "20",
    "apartmentNumber": 12
  }'
```

### 4.4 Delete legal entity
```bash
curl -sS -X DELETE "$BASE_URL/legal_entities/delete_legal_entity/$LEGAL_ID"
```

---

## 5) Contacts

Base: `/contacts`

### 5.1 Create contacts
```bash
curl -sS -X POST "$BASE_URL/contacts/create_contacts/$PERSON_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "contactPerson":"Manager One",
    "contactNumber":"+79991112233"
  }'
```

### 5.2 Show contacts
```bash
curl -sS "$BASE_URL/contacts/show_contacts/$CONTACTS_ID"
```

### 5.3 Update contacts
```bash
curl -sS -X PUT "$BASE_URL/contacts/update_contacts/$CONTACTS_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "contactPerson":"Manager Two",
    "contactNumber":"+79994445566"
  }'
```

### 5.4 Invalid contacts payload
```bash
curl -sS -X POST "$BASE_URL/contacts/create_contacts/$PERSON_ID" \
  -H "Content-Type: application/json" \
  -d 'null'
```

---

## 6) Wallet

Base: `/wallet`

### 6.1 Top up (success)
```bash
curl -sS -X POST "$BASE_URL/wallet/topup" \
  -H "Content-Type: application/json" \
  -d '{
    "personId": '"$PERSON_ID"',
    "amount": 10000
  }'
```

### 6.2 Top up (amount <= 0)
```bash
curl -sS -X POST "$BASE_URL/wallet/topup" \
  -H "Content-Type: application/json" \
  -d '{
    "personId": '"$PERSON_ID"',
    "amount": 0
  }'
```

### 6.3 Top up (unknown person)
```bash
curl -sS -X POST "$BASE_URL/wallet/topup" \
  -H "Content-Type: application/json" \
  -d '{
    "personId": 999999,
    "amount": 100
  }'
```

---

## 7) Campaigns

Base: `/orders`

### 7.1 Create campaign (success)
```bash
curl -sS -X POST "$BASE_URL/orders/advertisement_campaign" \
  -H "Content-Type: application/json" \
  -d '{
    "personId": '"$PERSON_ID"',
    "title": "Campaign A",
    "description": "Test campaign",
    "okvdCode": "62.01",
    "status": null,
    "budget": 1000.0,
    "targetAudience": "developers",
    "startDate": null,
    "endDate": null,
    "imageData": null
  }'
```

### 7.2 Create campaign (invalid personId)
```bash
curl -sS -X POST "$BASE_URL/orders/advertisement_campaign" \
  -H "Content-Type: application/json" \
  -d '{
    "personId": 999999,
    "title": "Bad Campaign",
    "description": "No owner",
    "okvdCode": "62.01",
    "status": null,
    "budget": 1000.0,
    "targetAudience": "developers",
    "startDate": null,
    "endDate": null,
    "imageData": null
  }'
```

### 7.3 Get campaign status
```bash
curl -sS "$BASE_URL/orders/campaign/$CAMPAIGN_ID"
```

### 7.4 Get campaign stats (page)
```bash
curl -sS "$BASE_URL/orders/campaign/$CAMPAIGN_ID/stats?pageNum=$PAGE_NUM"
```

### 7.5 Get all campaigns by person
```bash
curl -sS "$BASE_URL/orders/get_all_campaigns/$PERSON_ID?pageNum=$PAGE_NUM"
```

### 7.6 Get all campaigns invalid page
```bash
curl -sS "$BASE_URL/orders/get_all_campaigns/$PERSON_ID?pageNum=-1"
```

### 7.7 Update campaign
```bash
curl -sS -X PUT "$BASE_URL/orders/update_campaign/$CAMPAIGN_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "personId": '"$PERSON_ID"',
    "title": "Campaign A updated",
    "description": "Updated description",
    "okvdCode": "62.01",
    "status": "DRAFT",
    "budget": 1200.0,
    "targetAudience": "managers",
    "startDate": null,
    "endDate": null,
    "imageData": null
  }'
```

### 7.8 Delete campaign
```bash
curl -sS -X DELETE "$BASE_URL/orders/delete_campaign/$CAMPAIGN_ID"
```

---

## 8) Author content

Base: `/author`

### 8.1 Upload content (both image + video)
```bash
curl -sS -X POST "$BASE_URL/author/content" \
  -F "campaignId=$CAMPAIGN_ID" \
  -F "image=@$IMG_PATH;type=image/png" \
  -F "video=@$VIDEO_PATH;type=video/mp4"
```

### 8.2 Upload content (only image)
```bash
curl -sS -X POST "$BASE_URL/author/content" \
  -F "campaignId=$CAMPAIGN_ID" \
  -F "image=@$IMG_PATH;type=image/png"
```

### 8.3 Upload content (only video)
```bash
curl -sS -X POST "$BASE_URL/author/content" \
  -F "campaignId=$CAMPAIGN_ID" \
  -F "video=@$VIDEO_PATH;type=video/mp4"
```

### 8.4 Upload content (no files -> validation error)
```bash
curl -sS -X POST "$BASE_URL/author/content" \
  -F "campaignId=$CAMPAIGN_ID"
```

### 8.5 Upload content (unknown campaign)
```bash
curl -sS -X POST "$BASE_URL/author/content" \
  -F "campaignId=999999" \
  -F "image=@$IMG_PATH;type=image/png" \
  -F "video=@$VIDEO_PATH;type=video/mp4"
```

### 8.6 Get all content in campaign
```bash
curl -sS "$BASE_URL/author/get_all_content_campaigns/$CAMPAIGN_ID?pageNum=$PAGE_NUM"
```

### 8.7 Update content
```bash
curl -sS -X PUT "$BASE_URL/author/update_content/$CONTENT_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "imageContentType": "image/png",
    "imageFileName": "updated.png",
    "imageData": null,
    "videoData": null,
    "videoContentType": "video/mp4",
    "videoFileName": "updated.mp4",
    "mediaMetadata": "{}"
  }'
```

### 8.8 Delete content
```bash
curl -sS -X DELETE "$BASE_URL/author/delete_content/$CONTENT_ID"
```

---

## 9) Имитация логики scheduler (он удален)

В текущем коде `@Scheduled` отключен, поэтому автоинкремента на сервере нет.
Тем не менее можно тестом "эмулировать правило" и проверять ожидаемое поведение:
- каждая "минута" -> `views + 1`
- каждый 5-й просмотр -> `likes + 1`

### 9.1 Снять текущие stats
```bash
curl -sS "$BASE_URL/orders/campaign/$CAMPAIGN_ID/stats?pageNum=0"
```

### 9.2 Локально посчитать ожидаемые значения для N тиков
```bash
# Пример: 12 тиков от текущих значений.
CURRENT='{"views":7,"likes":1}'
TICKS=12
python3 - << 'PY'
import json
state = json.loads('''{"views":7,"likes":1}''')
ticks = 12
v,l = state["views"], state["likes"]
for _ in range(ticks):
    v += 1
    if v % 5 == 0:
        l += 1
print({"expectedViews": v, "expectedLikes": l})
PY
```

### 9.3 Проверить фактические stats повторным GET (сейчас без scheduler они не изменятся)
```bash
curl -sS "$BASE_URL/orders/campaign/$CAMPAIGN_ID/stats?pageNum=0"
```

> Для реальной серверной имитации через HTTP нужен отдельный тестовый endpoint вроде:
> `POST /orders/campaign/{campaignId}/simulate_ticks?ticks=5`
> (его в текущем API нет).

---

## 10) Distributed TX (активация + ledger)

### 10.1 Upload content в рамках distributed transaction
```bash
curl -sS -X POST "$BASE_URL/author/content" \
  -F "campaignId=$CAMPAIGN_ID" \
  -F "image=@$IMG_PATH;type=image/png"
```

### 10.2 Проверить ledger-запись активации
```bash
curl -sS "$BASE_URL/author/activation-ledger/$CAMPAIGN_ID"
```

Ожидание:
- вернется хотя бы одна запись;
- `campaignId` совпадает с `$CAMPAIGN_ID`;
- заполнены `globalTxId`, `debitedAmount`, `contentId`.

### 10.3 Тест rollback (через app.tx.simulate-ledger-failure=true)
1. Включить в конфиге:
```properties
app.tx.simulate-ledger-failure=true
```
2. Перезапустить приложение.
3. Повторить `10.1`.
4. Проверить, что новая запись контента и изменения кампании не зафиксированы.

