# P2-16 — OWASP Top Ten: Vulnerable and Outdated Components

**Aluno:** Leonardo Freitas  
**Disciplina:** Segurança e Teste de Software (STSW) — IDP  
**Professor:** Fabricio Santana  
**Prompt:** [prompts/16-outdated.md](https://github.com/fabriciosantana/stsw/blob/2026.2/prompts/16-outdated.md)


## 1. Introdução à Vulnerabilidade de Componentes Vulneráveis e Desatualizados

### Explique o que é a vulnerabilidade segundo a OWASP

A06:2021 ocorre quando a aplicação utiliza componentes de software vulneráveis, sem suporte ou desatualizados, ou quando a equipe não mantém inventário, atualização e monitoramento adequados desses componentes. Componentes incluem bibliotecas diretas e transitivas, frameworks, runtimes, servidores, plugins, imagens de contêiner e ferramentas de build.

A vulnerabilidade pode existir mesmo quando o código da aplicação está correto: uma biblioteca pode conter uma falha conhecida que um atacante consegue alcançar por meio das funções usadas pela aplicação.

### Por que permanece no Top 10? O que a diferencia de Security Misconfiguration?

Permanece relevante porque aplicações modernas dependem de um grande ecossistema de terceiros, incluindo dependências transitivas que a equipe talvez não conheça. Vulnerabilidades são divulgadas continuamente e muitas aplicações não têm processo confiável para saber se um componente afetado está presente, se está acessível ou se foi corrigido.

A06 se concentra em componentes com vulnerabilidades conhecidas, sem suporte ou sem manutenção adequada. A05 se concentra em opções e controles configurados incorretamente. Uma dependência antiga com CVE conhecida é tema de A06; uma aplicação atual em modo debug ou um bucket público por permissões erradas é tema de A05. As categorias podem se relacionar, mas são causas distintas. https://top10.owasp.org/2021/A06_2021-Vulnerable_and_Outdated_Components/

### Qual a relação entre dependências, bibliotecas de terceiros e essa vulnerabilidade?

O gerenciador de pacotes resolve dependências diretas e transitivas. A aplicação pode acabar incluindo bibliotecas que não aparecem diretamente no código, e uma vulnerabilidade transitiva pode alcançar funções executadas em produção. Maven Central, PyPI e npm oferecem distribuição e descoberta de pacotes, mas a disponibilidade em um registry não é garantia de que um pacote seja seguro, mantido ou adequado ao projeto.

Arquivos de lock (por exemplo, package-lock.json) registram versões e integridade esperadas para tornar a resolução reproduzível. Eles ajudam a estabilizar builds, mas não tornam uma versão vulnerável segura. É necessário monitorar advisories, origem, manutenção, licenças e compatibilidade.

### Exemplos reais — incluindo Log4Shell

- **Log4Shell, CVE-2021-44228:** falha crítica de execução remota de código no Apache Log4j. Sistemas que incluíam versões afetadas e expunham o caminho vulnerável podiam ser comprometidos. O NVD registra o CVE e a CISA documentou exploração ativa; a resposta inclui inventariar Log4j inclusive em dependências transitivas e atualizar para versão corrigida suportada. https://nvd.nist.gov/vuln/detail/CVE-2021-44228
- **Apache Struts, CVE-2017-5638:** a falha explorada na violação de dados da Equifax afetava o framework Struts. O relatório do GAO aponta problemas de inventário, detecção e distribuição do aviso de correção, o que contribuiu para a falha de identificação e correção em tempo hábil. https://www.gao.gov/assets/gao-18-559.pdf
- **OpenSSL Heartbleed, CVE-2014-0160:** falha de leitura de memória em versões vulneráveis do OpenSSL; ilustra como uma biblioteca amplamente incorporada pode afetar muitos serviços, mesmo quando a falha não está no código de negócio da aplicação. https://nvd.nist.gov/vuln/detail/CVE-2014-0160

### Qual o papel de Maven, PyPI e npm na disseminação do risco?

Esses repositórios tornam fácil instalar, publicar e atualizar pacotes. Isso acelera o desenvolvimento, mas também amplia a cadeia de confiança: um pacote pode ser abandonado, comprometido, substituído por um pacote malicioso ou depender de uma versão vulnerável. O registry não substitui revisão, lockfiles, verificação de origem, SCA e processo de atualização.

## 2. Riscos e Impactos Associados

### Principais riscos; exposição de dados e negação de serviço

A depender do CVE e da forma como o sistema usa o componente, a exploração pode causar execução de código, leitura ou alteração de dados, escalada de privilégios, indisponibilidade, movimentação lateral ou comprometimento da cadeia de build. Um DoS pode ocorrer quando a falha permite consumo desproporcional de CPU/memória ou bloqueio de recursos; exposição de dados pode ocorrer por leitura arbitrária, bypass de controles ou execução de código.

### Impacto em sistemas críticos ou expostos à internet

Em sistemas bancários, de saúde, governo ou infraestrutura crítica, o impacto pode incluir indisponibilidade de serviços essenciais, fraude, exposição de dados regulamentados, comprometimento de credenciais e interrupção operacional. A exposição à internet pode aumentar a quantidade de atacantes capazes de alcançar o componente, mas serviços internos também podem ser explorados após uma intrusão inicial.

### Por que testes manuais podem não detectar a falha?

A inspeção manual da interface raramente revela todas as bibliotecas instaladas, principalmente as transitivas, versões empacotadas dentro de JARs ou imagens, componentes usados apenas em jobs ou endpoints pouco acessados. A falha pode depender de uma combinação específica de versão, configuração e fluxo de dados. Por isso, a análise de código precisa ser complementada por inventário/SBOM, SCA e monitoramento contínuo de advisories.

### CWE e CWE-1395

A OWASP A06 mapeia fraquezas relacionadas ao uso de componentes conhecidos por conter vulnerabilidades. A **CWE-1395 — Dependency on Vulnerable Third-Party Component** descreve dependência de componente de terceiros que contém uma vulnerabilidade conhecida, o que pode expor a aplicação a ataques por meio desse componente. A existência da dependência não prova, sozinha, que a aplicação é explorável: é preciso analisar versão, uso, configuração e alcançabilidade. https://cwe.mitre.org/data/definitions/1395.html

### Papel da SBOM

A SBOM (Software Bill of Materials) é um inventário estruturado de componentes e versões presentes em um artefato. Ela ajuda a responder rapidamente “onde usamos essa biblioteca?” após um novo CVE, inclusive em dependências transitivas e contêineres. Deve ser gerada por build, mantida junto à versão do artefato e usada com fontes de vulnerabilidade atualizadas. SBOM não é, por si só, uma avaliação de segurança nem garante que as dependências sejam confiáveis.

## 3. Detecção e Monitoramento Prático

### Scanners de dependência: Dependency-Check, Snyk e Trivy

Scanners SCA identificam dependências, comparam versões e metadados com bases de vulnerabilidades e produzem alertas/relatórios. A identificação pode usar manifestos, lockfiles, nomes e versões ou análise de artefatos. Falsos positivos, versões não reconhecidas e diferenças entre “componente presente” e “código vulnerável alcançável” exigem triagem humana.

- **OWASP Dependency-Check:** projeto OWASP que analisa dependências para mapear componentes a vulnerabilidades conhecidas e oferece CLI, plugins Maven/Gradle/Ant e integrações CI. A correspondência pode precisar de revisão, pois identificação por nome/versão pode gerar falsos positivos ou não identificar componentes empacotados. https://owasp.org/www-project-dependency-check/
- **Snyk:** plataforma SCA que testa manifestos e lockfiles e apresenta advisory, severidade, caminho de dependência e recomendações de atualização; pode integrar repositórios e pipeline conforme plano/configuração.
- **Trivy:** scanner abrangente para repositórios, sistemas de arquivos, imagens e outros artefatos; detecta vulnerabilidades em pacotes do sistema e dependências de linguagem, além de outros tipos de risco conforme configuração. https://trivy.dev/latest/docs/target/

### Comparação entre Trivy e OWASP Dependency-Check

| Aspecto | Trivy | OWASP Dependency-Check |
|---|---|---|
| Uso comum | Imagens de contêiner, filesystem/repositório e pacotes de sistema e linguagem | SCA de dependências em projetos, com integração Maven/Gradle/CLI |
| Vantagem | Abrange diversos alvos em um único scanner; útil para pipeline de imagem e repositório | Relatório focado em dependências e integração familiar com builds Java |
| Limite | O resultado depende dos alvos e fontes habilitados; não prova explorabilidade | A identificação baseada em correspondência pode exigir triagem e a análise não substitui revisão |
| Complemento | SCA específico, SBOM, revisão e atualização | Scanner de imagem, secret scanning, análise do código e revisão |

### Como funciona o Dependency-Check e como interpretar CVE/CVSS

O scanner identifica componentes no projeto, busca correspondências nas fontes de vulnerabilidades configuradas, associa possíveis CVEs e gera relatório com evidência, versão e severidade. O processo pode incluir atualização de bases locais e demora conforme tamanho do projeto e disponibilidade da rede. Confirme se o componente foi identificado corretamente e se a versão afetada está de fato presente.

CVE é um identificador de vulnerabilidade; CVSS é um sistema de pontuação de severidade técnica. A pontuação não é o risco empresarial completo: priorize também exposição, privilégio necessário, disponibilidade de exploit, dados/processos afetados e possibilidade de mitigação. Uma vulnerabilidade com CVSS alto em componente não alcançável pode ter prioridade diferente de uma falha moderada diretamente exposta.

### Exemplo de projeto Java e simulação de execução

Para um projeto Maven, uma forma de integrar o plugin oficial é executar:

~~~bash
mvn org.owasp:dependency-check-maven:check
~~~

Exemplo mínimo de dependência para demonstrar a análise em um laboratório isolado (não usar esta versão em produção):

~~~xml
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-core</artifactId>
  <version>2.14.1</version>
</dependency>
~~~

A análise esperada é que o relatório procure correspondências para a versão detectada e associe advisories/CVEs aplicáveis. **Não foi executado nesta entrega**, então não afirmo que um CVE específico apareceu no relatório nem forneço pontuação observada. Depois da execução, guardar o relatório HTML/JSON, validar a dependência, corrigir para uma versão suportada e executar o scanner novamente.

## 4. Prática com Ferramentas Reais — OWASP Juice Shop

O procedimento abaixo é para uma cópia local de treinamento. A OWASP documenta a execução do Juice Shop com Docker e recomenda usá-lo como aplicação deliberadamente vulnerável para treinamento. https://devguide.owasp.org/en/07-training-education/01-vulnerable-apps/01-juice-shop/

~~~bash
git clone --depth 1 https://github.com/juice-shop/juice-shop.git
cd juice-shop
~~~

**Dependency-Check sobre o projeto:**

~~~bash
dependency-check.sh --project "OWASP Juice Shop" --scan . --format HTML --out ./reports
~~~

A sintaxe pode variar conforme instalação/versão da ferramenta. Registrar a versão, manter as fontes atualizadas e não incluir credenciais da NVD em logs. Revisar o relatório para confirmar que o componente, versão e caminho de dependência foram identificados corretamente.

**Trivy no repositório e na imagem:**

~~~bash
trivy fs --scanners vuln --format table .
docker build -t juice-shop-lab .
trivy image --scanners vuln juice-shop-lab
~~~

O build da imagem precisa ser feito no checkout local conforme instruções oficiais do projeto. Uma imagem oficial também pode ser examinada, desde que a tag/digest seja registrado para reprodutibilidade. Não atualizar automaticamente uma imagem de produção a partir do resultado do laboratório.

**Estado da análise:** nenhum clone, build, scan, nem container Juice Shop foi executado nesta sessão. Os comandos e a descrição de interpretação são instruções de execução; nenhum achado foi fabricado. Para comprovar a atividade prática, anexar os relatórios gerados e identificar data, versão da ferramenta, commit/tag do Juice Shop e imagem/digest analisado.

## 5. Aplicação em Cenário Real: sistema bancário legado

### Passos práticos para identificar e corrigir vulnerabilidades de componentes

1. Criar inventário de aplicações, runtimes, sistemas operacionais, dependências diretas e transitivas, imagens e serviços.
2. Gerar SBOM por release e procurar componentes fora de suporte.
3. Rodar SCA e correlacionar alertas com CVE/CVSS, exposição, alcançabilidade e criticidade do sistema.
4. Confirmar falsos positivos e mapear quais módulos realmente usam a dependência.
5. Definir contenção quando não houver patch imediato: restringir exposição, desabilitar caminho afetado ou isolar serviço, mantendo gestão formal de risco.
6. Atualizar para versão corrigida suportada, executar testes de regressão e segurança, e implantar com rollback controlado.
7. Monitorar advisories novos e repetir a análise em cada build e periodicamente em artefatos implantados.

### Riscos legais e de conformidade

A depender dos dados e contratos, uma exploração pode gerar obrigações de notificação, investigação, proteção de dados, auditoria, continuidade de serviço e demonstração de controles razoáveis. O uso de componente vulnerável não significa automaticamente uma infração legal; a análise depende da legislação aplicável, dos dados envolvidos, do impacto e das medidas adotadas pela organização.

### Política de atualização e verificação contínua no CI/CD

- Atualizar dependências com cadência definida e correções críticas priorizadas por risco e exposição.
- Manter lockfiles revisados e builds reproduzíveis.
- Executar SCA em pull requests e no pipeline; falhar o build em vulnerabilidade crítica confirmada ou política definida, com processo formal de exceção temporária e prazo.
- Gerar SBOM por release e associá-la ao artefato.
- Verificar assinatura/proveniência de artefatos e restringir permissões do pipeline.
- Abrir tickets de atualização com dono, severidade, prazo e evidência de reteste.
- Monitorar dependências implantadas, não somente os arquivos atuais do repositório.

## 6. Conclusão

Componentes vulneráveis e desatualizados podem introduzir falhas críticas sem que exista erro no código de negócio. Gestão efetiva exige inventário, SBOM, scanners, triagem contextual, atualização e controle da cadeia de fornecimento.

### Diferença entre vulnerabilidade conhecida e zero-day

Uma vulnerabilidade conhecida foi divulgada ou registrada e pode ter CVE/advisory, permitindo busca e correção dirigida. Zero-day é uma falha explorável para a qual ainda não havia correção disponível ao público/fornecedor no momento considerado. Essa distinção pode mudar ao longo do tempo: depois da divulgação, um zero-day pode se tornar vulnerabilidade conhecida, mesmo que ainda não haja patch.

### Como manter versões seguras

Manter inventário/SBOM, acompanhar fontes oficiais de advisories, usar versões suportadas e lockfiles, executar SCA em CI e em produção, revisar exceções com prazo, validar atualizações e remover dependências sem necessidade. A decisão final deve ponderar severidade, exposição, alcance do componente e impacto no negócio.

## Referências

- OWASP Top 10:2021 — A06: https://top10.owasp.org/2021/A06_2021-Vulnerable_and_Outdated_Components/
- MITRE CWE-1395: https://cwe.mitre.org/data/definitions/1395.html
- OWASP Dependency-Check: https://owasp.org/www-project-dependency-check/
- OWASP Developer Guide — Dependency-Check: https://devguide.owasp.org/en/05-implementation/02-dependencies/01-dependency-check/
- Trivy documentation: https://trivy.dev/latest/docs/
- NVD CVE-2021-44228 (Log4Shell): https://nvd.nist.gov/vuln/detail/CVE-2021-44228
- NVD CVE-2014-0160 (Heartbleed): https://nvd.nist.gov/vuln/detail/CVE-2014-0160
- GAO-18-559 — Equifax: https://www.gao.gov/assets/gao-18-559.pdf
- OWASP Juice Shop Developer Guide: https://devguide.owasp.org/en/07-training-education/01-vulnerable-apps/01-juice-shop/
