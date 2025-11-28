# Workshop – Demonstração de Uso do SQLMap

## 📌 Objetivo do Trabalho

O objetivo foi demonstrar, na prática, como utilizar o SQLMap para identificar e explorar vulnerabilidades de **SQL Injection** em uma aplicação web segura para testes.  
Para isso, utilizamos o **OWASP Juice Shop**, que é um ambiente oficial da OWASP criado especificamente para aprendizado e simulação ética de falhas de segurança.

---

## 🧃 Por que utilizei o OWASP Juice Shop?

Escolhemos o Juice Shop porque ele contém vulnerabilidades reais de propósito, incluindo SQL Injection, XSS, SSRF e outras.  
Ele permite:

- Testes totalmente seguros
- Reprodução exata da vulnerabilidade
- Uso real de requisições capturadas no Burp Suite
- Demonstração fiel do funcionamento do SQLMap

---

## ⚙️ Ferramenta Apresentada: SQLMap

O **SQLMap** é uma ferramenta de código aberto usada para detectar e explorar vulnerabilidades de SQL Injection automaticamente.  
Ele realiza:

- Enumeração de bancos de dados
- Testes boolean-based, time-based, UNION-based
- Extração de tabelas e colunas
- Identificação de comportamento vulnerável
- Automação completa das etapas de exploração

---

## ⚙️ Ferramentas Utilizadas

- **SQLMap (Kali Linux)**
- **Burp Suite** para capturar requisições
- **Arquivo `query.txt`** contendo a requisição HTTP capturada
- **Banco de dados SQLite** como alvo (indicado pelo próprio teste)

---

## 🚀 Passo a Passo Executado

### **1. Captura da requisição**

A requisição **POST** da aplicação foi capturada no **Burp Suite** e salva no arquivo: **`query.txt`**

Essa requisição continha o parâmetro: **`email`** que foi posteriormente testado como possível alvo de injeção.

---

### **2. Execução do SQLMap**

O comando utilizado foi:

```bash
sqlmap -r query.txt -p "email" --ignore-code=401 --dbms=sqlite --dbs --level=3 --risk=3 --time-sec=2 --batch
```

#### **Explicação resumida do comando**

- `-r query.txt` → utiliza a requisição capturada
- `-p "email"` → testa o parâmetro "email"
- `--ignore-code=401` → ignora respostas não autorizadas
- `--dbms=sqlite` → direciona os testes para SQLite
- `--dbs` → lista os bancos de dados
- `--level=3` e `--risk=3` → aumenta profundidade e agressividade dos testes
- `--batch` → executa sem pedir confirmações

---

## Resultado do Teste

O SQLMap identificou:

### ✔️ **Vulnerabilidade confirmada no parâmetro `email`**

**Tipo detectado:**  
🔸 _Boolean-Based Blind SQL Injection_

O SQLMap indicou que o parâmetro `'JSON email'` é injetável por meio de payloads baseados em:

- Cláusulas **WHERE/HAVING**
- Testes booleanos (`OR 1=1`, `OR 1=2`)
- Testes específicos para **SQLite**
- Tentativas de **stacked queries**
- Tentativas de **UNION queries**
- Testes **time-based**

Esses resultados demonstram que a aplicação responde de forma diferente dependendo do payload, permitindo exploração mesmo sem retornar dados diretamente.

---

## O que é uma Boolean-Based Blind SQL Injection?

É um tipo de SQL Injection **“cego”**, onde o atacante descobre informações por meio de **respostas verdadeiras ou falsas** do servidor, sem ver diretamente os dados do banco.

Esse tipo de injeção permite:

- Descobrir bancos de dados
- Enumerar tabelas
- Extrair dados lentamente
- Contornar filtros simples

---

## Conclusão

Os testes realizados mostram que a aplicação apresenta uma **vulnerabilidade crítica** no parâmetro `email`, permitindo ataques de SQL Injection.  
Essa falha pode levar ao comprometimento do banco, extração de dados sensíveis e impacto na integridade e disponibilidade da aplicação.

O uso do SQLMap demonstrou:

- Como identificar vulnerabilidades automaticamente
- Como diferentes técnicas de exploração são aplicadas
- A importância da validação e sanitização das entradas do usuário

Este trabalho reforça a necessidade de implementar medidas de segurança como:

- **Prepared Statements**
- **Validação e sanitização de inputs**
- **Políticas de acesso ao banco**
- **Monitoramento de requisições**
