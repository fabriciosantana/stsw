# Prompt 16: Vulnerable and Outdated Components (A06:2021)

**Aluno:** Guilherme Abade
**Referências:** [OWASP A06:2021](https://owasp.org/Top10/A06_2021-Vulnerable_and_Outdated_Components/) · [CWE-1395](https://cwe.mitre.org/data/definitions/1395.html)

Os comandos de Trivy e OWASP Dependency-Check e o exemplo com o Juice Shop destinam-se a laboratório local.

## 1. Introdução

A categoria *Vulnerable and Outdated Components* refere-se ao risco de utilizar bibliotecas, frameworks, runtimes ou componentes de terceiros com vulnerabilidades conhecidas ou sem suporte. O software contemporâneo é montado sobre dezenas ou centenas de dependências, diretas e transitivas, de modo que uma falha em qualquer delas se converte em falha da aplicação.

A vulnerabilidade permanece no Top 10 porque é difícil de testar e de avaliar quanto ao risco, e porque quase toda aplicação emprega componentes de terceiros, cujo inventário e atualização exigem esforço contínuo; em 2021 ascendeu ao sexto lugar, posição obtida por votação da comunidade. Distingue-se da *Security Misconfiguration* na medida em que esta diz respeito a como o que se possui é configurado, enquanto aquela trata de o que se utiliza estar desatualizado ou vulnerável. Deixar o Actuator aberto configura a primeira; usar uma versão do Spring com vulnerabilidade conhecida configura a segunda.

A relação com dependências de terceiros é direta, pois cada manifesto de projeto traz dependências que, por sua vez, trazem outras, transitivas, e a vulnerabilidade costuma residir justamente em uma transitiva cuja existência o desenvolvedor sequer conhece, o que torna essencial inventariar a árvore completa. Entre os exemplos reais, o Log4Shell permitiu execução remota de código no Log4j 2 por meio de uma simples string de log, atingindo um número enorme de sistemas Java por se tratar de uma dependência onipresente, muitas vezes transitiva; a falha no Struts2 foi explorada no vazamento da Equifax. Os repositórios públicos como Maven, PyPI e npm facilitam o reúso de código, mas disseminam o risco, já que uma versão vulnerável ou maliciosa publicada é baixada automaticamente por milhares de projetos, servindo ainda de vetor para *typosquatting* e *dependency confusion*.

## 2. Riscos e impactos

Os principais riscos consistem em herdar vulnerabilidades conhecidas, muitas com exploração pública disponível, que vão da execução remota de código à exposição de dados, ao desvio de autenticação e à negação de serviço. Uma biblioteca de análise sintática vulnerável pode permitir execução remota; uma de serialização, a desserialização insegura; uma de expressões regulares, a negação de serviço por consumo excessivo. Como o componente executa dentro da aplicação, a exploração herda seus privilégios.

O impacto em sistemas críticos ou expostos à internet é máximo, pois estes se tornam alvo de varredura automática em massa logo após a divulgação de uma vulnerabilidade, como ocorreu no Log4Shell. Essas falhas passam despercebidas em testes manuais porque residem em código de terceiros, com frequência transitivo, que não aparece na revisão do código próprio nem nos testes funcionais, tornando-se visível apenas por meio da análise de composição de software, que cruza as versões com as bases de vulnerabilidades. A categoria relaciona-se com a CWE-1395, que descreve a fraqueza de depender de um componente de terceiro contendo vulnerabilidades, mapeando sua causa raiz. A SBOM, ou inventário de componentes e versões, nos formatos CycloneDX ou SPDX, tem papel preventivo: quando surge uma nova vulnerabilidade, a organização descobre em minutos se é afetada e onde, em vez de auditar manualmente, o que a torna base para a resposta rápida e para a conformidade.

## 3. Detecção e monitoramento

Os scanners de dependência, ou ferramentas de análise de composição de software, examinam o manifesto, o arquivo de versões travadas e as imagens, montam a lista de componentes e a cruzam com bases de vulnerabilidade. A tabela compara três ferramentas.

| Ferramenta | Foco | Fonte | Destaques |
|---|---|---|---|
| OWASP Dependency-Check | Dependências de aplicação, em Java, .NET e outras | NVD, por CVE e CPE | Gratuita, com plugins para Maven e Gradle e relatório em HTML e JSON |
| Trivy | Imagens de contêiner, sistema de arquivos, repositórios, infraestrutura como código e segredos | Base própria, com CVE e avisos | Rápido, abrangente, adequado a Docker e a pipelines |
| Snyk | Dependências, contêineres, infraestrutura como código e código | Base própria e CVE | Comercial, com sugestão de correção e abertura de PR |

A comparação entre o Trivy e o Dependency-Check mostra que este é especialista em dependências de aplicação e utiliza a base da NVD com correspondência por CPE, enquanto aquele é mais amplo, por cobrir imagens, sistema operacional, infraestrutura como código e segredos, com base própria, maior velocidade e orientação a contêiner e pipeline; em projetos Java, costumam ser complementares. O funcionamento do Dependency-Check compreende a coleta de evidências de cada dependência, como nome, versão e hash, e a derivação do identificador de plataforma; a consulta à base da NVD por vulnerabilidades daquele identificador; a geração de um relatório com cada dependência, as vulnerabilidades associadas e a pontuação CVSS; e a possibilidade de falhar a construção acima de um limiar.

A pontuação CVSS mede a severidade de uma vulnerabilidade de 0 a 10, em que de 0 a 3,9 é baixa, de 4 a 6,9 média, de 7 a 8,9 alta e de 9 a 10 crítica, a partir de vetores como vetor de ataque, complexidade, privilégios e impacto em confidencialidade, integridade e disponibilidade. A pontuação orienta a priorização, de forma que o crítico e o alto sejam tratados primeiro, sobretudo quando há exposição e exploração conhecida; como não considera o contexto específico, serve de guia, e não de veredito absoluto. O exemplo a seguir contém uma dependência vulnerável.

```xml
<!-- pom.xml com versão vulnerável (Log4Shell) -->
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-core</artifactId>
  <version>2.14.1</version>   <!-- CVE-2021-44228, CVSS 10.0 -->
</dependency>
```

```bash
mvn org.owasp:dependency-check-maven:check
```

O relatório esperado lista `log4j-core 2.14.1` associado à vulnerabilidade CVE-2021-44228, de pontuação crítica. A medida corretiva consiste em atualizar para a versão corrigida, aplicar uma mitigação temporária quando a atualização imediata não for viável e reexecutar a varredura para confirmar a eliminação da vulnerabilidade.

## 4. Prática com ferramentas reais

O roteiro a seguir analisa o Juice Shop com as duas ferramentas.

```bash
# 1. Clonar o projeto
git clone https://github.com/juice-shop/juice-shop && cd juice-shop

# 2. OWASP Dependency-Check sobre as dependências Node, via Docker
docker run --rm -v "$(pwd)":/src owasp/dependency-check \
  --scan /src --format HTML --out /src/relatorio-dc --project "juice-shop"

# 3. Trivy na imagem Docker do Juice Shop
docker pull bkimminich/juice-shop
trivy image bkimminich/juice-shop

# 3b. Trivy no sistema de arquivos
trivy fs .
```

O Juice Shop é intencionalmente vulnerável e emprega bibliotecas antigas, razão pela qual os relatórios tendem a listar diversos componentes com vulnerabilidades de severidades variadas. O exercício consiste em interpretar o relatório, identificar os itens mais críticos e descrever a correção, seja a atualização, seja a remoção; a própria aplicação dispõe de um desafio relacionado, que pede informar à loja sobre uma biblioteca vulnerável em uso.

## 5. Aplicação em cenário real

Em um sistema bancário legado que utiliza bibliotecas antigas sem atualização, os passos práticos para identificar e corrigir vulnerabilidades começam pelo inventário, com a geração de uma SBOM de todos os componentes e versões, seguido da varredura por análise de composição de software, com priorização pela pontuação CVSS, pela exposição e pela existência de exploração pública. Em seguida, atualiza-se em ambiente de teste, a começar pelos itens críticos, aplicando mitigações onde a atualização não for possível, executa-se a bateria de testes automatizados para garantir a estabilidade, reexecuta-se a varredura e documenta-se o resultado, estabelecendo monitoramento contínuo.

Os riscos legais e de conformidade são relevantes, pois o uso de componente com vulnerabilidade conhecida, seguido de vazamento, pode caracterizar negligência, com sanções da LGPD, exigências regulatórias próprias do setor financeiro e responsabilidade contratual. A política de atualização e verificação contínua no pipeline inclui a análise de composição de software como etapa obrigatória, que falha a construção acima de um limiar, a abertura automática de propostas de atualização, a geração e o arquivamento de uma SBOM a cada construção, prazos de correção definidos por severidade e a revisão periódica de dependências sem suporte.

## 6. Conclusão

Depender de componentes de terceiros é inevitável e produtivo, porém transfere o risco das vulnerabilidades desses componentes para a aplicação; sem inventário e varredura, a falha permanece invisível até ser explorada. A distinção entre vulnerabilidade conhecida e de dia zero é essencial: a conhecida já possui identificador público e, muitas vezes, correção disponível, de modo que o risco está em não aplicar a atualização, foco desta categoria; a de dia zero ainda não tem conhecimento público nem correção, e defende-se por defesa em profundidade e resposta rápida quando divulgada. Para manter versões seguras, recomendam-se o inventário e a SBOM atualizados, a análise de composição contínua no pipeline com barreira por severidade, a atualização automatizada, o acompanhamento de avisos de segurança, a remoção de dependências não utilizadas e a preferência por componentes mantidos e com suporte ativo.
