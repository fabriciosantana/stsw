# Prompt 17: OWASP Top Ten: Software and Data Integrity Failures (A08:2021)

Referência: [OWASP A08:2021](https://owasp.org/Top10/A08_2021-Software_and_Data_Integrity_Failures/)

> Nota: comandos e workflows abaixo são exemplos de configuração; não foram executados contra um projeto real neste documento.

## 1. Introdução

A08 é sobre código e infraestrutura que não protegem contra violações de integridade: atualizações, bibliotecas, plugins, pipelines CI/CD e dados serializados aceitos sem verificar origem e integridade.

- **Principais problemas:** usar bibliotecas/plugins/CDNs de fontes não confiáveis; atualizações automáticas sem assinatura; pipeline CI/CD sem controle de acesso; dados serializados aceitos do cliente sem verificação.
- **Por que a falta de verificação é um risco:** se o sistema confia em qualquer artefato "que chegou", o atacante que controla um pacote, um servidor de atualização ou um repositório injeta código malicioso que roda com os privilégios da aplicação (ou do pipeline).
- **Ataques relacionados:** *CI/CD pipeline attacks* (alterar o processo de build para inserir um backdoor); *dependency confusion* (publicar no repositório público um pacote com o mesmo nome de um pacote interno e versão maior, e o gerenciador o baixa); *insecure deserialization* (objetos manipulados levam a execução de código ou alteração de lógica).
- **Carregar sem verificar origem/integridade:** é baixar e executar/usar código ou dados sem conferir assinatura, hash ou procedência (por exemplo, `<script src="http://cdn..."> ` sem SRI, `curl | bash`, atualização via HTTP).
- **Desserialização de objetos não confiáveis:** pode causar execução remota de código (RCE), manipulação de objetos/estado (por exemplo, trocar o papel do usuário no cookie serializado), injeção e negação de serviço.
- **Sistemas mais críticos:** pipelines de CI/CD (acesso a segredos e a produção), containers/imagens (muitas camadas de terceiros), nuvem/serverless e qualquer sistema com atualização automática, pois um único ponto comprometido atinge muitos destinos.

## 2. Impactos

- **Comprometer dados por meio de bibliotecas externas:** uma biblioteca maliciosa ou comprometida roda dentro do processo, lê e altera dados, envia segredos para fora e modifica regras de negócio sem que ninguém perceba.
- **Integridade x confidencialidade:** confidencialidade = impedir que pessoas não autorizadas *leiam* a informação; integridade = garantir que a informação/código não foi *alterado* indevidamente. Uma falha de integridade pode não vazar nada, mas faz o sistema aceitar dados ou executar código falso.
- **Código malicioso via atualização automática:** o atacante ganha execução em todas as máquinas que atualizam, com os privilégios do software (cadeia de suprimento, como no SolarWinds).
- **Relação com os princípios:** quebra a integridade (dados/código alterados), pode afetar a disponibilidade (sabotagem, ransomware) e a confiabilidade/confiança no sistema e no fornecedor.
- **Prejuízos:** custo de resposta e recuperação, interrupção do negócio, multas e processos, perda de clientes e dano à reputação; em ataques de cadeia de suprimento, os clientes do fornecedor também são atingidos.

## 3. Exemplos reais

- **Event-Stream (npm, 2018):** o mantenedor original, sem tempo, passou o projeto a um novo "colaborador" que adicionou a dependência `flatmap-stream`, contendo código malicioso criptografado que roubava carteiras de bitcoin (Copay). Vetor: engenharia social sobre o mantenedor + dependência transitiva que ninguém revisou.
- **SolarWinds Orion (2020):** invasores entraram no ambiente de build e inseriram um backdoor (SUNBURST) no código-fonte/compilação; a atualização legítima, assinada digitalmente, foi distribuída a cerca de 18 mil clientes (incluindo órgãos governamentais dos EUA). A assinatura garantia que veio da SolarWinds, mas não que o build não havia sido adulterado: falha de integridade no processo de build e distribuição.
- **Dependency confusion (2021):** o pesquisador Alex Birsan publicou pacotes públicos com os mesmos nomes de pacotes internos usados por Apple, Microsoft, PayPal e outras; os gerenciadores (npm, pip, RubyGems) preferiram a versão pública mais alta e executaram o código. Mostrou o problema da resolução de nomes entre repositórios privados e públicos.
- **CI/CD e backdoors:** um pipeline com tokens amplos, runners compartilhados, dependências de ações (`uses: x/y@main`) não fixadas ou scripts baixados e executados permitem a um atacante alterar artefatos ou obter segredos (como no incidente do Codecov em 2021, em que o script de upload foi adulterado).
- **Erros cometidos:** falta de revisão de código e de dependências, confiança em mantenedores/pacotes sem verificação, ausência de proteção do ambiente de build, tokens/permissões excessivos, ausência de verificação de hashes/assinaturas e de separação de repositórios internos e públicos, e detecção tardia.

## 4. Prevenção e boas práticas

- **Assinatura digital:** o autor assina o pacote/binário com sua chave privada; quem recebe confere com a chave pública, garantindo origem e que o conteúdo não foi alterado após a assinatura (ex.: Sigstore/cosign, GPG, assinatura de commits).
- **Ferramentas e frameworks:** o OWASP Dependency-Check identifica componentes vulneráveis; **SLSA** (Supply-chain Levels for Software Artifacts) define níveis de maturidade do processo de build (build isolado, reproduzível, com proveniência); **provenance metadata** (atestados) registra onde, quando e como o artefato foi construído, permitindo verificar se corresponde ao esperado.
- **Restringir plugins/bibliotecas desconhecidas:** cada dependência é código de terceiros executado com os seus privilégios; limitar a lista aprovada reduz a superfície de ataque.
- **Políticas de segurança no pipeline:** branch protection e revisão obrigatória, commits assinados, menor privilégio em tokens e `permissions:` mínimas, segredos em cofre, ações fixadas por hash (SHA), runners efêmeros, escaneamento de dependências e de IaC, aprovações manuais para produção, logs de auditoria.
- **Controle de versões e dependências:** o Git dá histórico e rastreabilidade; lockfiles (`package-lock.json`, `pom.xml` com versões fixas/`yarn.lock`) com hashes garantem que sempre se instala a mesma versão verificada; registro/proxy interno com *namespaces* reservados evita dependency confusion.

## 5. Prática com ferramentas reais

- **Dependency-Check em um projeto Java:** `mvn org.owasp:dependency-check-maven:check`. Costuma detectar bibliotecas com CVEs conhecidas (ex.: `commons-collections` 3.2.1 — gadget de desserialização; `jackson-databind` antigo; `log4j-core` 2.14).
- **Trivy/Snyk em Node.js/Python/containers:** `trivy fs .` (lê `package-lock.json`, `requirements.txt`), `trivy image minha-imagem:tag`, `snyk test`.
- **Pipeline com GitHub Actions e verificação de dependências:**

```yaml
name: ci
on: [pull_request]
permissions:
  contents: read
jobs:
  seguranca:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4          # em produção, fixar pelo SHA do commit
      - uses: actions/setup-node@v4
        with: { node-version: 22 }
      - run: npm ci                        # falha se o lockfile não bate com o package.json
      - run: npm audit --audit-level=high
      - uses: aquasecurity/trivy-action@master
        with:
          scan-type: fs
          severity: HIGH,CRITICAL
          exit-code: '1'
      - uses: actions/dependency-review-action@v4   # bloqueia PR que adiciona dependência vulnerável
```

- **Arquivos a monitorar:** `pom.xml`/`build.gradle`, `package.json` e `package-lock.json`/`pnpm-lock.yaml`, `requirements.txt`/`poetry.lock`, `Dockerfile` e `docker-compose.yml`, `.github/workflows/*`, scripts de build e de deploy, arquivos de IaC.
- **Alertas automáticos de atualização:** habilitar Dependabot (`.github/dependabot.yml`) ou Renovate para abrir PRs de atualização e alertas de segurança, além de notificar o canal da equipe.

## 6. Cenários reais

- **Desenvolvedor adiciona biblioteca de fonte duvidosa:** o DevSecOps (1) bloqueia o merge por política; (2) analisa a biblioteca (mantenedores, downloads, histórico, licença, código, SCA e checagem de typosquatting); (3) se já entrou em algum ambiente, revoga/rotaciona segredos que ela poderia acessar, reconstrói artefatos e verifica o tráfego de saída; (4) propõe alternativa aprovada ou o processo de homologação; (5) registra lições e orienta o time.
- **Política de controle de bibliotecas externas:** lista de componentes/licenças aprovados; processo de solicitação e revisão; uso exclusivo de registro interno/proxy (Artifactory/Nexus) com *namespaces* reservados; lockfile e verificação de hash obrigatórios; SCA e SBOM em todo build; regra de manutenção mínima (projeto ativo, atualizações recentes); revisão periódica e remoção de dependências sem uso; responsável designado por cada componente.
- **Avaliação de um ambiente de CI/CD, pontos de vulnerabilidade:** tokens de longa duração e amplos; ações/scripts de terceiros sem fixar versão; runners compartilhados e persistentes; qualquer pessoa pode editar o workflow; segredos expostos em logs; ausência de revisão para mudanças no pipeline; artefatos sem assinatura/proveniência; deploy automático sem aprovação; registros de pacotes públicos misturados com internos.

## 7. Conclusão

- **O que o desenvolvedor pode fazer:** só usar componentes de fontes confiáveis e verificar hash/assinatura; fixar versões com lockfile; escanear dependências a cada build; evitar desserialização de dados não confiáveis (usar JSON com validação, listas de classes permitidas); revisar mudanças no pipeline; proteger o ambiente de build; assinar e gerar proveniência dos artefatos.
- **Papel de assinaturas, versionamento e controles no CI/CD:** a assinatura garante origem e que nada mudou após o build; o versionamento dá rastreabilidade e permite auditar/reverter; os controles no CI/CD (revisão, menor privilégio, build isolado) garantem que o que foi assinado é mesmo o resultado do código revisado. Juntos impedem que o código malicioso entre pelo caminho "legítimo".
