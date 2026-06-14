package br.edu.idp.es.stsw.pyramid.client;

import br.edu.idp.es.stsw.pyramid.domain.TipResponse;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpExternalClient implements ExternalClient {

    private final RestClient restClient;

    public HttpExternalClient(
            RestClient.Builder restClientBuilder,
            @Value("${external.base-url}") String externalBaseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(externalBaseUrl).build();
    }

    @Override
    public TipResponse fetchTip() {
        JsonNode response = restClient.get()
                .uri("/tip")
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("External service returned no payload.");
        }

        String message = response.path("message").asText("");
        if (message.isBlank()) {
            throw new IllegalStateException("External service payload is incomplete.");
        }

        return new TipResponse(message);
    }
}
