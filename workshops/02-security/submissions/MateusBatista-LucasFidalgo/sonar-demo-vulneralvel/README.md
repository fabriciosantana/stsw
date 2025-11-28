
# 📘 Projeto de Demonstração com SonarQube + Aplicação Vulnerável

**Objetivo:** Demonstrar vulnerabilidades em uma aplicação real e como o **SonarQube** detecta problemas de segurança, qualidade e más práticas de programação.

---

## 🔐 1. O que é o SonarQube?

O **SonarQube** é uma plataforma de análise estática de código (**SAST**) que identifica automaticamente:

- Vulnerabilidades (ex.: SQL Injection, XSS)
- Bugs
- Code Smells (más práticas)
- Security Hotspots
- Problemas de manutenibilidade
- Falhas de cobertura de testes

Ele analisa o código **sem executá-lo**, usando milhares de regras baseadas em **OWASP**, **CWE** e padrões globais de segurança.

### **Principais recursos:**
- Dashboard visual do projeto  
- Análise de segurança automatizada  
- Controle de qualidade com **Quality Gates**  
- Integração com pipelines (CI/CD)  
- Suporte a dezenas de linguagens (Java, JS, Python, Go…)  

📎 **Site oficial:** https://www.sonarqube.org

---

## ⚠️ 2. Descrição da Aplicação Vulnerável

Este projeto contém uma pequena aplicação **Node.js + Express** propositalmente vulnerável para fins educacionais.

### ✔ Vulnerabilidades incluídas:

---

### **1. SQL Injection**

O login concatena diretamente o input do usuário na query SQL:

```javascript
const query = `
  SELECT * FROM users
  WHERE username = '${username}'
    AND password = '${password}'
`;
```

---

### **2. XSS (Cross-Site Scripting)**

O parâmetro `name` é inserido sem sanitização:

```html
<h1>Olá, ${name}</h1>
```

---

### **3. Segredo hardcoded**

```javascript
const SECRET_API_KEY = "MINHA_SUPER_CHAVE_SECRETA_123";
```

---

### **4. Senha em texto plano**

O SQLite salva senhas sem hash (má prática grave).

---

## 🐳 3. Como subir o ambiente com Docker

O projeto inclui um `docker-compose.yml` contendo:

* **Aplicação vulnerável** → porta **3000**
* **SonarQube** → porta **9000**

### Para subir tudo:

```bash
docker-compose up --build
```

### Acesse:

* Aplicação: [http://localhost:3000](http://localhost:3000)
* SonarQube: [http://localhost:9000](http://localhost:9000)

  * Login: **admin / admin**

---

## 🔍 4. Como testar as vulnerabilidades

---

### ✔ 4.1. Testar XSS

Acesse:

```
http://localhost:3000/?name=<script>alert('XSS')</script>
```

Ou URL-encoded:

```
http://localhost:3000/?name=%3Cscript%3Ealert('XSS')%3C%2Fscript%3E
```

Resultado: o navegador exibirá um **alert()**.

---

### ✔ 4.2. Testar SQL Injection

Na página principal, use:

**Username:**

```
admin' OR '1'='1
```

**Password:**

```
abc
```

Resultado: login sem senha válida.

Ou via cURL:

```bash
curl -X POST -d "username=admin' OR '1'='1&password=abc" http://localhost:3000/login
```

---

### ✔ 4.3. Exposição de segredo

Após login:

```
API KEY: MINHA_SUPER_CHAVE_SECRETA_123
```

---

## 📊 5. Como testar o SonarQube

---

### ✔ 5.1. Criar projeto no SonarQube

1. Acesse: [http://localhost:9000](http://localhost:9000)
2. Login: **admin / admin**
3. Vá em **Projects → Create Project → Manually**
4. Use:

   * **Project Key:** `sonar-demo-vulneravel`
   * **Name:** `Sonar Demo Vulnerável`
5. Gere um **Token de análise** (guarde!)

---

### ✔ 5.2. Rodar o SonarScanner

Instale o SonarScanner na sua máquina.

Na raiz do projeto, execute:

```bash
sonar-scanner \
  -Dsonar.projectKey=sonar-demo-vulneravel \
  -Dsonar.sources=src \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=SEU_TOKEN_AQUI
```

Ou, se tiver configurado o `sonar-project.properties`:

```bash
sonar-scanner
```

---

## 📈 6. Ver resultados no SonarQube

No painel do projeto, você verá:

### 🔐 Vulnerabilities

* SQL Injection detectado via análise de fluxo
* Hardcoded secret
* XSS no template HTML
* Senha em texto plano

### 🛑 Security Hotspots

* Uso de expressões perigosas
* Exposição de dados sensíveis

### 🧹 Code Smells

* Más práticas comuns do Node/JS

### ✔ Quality Gate

* Provavelmente marcado como **FAILED**

Mostrando como um pipeline CI/CD bloquearia um deploy inseguro.

---

## 🧪 7. Roteiro sugerido para apresentação

1. Suba tudo com:

   ```bash
   docker-compose up --build
   ```
2. Mostre o **XSS** no navegador
3. Mostre o **SQL Injection** quebrando o login
4. Abra o **SonarQube**
5. Navegue pelas vulnerabilidades
6. Explique o impacto de **Quality Gates** em pipelines
7. Conclua mostrando como SAST ajuda Segurança + QA

---

## 🎯 8. Conclusão

Este projeto demonstra claramente:

* Como vulnerabilidades reais surgem em código
* Como explorá-las em ambiente controlado
* Como o **SonarQube** detecta e classifica riscos
* Como equipes usam Quality Gates para evitar deploys vulneráveis

Ideal para trabalhos sobre **Segurança**, **Pentest**, **DevSecOps**, **Teste de Software** ou **SAST**.

---
