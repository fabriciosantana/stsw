package br.edu.idp.es.stsw.pyramid.e2e;

import br.edu.idp.es.stsw.pyramid.domain.Person;
import br.edu.idp.es.stsw.pyramid.repository.PersonRepository;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PracticalTestPyramidE2ETest {

    @RegisterExtension
    static WireMockExtension weatherApi = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @LocalServerPort
    private int port;

    @Autowired
    private PersonRepository personRepository;

    @DynamicPropertySource
    static void configureWeatherApi(DynamicPropertyRegistry registry) {
        registry.add("weather.base-url", weatherApi::baseUrl);
        registry.add("weather.api-key", () -> "e2e-test-key");
    }

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        personRepository.save(new Person("Ham", "Vocke"));
        weatherApi.resetAll();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldServeCriticalUserJourney() {
        weatherApi.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .willReturn(okJson("""
                        {
                          "main": { "temp": 19.0 },
                          "weather": [ { "description": "light rain" } ]
                        }
                        """)));

        RestAssured
                .given()
                .when()
                .get("/hello/Vocke")
                .then()
                .statusCode(200)
                .body(equalTo("Hello Ham Vocke!"));

        RestAssured
                .given()
                .when()
                .get("/weather")
                .then()
                .statusCode(200)
                .body(equalTo("Current weather in Hamburg: 19.0°C, light rain."));
    }
}
