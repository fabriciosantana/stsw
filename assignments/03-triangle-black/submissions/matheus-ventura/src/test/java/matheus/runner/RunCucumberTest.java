package matheus.runner;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "matheus.steps",
    plugin = {
        "pretty",
        "html:target/cucumber-reports.html",
        "json:target/cucumber-reports.json"
    },
    monochrome = true,
    stepNotifications = true
)
public class RunCucumberTest {
    // Esta classe permanece vazia
    // O Cucumber usa as anotações @RunWith e @CucumberOptions para executar os testes
}