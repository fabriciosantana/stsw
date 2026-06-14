# Roteiro da demonstração prática de SSRF

> [!IMPORTANT]
> Esta demonstração usa somente `127.0.0.1`. O servidor do laboratório bloqueia destinos externos e limita as portas que podem ser consultadas.

## Objetivo

Mostrar como uma aplicação vulnerável a SSRF pode fazer requisições em nome de um usuário e alcançar um serviço que simula estar na rede interna.

## Estrutura preparada

```text
SSRFmap_demo/
├── .venv/
├── SSRFmap/
├── lab_server.py
├── request.txt
└── roteiro.md
```

O laboratório inicia:

- aplicação vulnerável em `127.0.0.1:5000`;
- serviço interno simulado em `127.0.0.1:8000`;
- portas fechadas de comparação: `6379` e `8001`.

## 1. Explicação inicial

Fala sugerida:

> SSRF significa Server-Side Request Forgery. A vulnerabilidade acontece quando uma aplicação recebe uma URL e faz a requisição sem validar corretamente o destino. Com isso, alguém pode tentar fazer o servidor acessar recursos internos em seu nome.

## 2. Iniciar o laboratório

Abra o primeiro terminal na pasta `SSRFmap_demo` e execute:

```powershell
.\.venv\Scripts\python.exe .\lab_server.py
```

Mantenha esse terminal aberto. Ele mostrará as requisições recebidas durante a demonstração.

Fala sugerida:

> Este servidor foi criado deliberadamente com SSRF e está sendo executado em um laboratório local. Para manter a demonstração isolada, ele aceita somente destinos de loopback e uma lista fixa de portas.

## 3. Mostrar a requisição capturada

O arquivo `request.txt` contém:

```http
GET /ssrf?url=http://example.invalid HTTP/1.1
Host: 127.0.0.1:5000
Connection: close
```

Explique:

- `Host` identifica a aplicação vulnerável.
- `url` é o parâmetro que o SSRFmap substituirá pelos payloads.
- O valor inicial é apenas um marcador e não será acessado.

## 4. Executar o SSRFmap

Abra um segundo terminal na pasta `SSRFmap_demo` e execute:

```powershell
.\.venv\Scripts\python.exe .\SSRFmap\ssrfmap.py -r .\request.txt -p url -m portscan_demo
```

Explique os argumentos antes de pressionar Enter:

- `-r .\request.txt`: carrega a requisição HTTP capturada.
- `-p url`: indica o parâmetro vulnerável que será substituído.
- `-m portscan_demo`: executa somente a varredura curta e local preparada para o seminário.

> [!WARNING]
> Não omita `-m`. Na versão atual do SSRFmap, a ausência desse argumento faz a ferramenta tentar executar todos os módulos disponíveis.

## 5. Interpretar o resultado

A saída esperada é semelhante a:

```text
127.0.0.1:5000  open
127.0.0.1:8000  open
127.0.0.1:6379  closed
127.0.0.1:8001  closed
```

Fala sugerida:

> O SSRFmap não se conecta diretamente a essas portas. Ele altera o parâmetro `url` e envia as requisições para a aplicação vulnerável na porta 5000. É essa aplicação que tenta acessar as portas locais e devolve as respostas. As diferenças nas respostas permitem inferir quais portas estão abertas.

O terminal do servidor também exibirá várias chamadas a `/ssrf`, reforçando visualmente que as requisições passaram pela aplicação vulnerável.

## 6. Encerramento

Fala sugerida:

> A demonstração mostra como uma SSRF pode transformar uma aplicação web em um intermediário para alcançar serviços internos. O SSRFmap automatiza a geração dos payloads e a análise das respostas, mas depende da identificação prévia do parâmetro vulnerável. Entre as principais defesas estão validar destinos, permitir apenas protocolos e hosts necessários, bloquear endereços internos e aplicar controles de saída de rede.

Encerre o servidor no primeiro terminal com `Ctrl+C`.

## Preparação antes do seminário

1. Feche VPNs ou proxies que possam interferir no tráfego local.
2. Execute a demonstração uma vez antes da apresentação.
3. Confirme que as portas `5000` e `8000` estão livres.
4. Deixe os dois terminais abertos na pasta `SSRFmap_demo`.
5. Aumente o tamanho da fonte dos terminais para a plateia acompanhar.

### Alerta do Windows Defender

O repositório original contém `SSRFmap/data/cmd.jsp`, um webshell usado pelo módulo ofensivo `tomcat`. O Windows Defender pode identificá-lo como `Backdoor:PHP/Remoteshell.V` e colocá-lo em quarentena.

Esse arquivo **não é necessário** para o `portscan_demo`. Mantenha-o em quarentena e não selecione as opções de restaurar ou permitir no dispositivo. A demonstração preparada continua funcionando sem ele.

Evite executar módulos diferentes de `portscan_demo`, especialmente o módulo `tomcat`.

Para verificar se uma porta já está ocupada:

```powershell
Get-NetTCPConnection -State Listen -LocalPort 5000,8000 -ErrorAction SilentlyContinue
```
