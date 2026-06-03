package algo.vk_monetisation.jca;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class FnsManagedConnection {

    private final FnsManagedConnectionFactory mcf;

    public FnsConnection getConnection() {
        log.debug("Создание FnsConnection через ManagedConnection");
        return new FnsConnectionImpl(this);
    }

    public void destroy() {
        log.debug("Уничтожение managed connection");
    }

    public String verifyInn(String inn) {
        log.info("Проверка ИНН {} через API vok-demo", inn);

        if (!validateInnFormat(inn)) {
            log.warn("Неверный формат ИНН: {}", inn);
            return "INVALID";
        }
        HttpURLConnection conn = null;
        try {
            String baseUrl = mcf.getFnsServerUrl();
            String urlString = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "req?inn=" + inn;
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(mcf.getConnectionTimeout());
            conn.setReadTimeout(mcf.getConnectionTimeout());
            conn.setRequestProperty("Accept", "application/json");

            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(responseCode == 200 ? conn.getInputStream() : conn.getErrorStream(),
                            StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            if (responseCode == 200) {
                String body = response.toString();
                if (body != null && body.length() > 10 && !body.contains("\"error\"")) {
                    log.info("ИНН {} существует в системе", inn);
                    return "VERIFIED";
                } else {
                    log.warn("API вернул пустой ответ или ошибку для ИНН {}", inn);
                    return "INVALID";
                }
            } else {
                log.warn("API вернул код {} для ИНН {}", responseCode, inn);
                return "INVALID";
            }
        } catch (Exception e) {
            log.error("Ошибка вызова демо-API для ИНН {}", inn, e);
            return "ERROR";
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private boolean validateInnFormat(String inn) {
        return inn != null && (inn.length() == 10 || inn.length() == 12) && inn.matches("\\d+");
    }
}