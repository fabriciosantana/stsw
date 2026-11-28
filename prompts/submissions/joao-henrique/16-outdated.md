# Prompt 16: OWASP Top Ten: Vulnerable and Outdated Components (A06:2021)

Referências: [OWASP A06:2021](https://owasp.org/Top10/A06_2021-Vulnerable_and_Outdated_Components/), [CWE-1395](https://cwe.mitre.org/data/definitions/1395.html)

> Nota: os comandos de Trivy e Dependency-Check abaixo são o procedimento a executar; a saída mostrada nos exemplos é ilustrativa (formato típico), não uma captura real do Juice Shop.

## 1. Introdução

A06 trata do uso de componentes (bibliotecas, frameworks, runtimes, servidores, plugins) com vulnerabilidades conhecidas, sem suporte ou desatualizados.

- **Por que continua no Top 10:** aplicações modernas têm centenas de dependências transitivas (a maior parte do código é de terceiros), atualizar dá trabalho e pode quebrar compatibilidade, e muitas equipes não sabem exatamente o que usam. Subiu da 9ª (2017) para a 6ª posição.
- **Diferença para Security Misconfiguration:** em A05 o componente é seguro, mas *configurado* de forma errada; em A06 o próprio componente tem uma falha (CVE) ou está sem suporte, mesmo que configurado corretamente.
- **Relação com dependências:** cada biblioteca herda confiança total no processo da aplicação. Uma falha em uma dependência transitiva (que o time nem escolheu) compromete o sistema inteiro.
- **Exemplos reais:** **Log4Shell** (CVE-2021-44228, Log4j 2: execução remota de código por JNDI em mensagens de log); **Equifax 2017** (Apache Struts CVE-2017-5638 sem patch, 147 milhões de pessoas afetadas); **Heartbleed** (OpenSSL, 2014); **Spring4Shell** (2022).
- **Papel dos repositórios (Maven, PyPI, npm):** facilitam reutilizar código, mas também espalham a falha rapidamente (uma biblioteca vulnerável é puxada por milhares de projetos) e abrem espaço para pacotes maliciosos/typosquatting. A confiança no repositório não garante a segurança do pacote.

## 2. Riscos e impactos

- **Riscos:** execução remota de código, injeção, vazamento de dados, negação de serviço, escalada de privilégios, backdoors; ausência de patches para componentes sem manutenção; problemas de conformidade.
- **Exposição de dados ou negação de serviço:** uma biblioteca de parsing vulnerável pode ler dados fora do limite (vazamento, como o Heartbleed) ou entrar em loop/consumir memória com uma entrada especial (DoS).
- **Sistemas críticos/expostos à internet:** o risco é maior porque o atacante só precisa varrer a internet atrás da versão vulnerável, e a exploração costuma ser automática e em massa poucas horas depois do anúncio da CVE.
- **Por que testes manuais deixam passar:** a falha está em código de terceiros, que não é lido nem testado; a aplicação "funciona normal"; a vulnerabilidade só é ativada por uma entrada específica; muitas dependências são transitivas e invisíveis.
- **CWE:** A06 se relaciona com CWE-1104 (uso de componentes de terceiros sem manutenção) e **CWE-1395**: *Dependency on Vulnerable Third-Party Component* — o produto depende de um componente de terceiros que contém vulnerabilidade(s) conhecida(s).
- **SBOM (Software Bill of Materials):** inventário de todos os componentes e versões de um software (formatos CycloneDX e SPDX). Quando uma nova CVE é publicada, permite responder rapidamente "estamos afetados?" e aplicar a correção, além de dar transparência na cadeia de suprimento.

## 3. Detecção e monitoramento

- **Scanners de dependência (SCA):** comparam as dependências do projeto com bases de vulnerabilidades (NVD, GitHub Advisory, OSV).
  - **OWASP Dependency-Check:** gratuito, identifica bibliotecas (por evidências como nome/versão do JAR, hash) e as relaciona a CPEs/CVEs do NVD; relatórios em HTML/JSON/XML; plugin para Maven/Gradle/CI.
  - **Snyk:** SCA comercial com plano gratuito; sugere versão corrigida, abre pull requests de correção e monitora continuamente.
  - **Trivy (Aqua):** scanner open source para imagens de container, sistema de arquivos, repositórios Git, IaC e SBOM; rápido e fácil de colocar em CI.
- **Trivy x Dependency-Check:** o Trivy cobre mais alvos (imagens, SO, IaC, segredos), é rápido, sem muita configuração, e usa bases como GitHub Advisory/OSV. O Dependency-Check é focado em dependências de aplicação (principalmente Java/.NET), depende do NVD (baixar o banco é demorado e pode gerar mais falsos positivos por casamento de CPE), mas é maduro e integra bem com Maven.
- **Como o Dependency-Check funciona:** (1) analisadores varrem os arquivos e coletam evidências (nome, versão, fornecedor, hash); (2) mapeia para identificadores CPE/Package URL; (3) consulta o banco local do NVD e lista as CVEs; (4) mostra a severidade (CVSS) e gera o relatório. Falha o build se passar um limite (`failBuildOnCVSS`).
- **CVSS:** nota de 0,0 a 10,0 para a gravidade técnica de uma vulnerabilidade, calculada por métricas (vetor de ataque, complexidade, privilégios, interação, impacto em CIA). 0,1–3,9 baixa; 4,0–6,9 média; 7,0–8,9 alta; 9,0–10,0 crítica (Log4Shell = 10,0). Ajuda a priorizar, mas deve ser combinada com o contexto (se o código vulnerável é de fato alcançável e se existe exploit conhecido, p.ex. EPSS/KEV).
- **Exemplo Java com dependência vulnerável (`pom.xml`):**

```xml
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-core</artifactId>
  <version>2.14.1</version>   <!-- vulnerável ao Log4Shell -->
</dependency>
```

- **Execução:**

```bash
mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=7
```

- **Interpretação do relatório (exemplo ilustrativo):** `log4j-core-2.14.1.jar` → CVE-2021-44228 (CVSS 10.0, crítica), CVE-2021-45046 e outras. Ações: atualizar para uma versão corrigida (2.17.1 ou superior; hoje a versão atual da linha 2.x), verificar dependências transitivas (`mvn dependency:tree`), reexecutar a análise e, se não for possível atualizar de imediato, aplicar mitigação (desabilitar lookups de mensagens) e documentar a exceção.

## 4. Prática com ferramentas reais (Juice Shop)

```bash
# código-fonte
git clone https://github.com/juice-shop/juice-shop && cd juice-shop

# OWASP Dependency-Check (CLI)
dependency-check.sh --project juice-shop --scan . --format HTML --out ./relatorio-dc
#  (opcional: --nvdApiKey <chave> acelera a atualização do banco de dados)

# Trivy: dependências do repositório e imagem Docker
trivy fs --scanners vuln .
trivy image bkimminich/juice-shop
```

O Juice Shop inclui de propósito dependências vulneráveis (é um alvo de treino), então os dois scanners devem listar vulnerabilidades de severidade alta/crítica em pacotes npm (ex.: bibliotecas antigas de JWT, `sanitize-html`, `express-jwt`) e, no Trivy, também do sistema base da imagem. Os desafios "Vulnerable Library" e "Typosquatting" do próprio Juice Shop tratam desse tema.

## 5. Aplicação em cenário real: sistema bancário legado

- **Passos práticos:**
  1. Fazer o inventário/SBOM de todas as dependências, inclusive transitivas e de runtime (JVM, servidor de aplicação).
  2. Rodar SCA (Dependency-Check/Trivy) e priorizar por CVSS, exposição à internet e existência de exploit.
  3. Atualizar as dependências com correção; testar com a suíte de regressão.
  4. Para as que não têm atualização: substituir, isolar (rede/containers), aplicar mitigações (WAF virtual patching, desativar recursos vulneráveis) e documentar a aceitação do risco.
  5. Monitorar continuamente novas CVEs.
- **Riscos legais e de conformidade:** violações da LGPD (falha no dever de segurança), PCI-DSS (exige correções de segurança e inventário), regulação do Banco Central/Resolução CMN 4.893 (política de segurança cibernética), multas, responsabilidade civil e danos de reputação.
- **Política no pipeline CI/CD:**
  - A cada pull request: SCA (Dependency-Check/Trivy/Dependabot) com falha do build para CVSS ≥ 7 (ou "crítica" sem exceção aprovada).
  - Geração de SBOM a cada build, armazenada com o artefato.
  - Dependabot/Renovate para abrir PRs de atualização semanais.
  - Varredura noturna da produção/imagens, pois novas CVEs surgem depois do deploy.
  - Prazo (SLA) de correção por severidade: crítica 7 dias, alta 30, média 90.
  - Versões fixadas (lockfile) e só repositórios aprovados.

## 6. Conclusão

- **Vulnerabilidade conhecida x zero-day:** a conhecida já tem CVE publicada (e geralmente correção); zero-day é uma falha desconhecida do fornecedor, sem correção, já explorada ou prestes a ser. A maior parte dos ataques em larga escala explora falhas *conhecidas* com patch disponível, por isso a atualização contínua é tão eficaz.
- **Como manter versões seguras:** inventário/SBOM, SCA automático no CI e em produção, atualizações frequentes e pequenas (Dependabot/Renovate), reduzir o número de dependências, preferir componentes mantidos e de fontes confiáveis, travar versões com lockfile e verificar integridade, ter plano de resposta rápida para CVEs críticas e testes automatizados que deem confiança para atualizar.
