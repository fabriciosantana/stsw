package br.edu.idp.es.stsw.pyramid.e2e;

import br.edu.idp.es.stsw.pyramid.domain.StudyTopic;
import br.edu.idp.es.stsw.pyramid.repository.StudyTopicRepository;
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
class StudyFlowE2ETest {

    @RegisterExtension
    static WireMockExtension externalApi = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @LocalServerPort
    private int port;

    @Autowired
    private StudyTopicRepository studyTopicRepository;

    @DynamicPropertySource
    static void configureExternalApi(DynamicPropertyRegistry registry) {
        registry.add("external.base-url", externalApi::baseUrl);
    }

    @BeforeEach
    void setUp() {
        studyTopicRepository.deleteAll();
        studyTopicRepository.save(new StudyTopic("JUnit", "Framework for automated tests"));
        externalApi.resetAll();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldServeMainStudyFlow() {
        externalApi.stubFor(get(urlPathEqualTo("/tip"))
                .willReturn(okJson("""
                        {
                          "message": "Run your tests before pushing."
                        }
                        """)));

        RestAssured
                .given()
                .when()
                .get("/status")
                .then()
                .statusCode(200)
                .body(equalTo("Assignment 05 online"));

        RestAssured
                .given()
                .when()
                .get("/topics/JUnit")
                .then()
                .statusCode(200)
                .body(equalTo("Topic JUnit: Framework for automated tests"));

        RestAssured
                .given()
                .when()
                .get("/tip")
                .then()
                .statusCode(200)
                .body(equalTo("Daily tip: Run your tests before pushing."));
    }
}
