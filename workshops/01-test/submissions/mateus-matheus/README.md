### Projeto de Testes com Serenity BDD (UI Login)

**O que é Serenity BDD (rápido e direto):**
- **Serenity BDD** é um framework de testes que estende JUnit e integra WebDriver para testes de UI, gerando um **relatório vivo** (Living Documentation) com screenshots, passos (Given/When/Then) e rastreabilidade dos cenários.
- Aqui ele é usado com **JUnit 5** e o padrão **Steps + Page Objects**.

### Stack
- Java 11
- Maven
- Serenity BDD 4.x (core, junit5, screenplay)
- AssertJ
- Navegador: Chrome (padrão)

### Estrutura relevante
- `src/main/java/pages/LoginPage.java`: Page Object (mapeamento e ações da tela de login)
- `src/main/java/steps/LoginSteps.java`: Steps reutilizáveis (anotados com `@Step`) que descrevem as ações BDD
- `src/test/java/LoginTest.java`: cenário feliz (login com sucesso)
- `src/test/java/LoginNegativeTest.java`: cenários negativos (credenciais inválidas, campos vazios, usuário bloqueado)
- `src/test/java/LoginMoreTests.java`: variações (performance, problemático, etc.)
- `src/test/java/LoginEdgeCasesTest.java`: casos de borda (alguns testes intencionalmente falhando para demonstrar relatório)
- `pom.xml`: dependências Serenity e plugin que agrega o relatório na fase `verify`

### Como os testes estão organizados
- Os testes usam `@ExtendWith(SerenityJUnit5Extension.class)` e chamam métodos de `LoginSteps` que, por sua vez, usam o `LoginPage` (Page Object) para interagir com a UI.
- Padrão **BDD**: Given/When/Then refletido nos nomes de métodos anotados com `@Step` (ex.: "Abrir a página de login", "Informar credenciais", "Confirmar login").
- As asserções são feitas com **AssertJ**.

### Pré‑requisitos (Windows PowerShell)
- Java 11+ (`java -version`)
- Maven 3.8+ (`mvn -v`)
- Google Chrome instalado

Opcional (se precisar apontar driver manualmente):
- Baixe o ChromeDriver compatível e exporte a variável antes de rodar:
```powershell
$env:webdriver.chrome.driver="C:\\caminho\\para\\chromedriver.exe"
```
Obs.: Versões recentes do Serenity costumam usar WebDriverManager embutido. Se o download automático do driver falhar, use a variável acima.

### Como executar todos os testes
No diretório do projeto:
```powershell
mvn clean verify -Dwebdriver.driver=chrome
```

Execução headless (CI ou sem abrir janela):
```powershell
mvn clean verify -Dwebdriver.driver=chrome -Dwebdriver.chrome.headless=true
```

Executar um teste específico (ex.: `LoginTest`):
```powershell
mvn -Dtest=LoginTest clean verify -Dwebdriver.driver=chrome
```

Filtrar por método (ex.: método `usuarioConsegueFazerLogin` dentro de `LoginTest`):
```powershell
mvn -Dtest=LoginTest#usuarioConsegueFazerLogin clean verify -Dwebdriver.driver=chrome
```

### Como abrir o relatório do Serenity
Após o `mvn verify`, o relatório é gerado em:
- `target/site/serenity/index.html`

Abrir direto pelo Explorer:
```powershell
ii .\target\site\serenity\index.html
```

Se preferir via navegador específico (ex.: Chrome):
```powershell
start chrome .\target\site\serenity\index.html
```

### Dicas rápidas
- Se algum teste falhar propositalmente (há casos de demonstração), o build continua porque `pom.xml` usa `testFailureIgnore=true`. Mesmo assim o **relatório** é gerado com os detalhes dos passos e evidências.
- Se a URL base da aplicação não abrir, garanta que o `PageObject.open()` aponte corretamente via `serenity.conf` (arquivo padrão do Serenity) ou propriedade `webdriver.base.url`. Exemplo ad‑hoc:
```powershell
mvn clean verify -Dwebdriver.driver=chrome -Dwebdriver.base.url=https://www.saucedemo.com/
```

### Problemas comuns
- Erro de driver: ajuste `webdriver.chrome.driver` ou rode headless.
- Versão do Java: use Java 11 conforme `pom.xml`.
- Report não abriu: confirme a pasta `target/site/serenity/` e abra `index.html`.

---

Qualquer ajuste fino (ex.: apontar `webdriver.base.url`) pode ser feito sem alterar código, só via propriedades de linha de comando.


