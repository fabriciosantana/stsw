# 🔓 Vulnerable App

Aplicação Node.js intencionalmente vulnerável para demonstração e aprendizado de segurança.

## ⚠️ AVISO

**Esta aplicação contém vulnerabilidades intencionais para fins educacionais. NUNCA use em produção!**

## 🎯 O Que Esta Aplicação Demonstra

- **Dependências vulneráveis** (Express 4.16.0 com múltiplas CVEs)
- **XSS** (Cross-Site Scripting)
- **Path Traversal**
- **SQL Injection** (simulado)
- **IDOR** (Insecure Direct Object Reference)
- **DoS** (Denial of Service)

## 🚀 Instalação e Execução

```bash
npm install
npm start
```

O servidor será iniciado em **http://localhost:3001**

### Interface Web

Acesse `http://localhost:3001` no navegador para ver:
- Interface HTML com informações sobre vulnerabilidades
- Formulários para testar XSS
- Links para testar outras vulnerabilidades
- Lista de endpoints vulneráveis

### Testar Vulnerabilidades

1. **XSS**: `http://localhost:3001/?name=<script>alert('XSS')</script>`
2. **Path Traversal**: `http://localhost:3001/file?path=package.json`
3. **SQL Injection**: `http://localhost:3001/search?q=' OR '1'='1`
4. **IDOR**: `http://localhost:3001/user/1`

📖 Veja mais detalhes em `SOBRE-APLICACAO-VULNERAVEL.md`

## OWASP Dependency-Check

### 🧰 Pré-requisitos

**Java 11+ é obrigatório** para executar o Dependency-Check.

Verifique se o Java está instalado:
```bash
java -version
```

Se não tiver, baixe do site oficial: https://adoptium.net

### 📥 Instalação

#### 🪟 Windows

**Opção 1 — Via ZIP (Recomendada)**

1. Acesse o repositório oficial:
   - https://github.com/jeremylong/DependencyCheck/releases

2. Baixe o arquivo ZIP da versão mais recente:
   - Exemplo: `dependency-check-9.1.0-release.zip`

3. Extraia o conteúdo em uma pasta:
   - Exemplo: `C:\Tools\dependency-check\`

4. Adicione ao PATH do Windows:
   - Pesquise "Variáveis de ambiente" no Windows
   - Edite a variável PATH
   - Adicione: `C:\Tools\dependency-check\bin`

5. Teste no terminal (PowerShell ou CMD):
   ```powershell
   dependency-check.bat --version
   ```
   Deve aparecer algo como: `Dependency-Check Core version 9.1.0`

**Opção 2 — Via Chocolatey**
```powershell
choco install dependency-check
```

**Opção 3 — Script Automático**
Execute o script fornecido no projeto:
```powershell
.\install-dependency-check-auto.ps1
```

#### 🐧 Linux ou WSL

**Opção 1 — Via Homebrew (Mais Simples)**
```bash
brew install dependency-check
```

**Opção 2 — Manual**
```bash
wget https://github.com/jeremylong/DependencyCheck/releases/latest/download/dependency-check.zip
unzip dependency-check.zip -d /opt/dependency-check
sudo ln -s /opt/dependency-check/bin/dependency-check.sh /usr/local/bin/dependency-check
```

Teste:
```bash
dependency-check --version
```

#### 🍎 macOS

```bash
brew install dependency-check
```

Teste:
```bash
dependency-check --version
```

#### 🐳 Docker (Alternativa)

Se você já tem Docker instalado, essa é uma forma rápida sem precisar mexer no PATH:

```bash
docker run --rm \
  -v $(pwd):/src \
  owasp/dependency-check \
  --project "vulnerable-app" --scan /src --format HTML --out /src/report
```

Isso cria o relatório dentro da pasta atual (`./report`).

### 🔑 NVD API Key (Altamente Recomendado)

O Dependency-Check precisa de uma **API key gratuita do NVD** (National Vulnerability Database) para funcionar corretamente. Sem ela, o scan pode falhar.

**Como obter (gratuito, leva 2 minutos):**

1. Acesse: https://nvd.nist.gov/developers/request-an-api-key
2. Preencha o formulário com:
   - Nome completo
   - Email
   - Organização (opcional)
3. Você receberá a chave por email em poucos minutos
4. Defina a variável de ambiente:

**Windows (PowerShell):**
```powershell
$env:NVD_API_KEY="sua-chave-aqui"
```

**Linux/macOS:**
```bash
export NVD_API_KEY="sua-chave-aqui"
```

**Para tornar permanente no Windows:**
```powershell
[Environment]::SetEnvironmentVariable("NVD_API_KEY", "sua-chave-aqui", "User")
```

**Para tornar permanente no Linux/macOS:**
Adicione ao `~/.bashrc` ou `~/.zshrc`:
```bash
export NVD_API_KEY="sua-chave-aqui"
```

### 🧪 Execução do Scan

**Via Script Automático (Recomendado):**
```bash
node scan.js
```

**Via Linha de Comando:**

No Windows:
```powershell
dependency-check.bat --project "vulnerable-app" --scan ./ --format HTML --out ./report
```

No Linux/macOS:
```bash
dependency-check.sh --project "vulnerable-app" --scan ./ --format HTML --out ./report
```

**Parâmetros:**
- `--project "vulnerable-app"` - Nome do projeto no relatório
- `--scan ./` - Escaneia o diretório atual (onde está o package.json)
- `--format HTML` - Gera relatório em formato HTML
- `--out ./report` - Salva o relatório na pasta `./report`

### 📄 Visualização do Relatório

Após a execução, o relatório será gerado em:

```
./report/dependency-check-report.html
```

**Para visualizar:**
1. Abra o arquivo `./report/dependency-check-report.html` no seu navegador
2. O relatório mostrará todas as vulnerabilidades encontradas nas dependências
3. Você verá detalhes sobre o Express 4.16.0 e suas vulnerabilidades conhecidas

### 📊 Exemplo de Saída Esperada

O Dependency-Check identificará vulnerabilidades no Express 4.16.0, como:
- **CVEs** relacionados a versões antigas do Express
- **CVSS scores** (severidade das vulnerabilidades)
- **Links** para mais detalhes sobre cada vulnerabilidade
- **Dependências afetadas** (body-parser, cookie, qs, path-to-regexp, etc.)

### 💡 Dicas Importantes

- **Primeira execução**: O Dependency-Check baixa o banco de dados de vulnerabilidades da NVD — pode demorar 5 a 10 minutos. As próximas vezes serão bem mais rápidas.

- **Atualização do banco**: Execute periodicamente para manter o banco de dados atualizado:
  ```bash
  dependency-check.bat --updateonly  # Windows
  dependency-check.sh --updateonly   # Linux/macOS
  ```

