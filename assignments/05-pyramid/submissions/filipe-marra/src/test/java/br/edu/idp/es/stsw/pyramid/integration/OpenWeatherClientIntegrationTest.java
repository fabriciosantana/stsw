package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.client.OpenWeatherClient;
import br.edu.idp.es.stsw.pyramid.domain.WeatherDetails;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.web.client.RestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenWeatherClientIntegrationTest {

    @RegisterExtension
    static WireMockExtension weatherApi = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Test
    void shouldCallWeatherApiAndParseResponse() {
        weatherApi.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .willReturn(okJson("""
                        {
                          "main": { "temp": 21.2 },
                          "weather": [ { "description": "broken clouds" } ]
                        }
                        """)));

        OpenWeatherClient client = new OpenWeatherClient(RestClient.builder(), weatherApi.baseUrl(), "test-key");
        WeatherDetails weather = client.fetchForCity("Hamburg,de");

        assertThat(weather.temperatureCelsius()).isEqualTo(21.2);
        assertThat(weather.description()).isEqualTo("broken clouds");
    }

    @Test
    void shouldFailWhenPayloadIsIncomplete() {
        weatherApi.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .willReturn(okJson("""
                        {
                          "main": { "temp": 21.2 },
                          "weather": []
                        }
                        """)));

        OpenWeatherClient client = new OpenWeatherClient(RestClient.builder(), weatherApi.baseUrl(), "test-key");

        assertThatThrownBy(() -> client.fetchForCity("Hamburg,de"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Weather service payload is incomplete.");
    }

    @Test
    void shouldFailWhenApiReturnsNoBody() {
        weatherApi.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .willReturn(okJson("")));

        OpenWeatherClient client = new OpenWeatherClient(RestClient.builder(), weatherApi.baseUrl(), "test-key");

        assertThatThrownBy(() -> client.fetchForCity("Hamburg,de"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Weather service returned no payload.");
    }
}
