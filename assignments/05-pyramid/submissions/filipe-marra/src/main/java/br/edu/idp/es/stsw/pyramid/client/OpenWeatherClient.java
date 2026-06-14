package br.edu.idp.es.stsw.pyramid.client;

import br.edu.idp.es.stsw.pyramid.domain.WeatherDetails;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenWeatherClient implements WeatherClient {

    private final RestClient restClient;
    private final String apiKey;

    public OpenWeatherClient(
            RestClient.Builder restClientBuilder,
            @Value("${weather.base-url}") String weatherBaseUrl,
            @Value("${weather.api-key}") String apiKey
    ) {
        this.restClient = restClientBuilder.baseUrl(weatherBaseUrl).build();
        this.apiKey = apiKey;
    }

    @Override
    public WeatherDetails fetchForCity(String city) {
        JsonNode response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/data/2.5/weather")
                        .queryParam("q", city)
                        .queryParam("units", "metric")
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        if (response == null) {
            throw new IllegalStateException("Weather service returned no payload.");
        }

        double temperature = response.path("main").path("temp").asDouble(Double.NaN);
        JsonNode weatherArray = response.path("weather");
        String description = weatherArray.isArray() && !weatherArray.isEmpty()
                ? weatherArray.get(0).path("description").asText("")
                : "";

        if (Double.isNaN(temperature) || description.isBlank()) {
            throw new IllegalStateException("Weather service payload is incomplete.");
        }

        return new WeatherDetails(temperature, description);
    }
}
