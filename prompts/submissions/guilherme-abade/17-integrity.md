# Prompt 17: Software and Data Integrity Failures (A08:2021)

**Aluno:** Guilherme Abade
**Referência:** [OWASP A08:2021](https://owasp.org/Top10/A08_2021-Software_and_Data_Integrity_Failures/)

Os exemplos de pipeline e de ferramentas destinam-se a laboratório próprio.

## 1. Introdução

A categoria *Software and Data Integrity Failures*, nova no Top 10 de 2021, reúne as falhas em que a aplicação não verifica a integridade e a origem de software, dados ou atualizações, confiando em código, bibliotecas, *plugins* ou dados passíveis de adulteração. Os principais problemas abrangem o carregamento de bibliotecas e atualizações de fontes não confiáveis ou sem verificação de assinatura e de hash, a desserialização de dados não confiáveis, os pipelines de CI/CD sem controle de integridade sobre o que é construído e publicado e a confiança em dados do cliente para decisões sensíveis.

Carregar atualizações ou *plugins* sem verificação de origem e de integridade representa risco porque, na ausência de checagem de quem publicou e de confirmação de que o conteúdo não foi alterado, um atacante que comprometa o canal de distribuição ou o repositório injeta código malicioso, executado com a confiança da aplicação legítima, no que se denomina ataque de cadeia de suprimentos. Essas falhas viabilizam diferentes ataques: os dirigidos ao pipeline de CI/CD comprometem o processo de construção e implantação para inserir código no artefato final; a confusão de dependências consiste em publicar, em um repositório público, um pacote com o mesmo nome de um pacote interno e versão superior, induzindo o gerenciador a baixar o do atacante; a desserialização insegura permite executar código ou alterar a lógica a partir de objetos não confiáveis.

Aceitar bibliotecas e atualizações sem verificação equivale a conceder execução a um desconhecido. As consequências da desserialização de dados não confiáveis incluem a execução remota de código, a elevação de privilégio, a repetição de requisições, a adulteração de dados e a negação de serviço. Os sistemas mais críticos são os de CI/CD, de contêineres e de nuvem, pois um comprometimento nesses pontos se propaga a todos os artefatos e ambientes gerados, com grande alcance.

## 2. Impactos

Uma biblioteca maliciosa ou adulterada executa no mesmo processo da aplicação e pode ler ou alterar dados sensíveis, exfiltrar segredos e modificar o comportamento sem que o código próprio mude. Convém distinguir integridade de confidencialidade: esta impede que o dado seja visto, enquanto aquela garante que ele não seja alterado sem autorização e que a origem seja autêntica; um dado pode permanecer secreto e, ainda assim, ser adulterado, hipótese que a falha de integridade atinge diretamente.

Quando uma atualização automática traz código adulterado, ele executa com os privilégios da aplicação em todos os clientes que atualizarem, padrão observado no ataque à SolarWinds. Essas falhas relacionam-se com os princípios de integridade, disponibilidade e autenticidade, visto que um comprometimento pode derrubar ou sabotar o serviço e abala a certeza sobre a origem. Os prejuízos financeiros, decorrentes da resposta ao incidente, da parada e de multas, somam-se aos reputacionais, pela perda de confiança de clientes e parceiros, muitas vezes superiores ao dano técnico imediato.

## 3. Exemplos reais

No caso do pacote `event-stream`, no ecossistema Node.js em 2018, um mantenedor transferiu a manutenção de um pacote popular a um terceiro mal-intencionado, que acrescentou uma dependência com código ofuscado dirigido a roubar carteiras de criptomoeda de uma aplicação específica; o vetor combinou o comprometimento do mantenedor e uma dependência transitiva maliciosa em um pacote de confiança. No ataque à SolarWinds, em 2020, os atacantes comprometeram o processo de construção e inseriram um mecanismo de acesso persistente em uma atualização legitimamente assinada do produto Orion, distribuída a cerca de 18 mil clientes; a falha não foi uma vulnerabilidade de código, mas a ausência de garantia de integridade no pipeline de construção e distribuição, de modo que o artefato malicioso foi assinado e distribuído como legítimo.

A confusão de dependências, demonstrada em pesquisa de 2021, publicou pacotes públicos com nomes de pacotes internos de empresas e versão superior, induzindo os gerenciadores a baixar o pacote do pesquisador em lugar do interno, o que atingiu grandes companhias; a causa esteve na priorização da maior versão sem distinção entre a origem interna e a pública. Os mecanismos de acesso persistente introduzidos via CI/CD, por sua vez, decorrem de segredos expostos no pipeline, de dependências de construção não verificadas ou de etapas que baixam scripts sem checagem; os erros comuns nesses casos foram a falta de assinatura e de verificação de artefatos, o excesso de confiança em terceiros e a ausência de isolamento e de controle de integridade na construção.

## 4. Prevenção e boas práticas

A assinatura digital de pacotes e binários, acompanhada de sua verificação antes do uso, garante origem e integridade, por meio de ferramentas como Sigstore e GPG. A verificação de hash e o uso de integridade de sub-recursos protegem recursos de terceiros no frontend. Os arquivos de versões travadas, fixando versões e hashes, e o uso de um repositório interno confiável reduzem a superfície. A análise de composição de software detecta componentes vulneráveis, ao passo que os níveis SLSA e os metadados de proveniência comprovam como e onde o artefato foi construído, o que dificulta a adulteração silenciosa.

Convém ainda restringir o uso de *plugins* e bibliotecas desconhecidas, admitindo apenas fontes aprovadas e removendo o que não se utiliza, endurecer o pipeline de CI/CD com segredos em cofre, menor privilégio, isolamento dos executores, revisão obrigatória, proteção de ramo e verificação de integridade das dependências, e evitar a desserialização de dados não confiáveis, recorrendo, quando inevitável, a formatos com esquema, listas de tipos permitidos e garantia de integridade. O controle de versão e a gestão de dependências asseguram que o construído corresponda exatamente ao revisado e aprovado, por meio de arquivos de versões travadas, hashes e proteção de ramo, fechando a brecha entre o que foi revisado e o que chegou à produção.

## 5. Prática com ferramentas reais

```bash
# OWASP Dependency-Check em projeto Java
mvn org.owasp:dependency-check-maven:check

# Trivy em projeto Node ou Python e em contêiner
trivy fs .
trivy image minha-app:latest

# Snyk para dependências e contêineres
snyk test
snyk container test minha-app:latest
```

O pipeline de CI/CD a seguir, em GitHub Actions, incorpora a verificação de integridade.

```yaml
name: ci-seguro
on: [push, pull_request]
permissions:
  contents: read            # menor privilégio
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: '20', cache: 'npm' }

      # instala exatamente o arquivo de versões travadas, verificando a integridade
      - name: Instalar dependências
        run: npm ci

      # falha a construção caso haja dependência vulnerável
      - name: Auditoria de dependências
        run: npm audit --audit-level=high

      - name: Análise de composição com Trivy
        uses: aquasecurity/trivy-action@master
        with: { scan-type: 'fs', severity: 'HIGH,CRITICAL', exit-code: '1' }
```

Entre os arquivos a monitorar no pipeline estão `package-lock.json`, `yarn.lock`, `pom.xml`, `build.gradle`, `requirements.txt`, `poetry.lock`, o `Dockerfile` e as imagens base, cujas alterações devem passar por revisão. Os alertas automatizados incluem a abertura de propostas de atualização por ferramentas apropriadas, a notificação de novas vulnerabilidades e a exigência, pela proteção de ramo, das checagens de segurança antes da integração.

## 6. Aplicação em cenários reais

Quando um desenvolvedor insere uma biblioteca de fonte duvidosa, a equipe de segurança deve bloqueá-la por política, admitindo apenas repositórios aprovados, exigir a aprovação pela análise de composição e pela revisão, avaliar a manutenção, a reputação e a licença do pacote e registrar a decisão, sendo que a proteção de ramo impede a integração sem as checagens. A política organizacional de controle de bibliotecas externas prevê uma fonte única e aprovada, uma lista de dependências permitidas com processo de aprovação para novas, a análise de composição obrigatória com barreira por severidade, arquivos de versões travadas com hash e SBOM a cada construção, a revisão de licenças e de manutenção e a remoção periódica de dependências não utilizadas ou sem suporte. A avaliação de um ambiente de CI/CD quanto à integridade examina os segredos expostos em logs e variáveis, os executores compartilhados sem isolamento, as etapas que baixam scripts sem verificação, as permissões amplas do token do pipeline, a falta de assinatura e de proveniência dos artefatos e a ausência de proteção de ramo e de revisão obrigatória.

## 7. Conclusão

As falhas de integridade de software e dados surgem quando se confia em código, dados ou atualizações sem verificar a origem e a integridade, e constituem o cerne dos ataques de cadeia de suprimentos, como os casos `event-stream`, SolarWinds e confusão de dependências, que exploram a confiança na cadeia e têm alcance considerável. Para garantir a integridade em produção, o desenvolvedor deve assinar e verificar artefatos, fixar versões e hashes, empregar análise de composição e SBOM, endurecer o pipeline com menor privilégio, isolamento, revisão e proteção de ramo, evitar a desserialização de dados não confiáveis e adotar os níveis SLSA e a proveniência. As assinaturas garantem a origem, o versionamento assegura que o construído corresponde ao revisado e os controles no CI/CD impedem a adulteração no caminho do código até a produção, fechando, em conjunto, a janela em que um atacante poderia injetar código entre a aprovação e a entrega.
