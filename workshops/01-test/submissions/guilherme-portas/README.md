# Apache JMeter 5.6.3 — Teste de carga com apoio de IA

**Aluno:** Guilherme de Oliveira Portas · **Disciplina:** Segurança e Testes de Software (IDP) · **Apresentação:** `guilherme-portas.pdf` (12 slides + demo ao vivo)

## Introdução

O Apache JMeter é uma aplicação 100% Java, open source, para teste de carga e performance. Criado originalmente para web, hoje cobre HTTP(S), REST/SOAP, JDBC, JMS, FTP, TCP e outros protocolos. Ponto central: **o JMeter atua no nível de protocolo, não é um navegador** — não executa JavaScript nem renderiza HTML; simula múltiplos clientes fazendo requisições.

Na pirâmide de automação de testes, o JMeter posiciona-se no topo: **testes de sistema / carga e performance** (volume esperado, stress, spike e soak), acima dos testes de unidade e de integração/API funcional. Ele não substitui JUnit nem testes funcionais — complementa-os respondendo "o sistema aguenta?" em vez de "o sistema está correto?".

## Principais Funcionalidades

- IDE de teste (GUI) para gravar, montar e depurar Test Plans; **carga real sempre no modo CLI** (`-n`), sem listeners pesados.
- Multi-threading com Thread Groups independentes (inclui `setUp Thread Group` para preparação, ex.: login único).
- Correlação de dados: extratores CSS/JQuery, JSON, XPath e Regex; parametrização via properties `${__P(...)}` e CSV Data Set.
- Scripting com JSR223 (Groovy), timers (think time), assertions (código, conteúdo, SLA) e config elements (HTTP Defaults, Cookie/Cache Managers).
- Relatório HTML dinâmico gerado do `.jtl` (`-e -o`), com p90/p95/p99, throughput, error% e gráficos; JTL enxuto configurável via `user.properties`.
- Integrações: `jmeter-maven-plugin` (roda `.jmx` no `mvn verify`), CI (GitHub Actions headless), Backend Listener → InfluxDB/Grafana, serviços de nuvem compatíveis.
- Tipos de teste suportados: **caixa-preta** (contrato/resposta sob carga, sem olhar o código); o JMX não acessa detalhes internos da aplicação.

## Demonstração

Exemplo em `src/`: plano `openwebui-navegacao.jmx` contra o OpenWebUI 0.9.2 local (Docker, `http://localhost:8080`), **só navegação + APIs leves** (sem geração de LLM). 1 login no Setup publica o JWT como property global; o Thread Group principal executa 9 samplers (`/`, `/health`, `/api/version`, `/api/config`, `/manifest.json`, `/api/v1/models`, `/api/v1/chats/list`, `/api/v1/prompts/list`, `/ollama/api/tags`) com think time aleatório e assertions de SLA.

Resultado de referência (01/10/2026, 50 threads / ramp 50s / 10 loops): **4501 amostras, 0,87% de erro** — todos estouros de SLA no pico do ramp-up (app single-process), média geral 74ms. Achados: rate-limit no login (429 com bloqueio de ~60s → motivou o login único) e `304 Not Modified` do cache da SPA (assertion aceita 200 ou 304).

Detalhes de execução em [`src/README.md`](src/README.md).

## Ferramentas similares

- **Gatling** — carga via DSL em Scala/Java, relatórios HTML próprios; mesmo nível (protocolo/HTTP).
- **k6** — scripts em JavaScript, foco em CI e thresholds como código; mesmo nível.
- **Locust** — cenários em Python, distribuído; mesmo nível.
- **BlazeMeter** — plataforma SaaS construída em torno do JMeter (execução em nuvem de planos `.jmx`).

## Vantagens e Desvantagens

**Vantagens:** gratuito e open source (Apache); 100% Java e portátil; maduro, com documentação extensa e grande comunidade; protocolo-agnóstico (não só HTTP); parametrizável sem editar o plano (`-J` + `${__P}`); relatório apresentável sem ferramenta extra; bom para CI headless.

**Desvantagens:** GUI consome recursos — carga real exige CLI e disciplina (sem View Results Tree); não executa JavaScript (não serve para SPAs pesadas no client-side); consumo de CPU/memória por thread exige máquina dedicada (JMeter e app na mesma máquina invalidam números absolutos); scripts `.jmx` (XML) têm merge difícil no git; relatórios default exigem leitura crítica (média esconde cauda — usar p95).

## Casos de sucesso

- O próprio ecossistema valida a ferramenta: serviços comerciais de performance (ex.: BlazeMeter) foram construídos para executar planos `.jmx` em nuvem.
- Adoção ampla em pipelines CI para gates de performance (`mvn verify` falhando por SLA) e em testes de APIs/Spring Boot com perfil isolado e Actuator como observabilidade.
- Nesta disciplina: o plano deste trabalho revelou um controle real de segurança (rate-limit 429) e comportamento de cache (304) do alvo — teste de carga também como teste de resiliência.

## Conclusão

Adotar o JMeter quando: o alvo fala protocolo (HTTP/API), é preciso quantificar capacidade (p95, throughput, error%) e há pipeline para rodar headless com gates de SLA. Não adotar quando: a validação exige navegador real/JS, ou quando só há a máquina do desenvolvedor (números não serão confiáveis). Recomendação prática: smoke de 1–2 threads validando contrato → carga com ramp-up respeitando rate-limits → leitura por percentis, repetindo 3x e descartando a primeira rodada.

## Instruções para execução do exemplo

Pré-requisitos: Java 8+ e JMeter 5.6.3+ acessível via `JMETER` ou `PATH`, além do alvo no ar (`http://localhost:8080`). Ver o passo a passo completo em [`src/README.md`](src/README.md):

```bash
cd src/
export JMETER=/opt/apache-jmeter-5.6.3/bin/jmeter   # se jmeter não estiver no PATH
export OWUI_EMAIL="..." OWUI_PASSWORD="..."          # credenciais (nunca versionar)
./run.sh smoke    # 2 threads, valida contrato
./run.sh carga    # 50 threads / ramp 50s / 10 loops
```

Relatórios HTML saem em `src/report-smoke/` e `src/report-carga/` (regeneráveis de qualquer `.jtl` com `./run.sh relatorio <arquivo.jtl> <pasta>`).
