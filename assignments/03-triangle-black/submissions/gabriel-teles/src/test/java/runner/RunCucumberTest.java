package runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.core.cli.Main;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@SelectClasspathResource("features") // pasta onde estão os .feature
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "steps") // pacote dos steps
public class RunCucumberTest {
    public static void main(String[] args) {
        Main.main(new String[]{
            "-g", "steps",
            "src/test/resources/features"
        });
    }
}
