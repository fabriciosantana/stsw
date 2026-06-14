package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.client.HttpExternalClient;
import br.edu.idp.es.stsw.pyramid.domain.TipResponse;
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

class HttpExternalClientIntegrationTest {

    @RegisterExtension
    static WireMockExtension externalApi = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Test
    void shouldCallExternalApiAndParseResponse() {
        externalApi.stubFor(get(urlPathEqualTo("/tip"))
                .willReturn(okJson("""
                        {
                          "message": "Practice with small incremental commits."
                        }
                        """)));

        HttpExternalClient client = new HttpExternalClient(RestClient.builder(), externalApi.baseUrl());
        TipResponse response = client.fetchTip();

        assertThat(response.message()).isEqualTo("Practice with small incremental commits.");
    }

    @Test
    void shouldFailWhenPayloadIsIncomplete() {
        externalApi.stubFor(get(urlPathEqualTo("/tip"))
                .willReturn(okJson("""
                        {
                          "message": ""
                        }
                        """)));

        HttpExternalClient client = new HttpExternalClient(RestClient.builder(), externalApi.baseUrl());

        assertThatThrownBy(client::fetchTip)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("External service payload is incomplete.");
    }

    @Test
    void shouldFailWhenApiReturnsNoBody() {
        externalApi.stubFor(get(urlPathEqualTo("/tip"))
                .willReturn(okJson("")));

        HttpExternalClient client = new HttpExternalClient(RestClient.builder(), externalApi.baseUrl());

        assertThatThrownBy(client::fetchTip)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("External service returned no payload.");
    }
}
