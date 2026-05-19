package algo.vk_monetisation.eis.jca.dadata;

import jakarta.resource.ResourceException;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class DadataConnectionImpl implements DadataConnection {

    private final String apiUrl;
    private final String token;
    private final boolean stubMode;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private boolean closed;

    public DadataConnectionImpl(String apiUrl, String token, boolean stubMode) {
        this.apiUrl = apiUrl;
        this.token = token;
        this.stubMode = stubMode;
    }

    @Override
    public DadataInnResponse validateInn(DadataInnRequest request) throws ResourceException {
        checkOpen();
        if (stubMode || token == null || token.isBlank()) {
            String stub = "{\"stub\":true,\"inn\":\"" + request.inn() + "\",\"valid\":true}";
            log.info("DaData JCA stub: INN {}", request.inn());
            return new DadataInnResponse(true, "STUB " + request.inn(), stub);
        }
        try {
            String body = "{\"query\":\"" + request.inn() + "\"}";
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Token " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            String raw = response.body();
            boolean valid = response.statusCode() == 200 && raw.contains("\"suggestions\"");
            String name = extractCompanyName(raw);
            return new DadataInnResponse(valid, name, raw);
        } catch (Exception e) {
            throw new ResourceException("DaData HTTP error: " + e.getMessage(), e);
        }
    }

    private String extractCompanyName(String raw) {
        int idx = raw.indexOf("\"value\"");
        if (idx < 0) {
            return null;
        }
        int start = raw.indexOf(':', idx) + 1;
        int q1 = raw.indexOf('"', start + 1);
        int q2 = raw.indexOf('"', q1 + 1);
        if (q1 > 0 && q2 > q1) {
            return raw.substring(q1 + 1, q2);
        }
        return null;
    }

    @Override
    public void close() {
        closed = true;
    }

    private void checkOpen() throws ResourceException {
        if (closed) {
            throw new ResourceException("DadataConnection закрыто");
        }
    }
}
