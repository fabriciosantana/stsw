package br.edu.idp.es.stsw.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PUBLISH_ENABLED_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.edu.idp.es.stsw.hellocucumber.steps, br.edu.idp.es.stsw.steps")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/Cucumber.html")
@ConfigurationParameter(key = PLUGIN_PUBLISH_ENABLED_PROPERTY_NAME, value = "true") // necessário configurar a variável
                                                                                    // de ambiente
                                                                                    // CUCUMBER_PUBLISH_TOKEN gerada em
                                                                                    // https://reports.cucumber.io
public class RunCucumberWithJunitTest {

    public static void main(String[] args) {
        System.out.println("Stating runner with Junit...");
    }

}