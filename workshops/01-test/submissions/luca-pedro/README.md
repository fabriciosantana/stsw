#  Selenium 4 - Framework de Automação de Testes Web

**Apresentação:** Outubro/2025  
**Alunos:** Luca Lenzoni e Pedro Jersey  
**Tecnologia:** Selenium 4

---

##  Introdução ao Selenium

O **Selenium** é um dos frameworks **open-source** mais utilizados no mundo para a **automação de testes em aplicações web**. Criado em 2004, ele permite simular interações reais do usuário com navegadores.

* **Foco Principal:** Testes de **Caixa-Preta**, validando funcionalidades a partir da visão do usuário final.  
* **Posição na Pirâmide de Testes:** Opera no nível de **Interface (UI)**, sendo fundamental para validar **fluxos de ponta a ponta** e complementar testes de unidade e API.

---

##  Principais Recursos

O Selenium é conhecido por sua maturidade e flexibilidade:

*  **Compatibilidade Ampla:** Suporte a diversos navegadores (**Chrome, Firefox, Edge, Safari**) e múltiplas linguagens (**Java, Python, C#, Ruby**, etc.).  
*  **Integração com Frameworks:** Fácil integração com ferramentas de teste como **JUnit 5, TestNG** e **PyTest**.  
*  **Execução Distribuída:** Capacidade de rodar testes em paralelo via **Selenium Grid**, otimizando o tempo.  
*  **CI/CD:** Perfeita integração em pipelines de **Integração Contínua e Entrega Contínua** (Jenkins, GitHub Actions, GitLab CI).  

---

##  Análise: Vantagens e Desvantagens

###  Vantagens (Por que usar o Selenium?)
- **Padrão de Mercado:** Framework maduro, confiável e considerado um standard global.  
- **Comunidade:** Enorme base de usuários e documentação consolidada.  
- **Compatibilidade:** Suporte amplo a navegadores e várias linguagens de programação.  
- **Integração CI/CD:** Excelente capacidade de integração em pipelines modernos.  

###  Desvantagens (Pontos de Atenção)
- **Configuração Inicial:** Setup pode ser complexo (drivers, dependências).  
- **Curva de Aprendizado:** Maior em comparação a ferramentas mais novas (Playwright, Cypress).  
- **Fragilidade:** Testes de UI são mais lentos e propensos a falhas intermitentes (flaky tests).  

---

##  Frameworks Similares
- **Playwright (Microsoft):** Moderno, cross-browser e fácil setup.  
- **Cypress:** Foco em JavaScript/Frontend, execução rápida no browser.  
- **Puppeteer (Google):** Focado em testes headless e web scraping.  
- **TestCafé:** Alternativa simples, com menos dependência de drivers externos.  

---

##  Conclusão

O **Selenium 4** é a escolha ideal para projetos que precisam de um framework robusto e maduro para automação de testes de interface.  

-  **Recomendado quando:** há necessidade de **cross-browser testing**, integração com **CI/CD** e cobertura de interface em projetos grandes.  
-  **Não recomendado quando:** a prioridade são **testes rápidos e simples de frontend**, onde ferramentas como **Cypress** ou **Playwright** podem ser mais ágeis.  

---

##  Demonstração Prática

O projeto `selenium-demo` ilustra uma implementação prática de automação de login.

**Implementação:** **Login em HTML** + testes automatizados com **JUnit 5** e **Selenium 4**.

### Casos de Teste Cobertos:
*  Login válido  
*  Login inválido  
*  Senha incorreta  
*  Campos vazios  
*  Entradas longas (stress test)  

---

##  Como Executar o Exemplo

Para rodar os testes localmente usando **Maven** (ambiente Linux/WSL):

```bash
# 1) Instalar Java (OpenJDK 21) e Maven
sudo apt update
sudo apt install openjdk-21-jdk maven -y

# 2) Garantir que o Google Chrome/Chromium esteja instalado
google-chrome --version
# Ou instale, se necessário:
sudo apt install chromium-browser -y

# 3) Navegar para o diretório do projeto e compilar
cd localDaPasta
mvn clean compile

# 4) Rodar os testes
mvn test
