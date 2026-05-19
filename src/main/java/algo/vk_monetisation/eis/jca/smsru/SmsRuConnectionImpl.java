package algo.vk_monetisation.eis.jca.smsru;

import jakarta.resource.ResourceException;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class SmsRuConnectionImpl implements SmsRuConnection {

    private final String apiUrl;
    private final String apiId;
    private final boolean stubMode;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private boolean closed;

    public SmsRuConnectionImpl(String apiUrl, String apiId, boolean stubMode) {
        this.apiUrl = apiUrl;
        this.apiId = apiId;
        this.stubMode = stubMode;
    }

    @Override
    public SmsRuSendResponse sendSms(SmsRuSendRequest request) throws ResourceException {
        checkOpen();
        if (stubMode || apiId == null || apiId.isBlank()) {
            String stub = "{\"stub\":true,\"status\":\"OK\",\"phone\":\"" + request.phone() + "\"}";
            log.info("SMS.ru JCA stub: {} -> {}", request.phone(), request.message());
            return new SmsRuSendResponse(true, stub);
        }
        try {
            String query = apiUrl
                    + "?api_id=" + enc(apiId)
                    + "&to=" + enc(request.phone())
                    + "&msg=" + enc(request.message())
                    + "&json=1";
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(query))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String raw = response.body();
            boolean success = response.statusCode() == 200 && raw.contains("\"status\":\"OK\"");
            return new SmsRuSendResponse(success, raw);
        } catch (Exception e) {
            throw new ResourceException("SMS.ru HTTP error: " + e.getMessage(), e);
        }
    }

    private static String enc(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        closed = true;
    }

    private void checkOpen() throws ResourceException {
        if (closed) {
            throw new ResourceException("SmsRuConnection закрыто");
        }
    }
}
