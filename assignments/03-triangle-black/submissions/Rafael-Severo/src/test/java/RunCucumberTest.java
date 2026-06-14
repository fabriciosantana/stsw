import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources", // Onde estão os arquivos .feature
    glue = "",                       // Onde estão os arquivos de Steps (deixe vazio se estiver na mesma pasta raiz)
    plugin = {"pretty", "html:target/relatorio-cucumber.html"} // Gera um relatório visual bonito
)
public class RunCucumberTest {
    // Essa classe fica vazia mesmo!
}
