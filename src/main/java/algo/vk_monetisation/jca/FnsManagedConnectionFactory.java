package algo.vk_monetisation.jca;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class FnsManagedConnectionFactory {
    private String fnsServerUrl = "https://api.sbis.ru/vok-demo";
    private int connectionTimeout = 5000;

    public FnsManagedConnection createManagedConnection() {
        log.info("Создание managed connection для URL: {}", fnsServerUrl);
        return new FnsManagedConnection(this);
    }
}