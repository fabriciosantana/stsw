# SSRFmap: automacao de testes de seguranca para SSRF

**Aluno:** Matheus Ventura  
**Disciplina:** Seguranca e Teste de Software  
**Data da apresentacao:** junho de 2026

## Introducao

O [SSRFmap](https://github.com/swisskyrepo/SSRFmap) e uma ferramenta de codigo aberto escrita em Python para automatizar a exploracao controlada de vulnerabilidades Server-Side Request Forgery (SSRF). Ela recebe uma requisicao HTTP capturada, identifica o parametro vulneravel e substitui seu valor por payloads adequados ao modulo selecionado.

Na piramide de automacao de testes, a ferramenta atua principalmente no nivel de servico/API. Sua abordagem e de caixa-preta: o teste manipula requisicoes e analisa respostas sem depender do codigo-fonte da aplicacao avaliada.

## Principais funcionalidades

- Leitura de requisicoes HTTP brutas, inclusive exportadas por proxies como o Burp Suite.
- Substituicao automatica do parametro vulneravel por payloads SSRF.
- Modulos para enumeracao de portas e redes internas, metadados de nuvem e protocolos como Redis, FastCGI e Gopher.
- Opcoes de evasao para testar variacoes de representacao de enderecos IP.
- Arquitetura extensivel por modulos Python.

O SSRFmap e uma ferramenta de seguranca ofensiva e deve ser utilizado somente em ambientes proprios ou com autorizacao explicita.

## Demonstracao

O exemplo em [`src`](./src) cria um laboratorio totalmente local:

- uma aplicacao deliberadamente vulneravel em `127.0.0.1:5000`;
- um servico interno simulado em `127.0.0.1:8000`;
- duas portas fechadas, `6379` e `8001`, para comparacao;
- um modulo customizado, `portscan_demo`, que limita a verificacao a esses quatro destinos locais.

O servidor bloqueia hosts externos, protocolos diferentes de HTTP e portas fora da lista permitida. O roteiro detalhado da apresentacao esta em [`src/roteiro.md`](./src/roteiro.md).

## Requisitos

- Python 3.10 ou superior.
- PowerShell, Prompt de Comando ou outro terminal.
- Portas locais `5000` e `8000` livres.

## Execucao do exemplo

Na raiz desta submissao, crie e ative um ambiente virtual:

```powershell
cd src
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install --upgrade pip
python -m pip install -r .\SSRFmap\requirements.txt
```

No primeiro terminal, inicie o laboratorio:

```powershell
python .\lab_server.py
```

Em um segundo terminal, com o mesmo ambiente virtual ativado, execute:

```powershell
python .\SSRFmap\ssrfmap.py -r .\request.txt -p url -m portscan_demo
```

A saida esperada deve classificar `5000` e `8000` como abertas e `6379` e `8001` como fechadas. Encerre o laboratorio com `Ctrl+C`.

> Nao execute outros modulos durante a demonstracao. Alguns modulos originais possuem comportamento ofensivo e nao sao necessarios para este laboratorio.

## Ferramentas similares

- Burp Suite Professional, com scanner web e Collaborator para SSRF blind.
- Gopherus, focado na geracao manual de payloads Gopher.
- Nuclei, que detecta SSRF por templates YAML e interacoes fora de banda.
- OWASP ZAP, scanner de aplicacoes web com recursos de automacao e extensoes.

## Vantagens e desvantagens

**Vantagens:** automatiza payloads complexos, possui modulos especializados, e extensivel e facilita testes reproduziveis a partir de requisicoes capturadas.

**Desvantagens:** exige que o parametro vulneravel ja tenha sido identificado, possui suporte limitado a SSRF blind sem infraestrutura adicional e inclui modulos ofensivos que requerem cuidado operacional.

## Casos de uso

O SSRFmap e adequado para laboratorios educacionais, validacao autorizada de APIs e aplicacoes web, pentests e verificacao de controles de saida de rede. Referencias publicas frequentemente o apresentam em pesquisas, write-ups e programas de bug bounty, embora nao haja uma lista oficial confiavel de empresas usuarias.

## Conclusao

O SSRFmap e especialmente util quando existe uma requisicao reproduzivel e o objetivo e aprofundar o impacto tecnico de uma SSRF. Ele nao substitui a identificacao inicial da falha nem controles defensivos como allowlists, validacao de DNS/IP, bloqueio de enderecos internos e restricoes de egress. Em producao, seu uso deve ocorrer apenas com escopo, autorizacao e isolamento claramente definidos.

## Apresentacao

Os slides estao em [`matheus-ventura.pdf`](./matheus-ventura.pdf).
