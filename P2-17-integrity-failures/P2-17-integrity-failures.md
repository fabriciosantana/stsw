# P2-17 — OWASP Top Ten: Software and Data Integrity Failures

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software (STSW) — IDP  
**Professor:** Fabricio Santana  
**Prompt:** [prompts/17-integrity.md](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/17-integrity.md)


## 1. Introdução à Vulnerabilidade Software and Data Integrity Failures

### Explique o que a OWASP define como falha de integridade de software e dados

A08:2021 cobre falhas em que código, atualizações, dados críticos ou processos de build/deploy não têm controles suficientes para impedir alteração não autorizada ou verificar origem e integridade. Exemplos incluem plugins ou bibliotecas obtidos de fontes não confiáveis, atualização automática sem validação, pipeline CI/CD mal protegido e desserialização de objetos não confiáveis. A OWASP destaca a necessidade de validar assinaturas/proveniência e proteger a cadeia de desenvolvimento. https://top10.owasp.org/2021/A08_2021-Software_and_Data_Integrity_Failures/

### Principais problemas, atualizações e bibliotecas sem verificação

Sem uma verificação de origem e integridade, o sistema pode instalar código adulterado como se fosse atualização legítima. Um pacote pode vir de publicador comprometido, de um registry não aprovado, de dependência transitiva maliciosa ou de um processo de build alterado. O risco é execução de código com os privilégios do build ou da aplicação, alteração de artefatos e comprometimento de todos os consumidores da distribuição.

### Como se relaciona com ataques a CI/CD, dependency confusion e insecure deserialization?

- **Ataque à cadeia CI/CD:** invasor modifica código-fonte, configuração, credencial do runner, action/plugin ou artefato durante build/deploy; o processo publica uma atualização aparentemente legítima.
- **Dependency confusion:** um gerenciador resolve um pacote público com o mesmo nome de um pacote interno, ou prioriza a fonte errada, fazendo o build instalar conteúdo controlado por terceiro.
- **Desserialização insegura:** aplicação aceita objetos serializados controláveis por atacante e os reconstitui sem validação segura. Dependendo da tecnologia e das classes disponíveis, pode causar alteração de estado, acesso indevido ou execução de código.

“Carregar sem verificação” significa que a aplicação confia em nome, URL, tag ou conexão sem validar que o artefato veio do fornecedor esperado e permaneceu íntegro. TLS protege o transporte em determinadas condições, mas não prova, sozinho, que o autor do pacote é confiável.

### Consequências de desserializar objetos não confiáveis

A desserialização pode produzir estados indevidos, burlar validações, consumir recursos e, em algumas bibliotecas/cadeias de gadgets, levar a execução de código. Dados serializados recebidos de clientes não devem ser tratados como seguros apenas porque estão codificados ou assinados informalmente.

### Em quais sistemas os riscos são mais críticos?

- **Cloud:** credenciais de pipeline e identidades de serviço podem ter acesso amplo a recursos e dados.
- **Containers:** imagem alterada pode distribuir backdoor para múltiplas réplicas e ambientes.
- **CI/CD:** runners, tokens, actions, artefatos e segredos podem permitir que a alteração chegue à produção.
- **Sistemas que autoatualizam:** um pacote adulterado pode ser aplicado automaticamente a muitos clientes.

## 2. Compreensão dos Impactos

### Como um invasor pode comprometer a integridade de dados por meio de bibliotecas externas?

Uma biblioteca executada com privilégios da aplicação pode alterar registros, regras de negócio, arquivos, respostas ou dados antes de persistência. Também pode interceptar dados e produzir efeitos sobre confidencialidade ou disponibilidade. O vetor depende do componente e do nível de acesso concedido.

### Diferença entre integridade e confidencialidade

- **Integridade:** dados e código permanecem corretos e só são modificados por atores autorizados.
- **Confidencialidade:** dados só são acessados por atores autorizados.

Uma violação de integridade pode ocorrer sem divulgação de dados; uma violação de confidencialidade pode ocorrer sem alterar registros. Ataques a software frequentemente afetam ambas, além da disponibilidade.

### O que acontece se atualização automatizada executar código malicioso?

O código pode executar com permissões da aplicação ou do agente que instalou a atualização. Isso pode permitir roubo de segredos, alteração de artefatos, instalação de backdoor, acesso a dados e propagação a outros sistemas. A automação amplia a escala: o pacote pode ser distribuído antes que a equipe detecte a alteração.

### Relação com integridade, disponibilidade e confiabilidade

A integridade é violada quando código ou dados são alterados sem autorização; a disponibilidade pode ser afetada por sabotagem ou DoS; e a confiabilidade da distribuição/build deixa de existir quando consumidores não conseguem provar a origem e o estado do artefato.

### Prejuízos financeiros e reputacionais

Custos podem incluir investigação, contenção, rebuild de ambientes, rotação de credenciais, interrupção de serviço, atendimento a clientes, auditoria e reparação. A empresa também pode perder confiança de clientes e parceiros e enfrentar obrigações contratuais ou regulatórias conforme o caso.

## 3. Exemplos Reais de Ataques Ligados à Falha de Integridade

### O ataque de Event-Stream

Em 2018, uma nova dependência maliciosa chamada flatmap-stream foi adicionada ao pacote npm event-stream por um mantenedor que havia obtido acesso ao projeto. O código malicioso tinha como alvo uma aplicação específica associada a carteiras de criptomoedas. O vetor foi a confiança em manutenção e distribuição de dependência transitiva: a versão comprometida foi publicada em um pacote popular. O npm removeu as versões afetadas e tomou controle do pacote para impedir abuso adicional. https://blog.npmjs.org/post/180565383195/details-about-the-event-stream-incident

### Ataque ao SolarWinds Orion

No incidente SolarWinds, código malicioso foi introduzido no processo de build do Orion e distribuído por atualizações legítimas do produto. O ataque mostra que assinatura ou aparência de atualização oficial não basta se o próprio ambiente que produz o artefato está comprometido; é necessário proteger a cadeia de build e verificar proveniência e comportamento do software. O MITRE ATT&CK descreve a inserção de código no build e a distribuição pela atualização usual. https://attack.mitre.org/campaigns/C0024/

### Dependency confusion e empresas como Apple e Microsoft

Em 2021, o pesquisador Alex Birsan descreveu um experimento de dependency confusion envolvendo nomes de pacotes internos de organizações, incluindo Apple e Microsoft. Em termos gerais, nomes internos foram publicados em registries públicos com versões altas, para verificar se builds corporativos tentariam resolver esses nomes pela fonte pública. O caso demonstra como configuração ambígua de fontes e nomes previsíveis pode fazer o pipeline baixar o pacote errado.

A menção de uma empresa no relato não prova, por si só, que ela sofreu comprometimento ou exfiltração. É importante diferenciar uma demonstração com divulgação coordenada de uma intrusão confirmada. A defesa é reservar namespaces internos, configurar prioridade de registries explicitamente, restringir fontes e monitorar resolução de dependências. https://medium.com/@alex.birsan/dependency-confusion-how-i-hacked-into-apple-microsoft-and-dozens-of-other-companies-4a5d60fec610

### Como falhas no CI/CD permitem backdoors

Um invasor que obtém acesso a token, runner, action ou regra de aprovação pode alterar código, dependências, comandos de build ou artefatos. Se a pipeline publica sem revisão, assinatura/proveniência verificável e segregação, a mudança pode chegar a produção como release normal.

### Erros de segurança nos casos

As lições recorrentes são confiança excessiva em mantenedores ou fontes externas, privilégios amplos em pipelines, ausência de inventário e lockfiles revisados, pouca segregação entre código e distribuição e validação insuficiente da origem e proveniência do artefato. Cada incidente possui detalhes próprios; esses pontos são padrões de risco, não afirmação de que todos ocorreram igualmente em todos os casos.

## 4. Técnicas de Prevenção e Boas Práticas

### O que significa assinar digitalmente pacotes e binários?

O produtor calcula uma assinatura com uma chave privada sobre o pacote ou seu digest. O consumidor verifica essa assinatura usando a chave pública confiável. Se a verificação passa, há evidência de que o conteúdo corresponde à assinatura e foi produzido por quem controla a chave correspondente. A assinatura não prova que o software é benigno: é necessário proteger as chaves e confiar no processo do produtor.

### Como Dependency-Check, SLSA e provenance ajudam?

- **OWASP Dependency-Check** encontra dependências com vulnerabilidades conhecidas; é controle de análise de componente, não prova de origem ou integridade do build.
- **CycloneDX/SBOM** registra componentes e versões presentes no artefato.
- **SLSA** oferece níveis e práticas para fortalecer a cadeia de build e reduzir risco de adulteração.
- **Provenance metadata** descreve como, onde e a partir de que fontes um artefato foi produzido. Consumidores podem verificar se a proveniência satisfaz uma política de confiança.
- **Assinaturas e attestations** vinculam artefatos a produtores/builds e ajudam a detectar adulteração.

### Por que restringir plugins e bibliotecas externas desconhecidas?

Cada dependência aumenta superfície, exposição a CVEs e confiança delegada. Plugins podem executar código com permissões amplas. Restringir fontes, revisar necessidade, manutenção, reputação, permissões e licenças reduz a chance de incluir pacote malicioso ou abandonado.

### Políticas para CI/CD

- Proteger branch principal com revisão obrigatória e checks.
- Conceder permissões mínimas ao token de cada workflow.
- Usar runners isolados/efêmeros e evitar execução de código não confiável com segredos de produção.
- Fixar actions e ferramentas em versões imutáveis verificadas (por exemplo, SHA completo), atualizando-as por processo revisado.
- Separar build e deploy, exigindo aprovação e identidade de curta duração para produção.
- Proteger segredos em secret manager; rotacionar após suspeita de exposição.
- Gerar SBOM e provenance por release, assinar artefatos e verificar antes do deploy.
- Manter logs de build e trilha de auditoria fora do controle do executor da pipeline.

### Controle de versão e dependências

Commits revisáveis, tags protegidas, lockfiles, hashes de pacote, revisão de alterações e registro de origem tornam mudanças auditáveis e repetíveis. Dependências devem ser atualizadas por pull requests rastreáveis, com comparação de changelog, análise de risco e testes.

## 5. Prática com Ferramentas Reais

### OWASP Dependency-Check em projeto Java

Procedimento proposto para um projeto de laboratório:

~~~bash
dependency-check.sh --project "projeto-java-lab" --scan . --format HTML --out ./reports
~~~

Na análise real, registrar versão do scanner, timestamp, commit e relatório. Revisar os componentes identificados, CVEs, evidências de correspondência e severidade. Confirmar falsos positivos e alcançar o caminho da dependência antes de definir prioridade.

**Resultado:** nenhum projeto Java foi escaneado nesta sessão; portanto, não declaro vulnerabilidades detectadas.

### Snyk ou Trivy em contêineres e projetos Node.js/Python

Exemplos de comandos reproduzíveis com Trivy:

~~~bash
trivy fs --scanners vuln --format table .
trivy image --scanners vuln nome-da-imagem:tag
~~~

Para Node.js/Python, usar o diretório que contém os manifests e lockfiles; para contêiner, informar a imagem exata e registrar tag/digest. Snyk pode analisar o repositório/manifesto via CLI ou integração habilitada. Revisar advisory, caminho e versão; corrigir e repetir o scan.

**Resultado:** Trivy/Snyk e imagem Juice Shop não foram escaneados nesta sessão.

### Pipeline de CI/CD simples com verificação das dependências

A proposta a seguir ilustra uma etapa básica para Node.js. Em produção, fixar actions a SHAs completos revisados e manter ações atualizadas; o exemplo usa tags legíveis para fins didáticos.

~~~yaml
name: dependency-integrity
on:
  pull_request:
  push:
    branches: [main]

permissions:
  contents: read

jobs:
  scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: npm
      - name: Install from lockfile
        run: npm ci
      - name: Check known advisories
        run: npm audit --audit-level=high
      - name: Record lockfile digest
        run: sha256sum package-lock.json
~~~

Esse pipeline verifica avisos conhecidos e registra o digest do lockfile, mas não substitui assinatura do artefato, SBOM ou provenance. A aprovação do pipeline precisa ser protegida; o próprio workflow, actions e runner também fazem parte da cadeia de confiança.

### Arquivos a monitorar e alertas

Monitorar manifests e lockfiles: package.json/package-lock.json, pom.xml, gradle files/lockfiles, requirements.txt/poetry.lock/uv.lock, Dockerfile e arquivos de configuração de build. Também controlar workflow YAML, scripts de publicação e fontes de registry. Alertas devem gerar tickets com componente, versão, CVE, criticidade, dono e prazo; secret scanning deve acionar revogação/rotação de segredo quando confirmar exposição.

## 6. Aplicação em Cenários Reais

### Biblioteca de fonte duvidosa: resposta do time DevSecOps

Interromper a integração até confirmar origem e finalidade; preservar o diff, logs do build e versão instalada; identificar se houve execução e quais permissões possuía; procurar segredos/dados acessíveis; remover o pacote e substituir por alternativa revisada; reconstruir em runner limpo; rotacionar credenciais expostas; revisar artefatos implantados e monitorar sinais de comprometimento. Se o pacote já chegou a clientes, tratar como incidente e seguir plano de resposta/divulgação aplicável.

### Política organizacional de bibliotecas externas

1. Permitir registries aprovados e bloquear resolução ambígua entre fontes privadas e públicas.
2. Exigir revisão de nova dependência, justificativa, mantenedor, manutenção, licença, reputação e privilégio.
3. Fixar versões e usar lockfiles; atualizar de forma regular e urgente para CVEs exploradas.
4. Gerar SBOM por artefato; manter inventário e responsáveis.
5. Executar SCA, secret scan e análise de imagem em CI.
6. Usar proveniência, assinatura e verificação de artefatos.
7. Exigir exceção documentada, dono, mitigação e data de expiração.
8. Auditar dependências e pipelines periodicamente.

### Avaliação de CI/CD — pontos de risco

Verificar quem pode alterar workflows, tokens com permissões excessivas, secrets expostos a forks, actions de terceiros sem pinagem, runners compartilhados, artefatos não assinados, build executado em branch não confiável, deploy sem aprovação, dependências sem lockfile, upload/download de artefatos sem digest validado e logs contendo segredos.

## 7. Conclusão

Falhas de integridade permitem que código e dados sejam alterados ou distribuídos sem a confiança e validação esperadas. A defesa precisa cobrir dependências, registries, build, artefatos e deploy; ferramentas isoladas não substituem processos de confiança, revisão e resposta.

### Como garantir integridade em produção?

Usar dependências verificadas e fixadas; manter lockfiles e SBOM; proteger branches, credenciais e runners; gerar artefatos em ambiente controlado; assinar e verificar releases; aplicar menor privilégio; monitorar alertas e atualizar componentes; manter processo de rollback e resposta a comprometimento.

### Papel de assinaturas, versionamento e controles de CI/CD

Assinaturas e provenance ajudam a validar autoria, origem e cadeia de produção. Versionamento e lockfiles tornam alterações auditáveis e builds reproduzíveis. Revisões, isolamento, permissões mínimas e gates no CI/CD impedem ou detectam mudanças não autorizadas antes da produção.

## Referências

- OWASP Top 10:2021 A08: https://top10.owasp.org/2021/A08_2021-Software_and_Data_Integrity_Failures/
- npm — relatório Event-Stream: https://blog.npmjs.org/post/180565383195/details-about-the-event-stream-incident
- MITRE ATT&CK — SolarWinds Orion: https://attack.mitre.org/campaigns/C0024/
- Alex Birsan — Dependency Confusion (relato do pesquisador): https://medium.com/@alex.birsan/dependency-confusion-how-i-hacked-into-apple-microsoft-and-dozens-of-other-companies-4a5d60fec610
- OWASP Dependency-Check: https://owasp.org/www-project-dependency-check/
- SLSA: https://slsa.dev/
- CycloneDX: https://cyclonedx.org/
- Trivy: https://trivy.dev/latest/docs/
