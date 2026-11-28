# Atividade 09: Laboratórios de Security Misconfiguration, Componentes Desatualizados e Logging

**Aluno:** Guilherme Abade

Esta atividade resolve os desafios de configuração insegura e de componentes vulneráveis do OWASP Juice Shop e os laboratórios de divulgação de informação do PortSwigger Web Security Academy, ambientes deliberadamente vulneráveis destinados ao estudo. O Juice Shop foi executado localmente por `docker run --rm -p 3000:3000 bkimminich/juice-shop`, e os laboratórios do PortSwigger foram resolvidos na plataforma oficial. Cada desafio é apresentado com a falha explorada, o procedimento de resolução e a correção correspondente.

## OWASP Juice Shop: Security Misconfiguration

### Provocar um erro tratado de forma pouco elegante e inconsistente

A aplicação não trata de modo uniforme as entradas inesperadas. A solução envia a um endpoint um valor com tipo diferente do esperado, por exemplo um vetor ou um objeto onde se aguarda um texto, o que provoca um erro cuja resposta expõe o rastreamento de pilha e informações internas. A correção adota um tratamento de erro global e consistente, sem rastreamento de pilha ao cliente, com o registro do detalhe apenas no servidor.

### Usar uma interface B2B obsoleta que não foi devidamente desativada

A aplicação mantém um ponto de integração entre empresas que deveria ter sido removido. A solução identifica, no código do cliente, a referência a essa interface e a utiliza, dado que permanece ativa. A correção remove efetivamente os recursos obsoletos e revisa as rotas publicadas, de forma que nenhuma funcionalidade descontinuada continue acessível.

## OWASP Juice Shop: Vulnerable and Outdated Components

### Informar a loja sobre um impostor por typosquatting no frontend

Uma dependência do frontend tem nome muito semelhante ao de um pacote legítimo, caracterizando um pacote impostor instalado por confusão de nome. A solução examina a lista de dependências do cliente, identifica o pacote de nome suspeito e o reporta pelo mecanismo de feedback da aplicação. A correção verifica a origem das dependências, adota uma lista de pacotes aprovados e emprega análise de composição de software.

### Informar a loja sobre uma biblioteca vulnerável em uso

A aplicação utiliza uma biblioteca com vulnerabilidade conhecida. A solução localiza a biblioteca e a versão nas dependências do projeto, confirma a vulnerabilidade associada em uma base pública e a reporta. A correção atualiza a biblioteca para uma versão corrigida e institui a varredura contínua de dependências no pipeline.

## Laboratórios do PortSwigger: Information Disclosure

### Information disclosure in error messages

A aplicação revela, em uma mensagem de erro, a versão de um componente utilizado. A solução provoca o erro ao enviar um valor inválido em um parâmetro e lê a versão exposta na resposta. A correção apresenta mensagens de erro genéricas e não revela detalhes de implementação ao cliente.

### Information disclosure on debug page

Uma página de depuração acessível expõe variáveis de ambiente e informações internas. A solução localiza essa página, referenciada no código ou em um caminho previsível, e lê os dados sensíveis. A correção remove as páginas de depuração dos ambientes acessíveis e restringe o acesso a informações internas.

### Source code disclosure via backup files

Um arquivo de cópia de segurança do código-fonte permanece acessível no servidor. A solução identifica o arquivo com extensão de backup em um diretório conhecido e obtém o código, que contém dados sensíveis. A correção impede o acesso a arquivos de cópia de segurança e os mantém fora do diretório público.

### Authentication bypass via information disclosure

A aplicação divulga, em um cabeçalho de resposta a um método HTTP alternativo, informação que revela como o controle administrativo é verificado. A solução inspeciona essa resposta, descobre o cabeçalho que determina o acesso e o reproduz para alcançar a funcionalidade restrita. A correção não expõe detalhes do mecanismo de autorização e valida o acesso de forma que não dependa de cabeçalhos controláveis pelo cliente.

## Síntese

Os desafios desta atividade reúnem três categorias próximas. A configuração insegura manifesta-se no tratamento inconsistente de erros e em recursos obsoletos não desativados, e corrige-se com tratamento de erro uniforme, remoção do que não se utiliza e revisão das rotas publicadas. Os componentes vulneráveis e desatualizados exigem inventário das dependências, verificação de origem e varredura contínua. A divulgação de informação, por mensagens de erro, páginas de depuração e arquivos de cópia de segurança, corrige-se ao não expor detalhes internos ao cliente e ao restringir o acesso a artefatos sensíveis. Em conjunto, os laboratórios reforçam que padrões seguros e a ocultação de detalhes de implementação são parte essencial da defesa.
