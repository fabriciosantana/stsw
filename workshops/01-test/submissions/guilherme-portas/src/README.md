# Exemplo JMeter — OpenWebUI (http://localhost:8080)

Alvo validado em 01/10/2026: container `open-webui` healthy, versão `0.9.2`.
Login `POST /api/v1/auths/signin` retorna `token` JWT (testado com curl, HTTP 200).

## Arquivos

- `openwebui-navegacao.jmx` — plano principal
- `run.sh` — runner CLI (smoke / carga / relatorio)
- `user.properties` — JTL enxuto + percentis p90/p95/p99

## Pré-requisitos

- Java 8+ e JMeter 5.6.3+: no `PATH` ou via `export JMETER=/caminho/para/bin/jmeter`
- Alvo no ar em `http://localhost:8080`
- Credenciais via ambiente (nunca versionar senha):
  ```bash
  export OWUI_EMAIL="..." OWUI_PASSWORD="..."
  ```

## Cenário

Foco: **só navegação + APIs leves** (sem `/chat/completions`, sem LLM gerando texto).

0. `Setup Auth` (setUp Thread Group, 1 thread): `POST /api/v1/auths/signin` 1x, publica o JWT como property global `auth_token` (JSR223 Groovy `props.put`). Evita 50 logins simultâneos — ver achado de rate-limit abaixo.
1. Loop Navegação (`loops=10` por thread, token via `${__P(auth_token)}`):
   - `02 GET /` (SPA — aceita `200` ou `304`, ver Cache Manager abaixo)
   - `02 GET /` (SPA)
   - `03 GET /health` (assert contém `status`)
   - `04 GET /api/version`
   - `05 GET /api/config`
   - `06 GET /manifest.json`
   - `07 GET /api/v1/models` (auth)
   - `08 GET /api/v1/chats/list` (auth)
   - `09 GET /api/v1/prompts/list` (auth)
   - `10 GET /ollama/api/tags` (auth)
- Think time: Uniform Random (`think_time=300ms` + até 500ms aleatório)
- SLAs: health/version <1000ms, models/chats <2000ms, home/ollama <3000ms, login <2000ms

Parametrização via properties: `host, port, protocol, threads, ramp, loops, think_time, email, password`.

## Como rodar

```bash
cd src/
chmod +x run.sh
./run.sh smoke    # 2 threads, valida contrato
./run.sh carga    # 50 threads / ramp 50s / 10 loops
```

Rapidez/rampa: 50 threads em 50s = 1 thread/s. Total aproximado: 1 login (setup) + 50×10×9 = 4500 amostras de navegação.

Relatório HTML sai em `report-smoke/` e `report-carga/` (ignorado pelo git; regenerável de qualquer `.jtl` com `./run.sh relatorio <arquivo.jtl> <pasta>`). Métricas que importam: p90/p95/p99, throughput req/s, error%, latency vs elapsed.

## Limites conhecidos

- **Rate-limit no login (achado do teste):** 50 logins concorrentes retornam `429 Too Many Requests` com bloqueio de ~60s. Por isso o plano loga **1x no Setup** e compartilha o token. Não voltar a login-por-thread sem aumentar o ramp para minutos.
- **Cache (`304`):** o Cache Manager revalida a SPA e o servidor responde `304 Not Modified` a partir da 2ª iteração da mesma thread — a assertion da home aceita `200 OU 304`, comportamento correto de browser.
- Mesmo usuário nas 50 threads: token compartilhado é suficiente para navegação/APIs leves; para teste de escrita concorrente real, usar 50 usuários distintos via CSV Data Set.
- JMeter e Docker na mesma máquina disputam CPU — válido como comparativo, não como número absoluto.

## Resultado da carga de referência (01/10/2026)

`./run.sh carga` — 50 threads / ramp 50s / 10 loops = **4501 amostras, 0,87% erro (39)**.
Todos os 39 erros são **estouro de SLA com HTTP 200/304** (~4,3–5,1s no pico do ramp-up),
concentrados em `/health`, `/`, `/api/config` e `/api/v1/models`. Leitura: o OpenWebUI
single-process enfileira no burst inicial e recupera em seguida (avg geral 74ms, throughput ~42 req/s).
Se o SLA de 1s no `/health` for contratual, o próximo experimento é ramp 120s ou réplicas do container.

## Próximo passo IA

Agente pode: parsear `carga.jtl`, apontar sampler com p95 > SLA ou error% > 1%, cruzar com `docker stats/logs` e sugerir novo `-Jthreads/-Jramp` ou tuning no OpenWebUI/Ollama.
