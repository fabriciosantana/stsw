# 05-pyramid — The Practical Test Pyramid (guilherme-portas)

Execução do tutorial [The Practical Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html) (Ham Vocke / Martin Fowler) e do repositório [hamvocke/spring-testing](https://github.com/hamvocke/spring-testing).

Código de produção e de teste foi mantido o mais fiel possível ao artigo/site, com adaptações mínimas para Maven + Java 21 + ambiente offline (H2) documentadas abaixo.

## Estrutura

```
pom.xml (Spring Boot 3.4.5, Java 21 — tradução do build.gradle original)
src/main/java/example/
  ExampleApplication.java   # @SpringBootApplication + @Bean RestTemplate (igual ao site)
  ExampleController.java    # GET /hello, /hello/{lastName}, /weather (igual ao site)
  person/Person.java        # @Entity (igual ao site)
  person/PersonRepository.java # extends CrudRepository + findByLastName (igual ao site)
  weather/WeatherClient.java   # @Value ${weather.url} + ${weather.api_secret} (igual ao site)
  weather/WeatherResponse.java # @JsonIgnoreProperties + getSummary() (igual ao site)
src/main/resources/application.properties # Postgres prod (igual ao site)
src/test/resources/
  application.properties    # weather.url=http://localhost:8089 + H2 (2 linhas originais preservadas)
  weatherApiResponse.json   # stub da API de clima (igual ao site)
pacts/person_consumer-person_provider.json # contrato de exemplo (igual ao target/pacts do site)
```

## Pirâmide implementada (artigo → teste)

| Nível | Teste | Técnica do artigo |
|---|---|---|
| Unit (base) | `ExampleControllerTest` (5 testes, Mockito stubando `PersonRepository`/`WeatherClient`, arrange/act/assert) | “Implementing a Unit Test” — `given(...).willReturn(...)`, `assertThat(greeting, is(...))` |
| Unit | `weather/WeatherClientTest` (mock `RestTemplate`, caso sucesso + `RestClientException` → `Optional.empty`) | Sociable/solitary, stubbing |
| Unit | `weather/WeatherResponseTest` (Jackson deserializa `weatherApiResponse.json`) | Serialização na borda |
| Unit/integração leve | `ExampleControllerAPITest` (`@WebMvcTest` + `MockMvc`, sem subir servidor) | Sidebar “Specialised Test Helpers — MockMVC” |
| Integração DB | `person/PersonRepositoryIntegrationTest` (`@DataJpaTest`, save + findByLastName + deleteAll) | “Database Integration” |
| Integração serviço | `weather/WeatherClientIntegrationTest` (`@SpringBootTest` + WireMock em 8089, stub `GET /data/2.5/weather...` com `weatherApiResponse.json`) | “Integration With Separate Services” |
| Contrato consumer | `weather/WeatherClientConsumerTest` (Pact `given("weather forecast data")`, gera `target/pacts/*.json`) | “Consumer Test (our team)” |
| Contrato provider | `ExampleProviderTest` (`@Provider("person_provider")`, `@State("person data")`, MockMvcTarget) | “Provider Test (our team)” |
| E2E REST | `HelloE2ERestTest` (sobe app `RANDOM_PORT`, REST-assured `GET /hello/Pan`) | “REST API End-to-End Test” |
| E2E UI | `HelloE2ESeleniumTest` (Chrome headless → `/hello` contém “Hello World!”) | “User Interface End-to-End Test” |
| Aceitação | `weather/WeatherAcceptanceTest` (WireMock + REST-assured em `/weather`) | “Acceptance Tests” |

Total: **19 testes, todos verdes** (`mvn test`).

## Como rodar

```bash
cd assignments/05-pyramid/submissions/guilherme-portas
mvn test
# E2E Selenium isolado:
mvn test -Dtest=HelloE2ESeleniumTest
# Rodar app (precisa Postgres em 15432 ou ajuste p/ H2 + WEATHER_API_KEY):
export WEATHER_API_KEY=dummy
mvn spring-boot:run
curl http://localhost:8080/hello
```

Produção usa PostgreSQL (`startDatabase.sh` original / `docker run ... postgres:15432`); testes usam H2 em memória, WireMock em 8089 e pact em `./pacts/`, sem rede/chave real.

## Desvios do original (justificados)

1. **Gradle → Maven:** `build.gradle` traduzido para `pom.xml` (mesmas libs: `spring-boot-starter-web`, `data-jpa`, `h2`, `postgresql`, `spring-boot-starter-test`, `rest-assured:5.5.0`, `wiremock-standalone:3.3.1`, `selenium-java`, `pact consumer:junit5:4.6.17`, `provider:junit5spring:4.6.17`, `webdrivermanager:5.9.2`).
2. **Spring Boot 3.5.6/Gradle + Java 25 → 3.4.5/Maven + Java 21:** `@MockitoBean` só existe a partir do Boot 3.4; ambiente da disciplina usa JDK 21.
3. **Teste `src/test/resources/application.properties`:** 2 linhas originais (`weather.url`, `weather.api_secret`) preservadas; adicionado bloco H2 + `generate-unique-name=true` para `mvn test` offline sem Postgres/Docker.
4. **`ExampleProviderTest`: `@PactFolder("target/pacts")` → `"pacts"`:** `target/` é saída de build no Maven; pact de exemplo versionado em `./pacts/` (mesmo JSON do site). `DEFINED_PORT` → `MOCK` (porta 8080 ocupada na máquina; verificação usa MockMvc, não precisa de servidor real).
5. **`HelloE2ESeleniumTest`:** mantido `WebDriverManager` + `ChromeDriver` + `--headless=new` do site; adicionado `--no-sandbox --disable-dev-shm-usage`, `setBinary("/usr/bin/chromium")` e fallback para `/usr/bin/chromedriver` (mismatch 154 vs 153 neste host).
6. **API de clima:** `openweathermap.org` (o `darksky.net` do artigo foi descontinuado); testes usam stub fixo, sem chave.

## Referências

- Artigo: https://martinfowler.com/articles/practical-test-pyramid.html
- Código-base: https://github.com/hamvocke/spring-testing (clonado em 04/10/2026; arquivos copiados verbatim exceto 4 ajustes acima)
