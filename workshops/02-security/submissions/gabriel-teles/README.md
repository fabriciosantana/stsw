# 🛡️ Trivy - Scanner de Vulnerabilidades Universal (v0.52.2)

**Alunos:** Gabriel Teles e Lucas Barreto
**Data:** 28 de Novembro de 2025

## Introdução

**Trivy** é um *scanner* de segurança de código aberto mantido pela Aqua Security. Ele é reconhecido pela sua **simplicidade** e **velocidade**, fornecendo uma análise abrangente de vulnerabilidades e configurações incorretas em diversos alvos.

Ele se posiciona no topo da **pirâmide de automação de testes**, atuando no nível de **Segurança Estática de Aplicação (SAST)** e **Análise de Composição de Software (SCA)**. O Trivy é uma ferramenta fundamental na abordagem **DevSecOps**, permitindo que as equipes de desenvolvimento "movam a segurança para a esquerda" (*shift-left*), identificando problemas antes do *deploy*.

## Principais Funcionalidades

O Trivy é um *scanner* "tudo-em-um" com uma ampla gama de recursos:

  * **Recursos Suportados (Alvos):**
      * **Imagens de Contêiner** (Docker, OCI).
      * **Sistemas de Arquivos** e Repositórios Git.
      * **Configurações de Kubernetes** (YAML, Kustomize).
      * **Infraestrutura como Código (IaC):** Terraform, CloudFormation, Ansible.
      * **Dependências de Linguagem:** Go, Java (Maven/Gradle), Python (pip), Node.js (npm/yarn), Ruby, PHP, etc.
  * **Tipos de Testes Possíveis:**
      * **Caixa-Branca:** Analisa dependências e código-fonte, detectando vulnerabilidades conhecidas (CVEs).
      * **Caixa-Preta:** Varredura em imagens de contêiner sem acesso ao código-fonte, focando nos pacotes instalados.
      * **Análise de Configuração:** Verifica *misconfigurations* (Configuração Incorreta) e Segredos.
  * **Integrações Disponíveis:**
      * **CI/CD:** Código de saída não zero para falhar *builds* (Jenkins, GitLab CI, GitHub Actions).
      * **Registries de Contêiner:** Docker Hub, Amazon ECR, Google Artifact Registry.
      * **Formatos de Saída:** Tabela, JSON, SARIF (para integração com GitHub Code Scanning).

## Demonstração

O exemplo implementado é um *script* `bash` que demonstra a varredura de **Imagens de Contêiner** Docker, que é um dos casos de uso mais comuns do Trivy. O *script* executa dois cenários:

1.  **SUCESSO:** Varredura da imagem `alpine:latest`, que deve retornar zero vulnerabilidades **CRITICAL/HIGH**.
2.  **FALHA:** Varredura da imagem antiga `nginx:1.21.6-alpine`, que intencionalmente contém vulnerabilidades conhecidas para demonstrar a falha em um *pipeline* de CI/CD.

## Lista de Frameworks Similares

Ferramentas que atuam no mesmo nível da pirâmide (SAST/SCA) ou com propósito semelhante:

  * **Anchore Grype:** Scanner de vulnerabilidades *open-source* com foco em imagens de contêiner.
  * **Clair:** Ferramenta mais antiga desenvolvida pela CoreOS, focada apenas em imagens de contêiner.
  * **Snyk:** Plataforma comercial robusta que cobre vulnerabilidades em código, dependências e IaC.
  * **OWASP Dependency-Check:** Ferramenta de SCA focada em dependências de código (Java, .NET, etc.).

## Vantagens e Desvantagens

| Vantagens | Desvantagens |
| :--- | :--- |
| **Simplicidade e Curva de Aprendizado Baixa:** Binário único e fácil de instalar. | **Falsos Positivos:** Pode ocorrer em dependências não utilizadas ou em pacotes de OS (exige ajuste de filtros). |
| **Velocidade:** Conhecido por ser um dos scanners mais rápidos, ideal para *pipelines* de CI/CD. | **Cobertura de Código-Fonte:** Para análise profunda de vulnerabilidades *no código* (e não nas dependências), outras ferramentas SAST dedicadas podem ser necessárias. |
| **Abrangência (*Universal*):** Suporte a Imagens, IaC, Kubernetes, Dependências, e Segredos. | **Atualização de DB:** Depende da atualização constante de sua base de dados para garantir a precisão. |
| **Documentação:** Ampla e de alta qualidade mantida pela comunidade e Aqua Security. | |

## Casos de Sucesso

O Trivy é amplamente utilizado na comunidade *cloud-native*. Algumas empresas e projetos que o utilizam ou o recomendam incluem:

  * **Aqua Security:** Mantenedora da ferramenta, que o integra em sua plataforma.
  * **Projetos CNCF (Cloud Native Computing Foundation):** É uma ferramenta comum em *pipelines* DevSecOps de projetos que utilizam Kubernetes e contêineres.
  * **Empresas de Tecnologia e Startups:** Adotado por inúmeras equipes de engenharia por sua facilidade de integração em ambientes de CI/CD.

## Conclusão

O Trivy é a **ferramenta de linha de comando padrão** para varredura de segurança *shift-left* em ambientes *cloud-native*. Sua velocidade, simplicidade de uso e a abrangência de alvos o tornam uma escolha excelente.

  * **Recomendação de Uso:** Deve ser o **primeiro** scanner a ser implementado em qualquer *pipeline* de CI/CD que lide com contêineres, dependências e/ou IaC.
  * **Quando Adotar:** Sempre que a **velocidade** e a **cobertura ampla** de diferentes tipos de ativos de *build* forem prioritárias.
  * **Quando *Não* Adotar:** Como única solução. Ele deve ser complementado com ferramentas mais robustas para *fuzzing* ou análise dinâmica (DAST) em ambientes de *staging*/produção.



## 🛠️ Instruções para Execução do Exemplo

Para executar os *scripts* de demonstração, você deve ter o **Trivy** instalado em sua máquina. Para a visualização do relatório JSON (Script com Geração de Relatório), é recomendável ter o **`jq`** instalado.

### 1\. Pré-requisitos (Trivy e Opcional `jq`)

| Ferramenta | macOS/Linux (Homebrew) | Ubuntu (snap) | Descrição |
| :--- | :--- | :--- | :--- |
| **Trivy** (Obrigatório) | `brew install trivy` | `sudo snap install trivy` | Scanner de Vulnerabilidades. |
| **`jq`** (Recomendado) | `brew install jq` | `sudo apt install jq` | Processador JSON, usado para exibir o relatório de falha de forma legível. |

*Outras opções de instalação (pacotes DEB, RPM, Docker) estão disponíveis na documentação oficial do Trivy.*

### 2\. Preparação e Permissão de Execução

Certifique-se de que ambos os scripts (`script-trivy.sh` e `script-trivy-report.sh`) estão no diretório correto (`/workshops/NN-WWWW/submissions/seu-nome/src`) e conceda permissão de execução:

```bash
chmod +x script-trivy.sh script-trivy-report.sh
```

### 3\. Execução dos Exemplos

Você pode executar os scripts separadamente para fins de demonstração:

#### **A. Demonstração Padrão (Saída em Tabela)**

Executa a varredura e mostra o resultado direto no terminal (Ideal para a apresentação ao vivo):

```bash
./script-trivy.sh
```

*O script fará o *pull* das imagens e executará a varredura, destacando os casos de sucesso (imagem limpa) e falha (imagem vulnerável).*

#### **B. Demonstração com Geração de Relatório (JSON)**

Executa a varredura e salva os resultados em arquivos JSON no diretório `./trivy_reports` (Ideal para mostrar integração CI/CD):

```bash
./script-trivy-report.sh
```

*Este script irá **criar** a pasta `./trivy_reports` e salvar os relatórios. O script tentará usar o `jq` para exibir um trecho formatado do relatório de falha no final.*