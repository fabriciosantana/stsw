package br.edu.idp.es.stsw.bva.runners;

import io.cucumber.core.cli.Main;

public class RunCucumberWithCli {

    public static void main(String[] args) {
        Main.main(new String[] {
                "--glue", "br.edu.idp.es.stsw.bva.steps",
                "--plugin", "pretty",
                "--plugin", "html:target/site/cucumber-reports/CucumberCli.html",
                "src/test/resources/features"
        });
    }
}
