# Atividade 08: Laboratórios de Identificação, Autenticação e Broken Access Control

**Aluno:** Guilherme Abade

Esta atividade resolve os desafios de autenticação e de controle de acesso do OWASP Juice Shop e os laboratórios correspondentes do PortSwigger Web Security Academy, ambientes deliberadamente vulneráveis destinados ao estudo. O Juice Shop foi executado localmente por `docker run --rm -p 3000:3000 bkimminich/juice-shop`, e os laboratórios do PortSwigger foram resolvidos na plataforma oficial. Cada desafio é apresentado com a falha explorada, o procedimento de resolução e a correção correspondente.

## OWASP Juice Shop

### Authentication: login com as credenciais do administrador sem SQL Injection

O desafio exige acessar a conta do administrador sem recorrer à injeção SQL e sem alterar a senha previamente. A solução identifica o e-mail do administrador, exposto em avaliações de produtos, e aplica um ataque de dicionário com uma lista de senhas comuns contra esse usuário, uma vez que a aplicação não limita as tentativas de login. A correção impõe limite de tentativas, exige senhas fortes e adota autenticação de múltiplos fatores.

### Broken Access Control: acessar a seção de administração da loja

A interface administrativa não aparece no menu, mas a rota existe no código do cliente. A solução inspeciona os arquivos JavaScript da aplicação, localiza o caminho da administração e acessa-o diretamente, dado que não há verificação de autorização no servidor. A correção aplica controle de acesso no servidor, de forma que apenas usuários com o papel adequado alcancem a funcionalidade.

### Broken Access Control: remover todas as avaliações de cinco estrelas

A funcionalidade de administração de avaliações fica acessível por uma chamada de API sem verificação de papel. A solução, de posse do acesso administrativo obtido no desafio anterior, remove as avaliações destacadas. A correção verifica a autorização em cada operação sensível, e não apenas na exibição da interface.

### Broken Access Control: visualizar a cesta de outro usuário

O identificador da cesta trafega em um parâmetro que a aplicação não valida quanto à propriedade. A solução altera esse identificador para o de outro usuário e obtém o conteúdo da cesta alheia, o que caracteriza uma referência direta insegura a objeto. A correção associa a cesta ao usuário autenticado e valida a propriedade do recurso no servidor.

## Laboratórios do PortSwigger: Authentication

### Username enumeration via different responses

A aplicação retorna mensagens distintas para um usuário inexistente e para uma senha incorreta, o que permite enumerar usuários válidos. A solução automatiza, com o Intruder, o envio de uma lista de nomes e compara as respostas para identificar o usuário existente, cuja senha é então descoberta por um segundo ataque. A correção emprega uma mensagem genérica única, com tempo de resposta e código de status uniformes.

### 2FA simple bypass

Após a primeira etapa de autenticação, a aplicação encaminha para a verificação do segundo fator, mas a sessão já permite acessar páginas internas diretamente. A solução autentica-se com as credenciais conhecidas e, em vez de concluir o segundo fator, navega diretamente para a página restrita. A correção condiciona o acesso à conclusão efetiva da segunda etapa, mantendo a sessão não autenticada até lá.

### Password reset broken logic

O fluxo de redefinição de senha confia em um parâmetro que identifica o usuário, enviado junto do token. A solução altera esse parâmetro para a conta-alvo, redefinindo a senha de outro usuário. A correção vincula o token exclusivamente à conta que o solicitou e não aceita a identidade do usuário a partir de um campo controlável.

## Laboratórios do PortSwigger: Broken Access Control

### Unprotected admin functionality

A interface administrativa está acessível em um caminho previsível, sem qualquer verificação. A solução acessa o caminho diretamente e executa a ação restrita. A correção aplica autorização no servidor para toda função administrativa.

### Unprotected admin functionality with unpredictable URL

O caminho da administração não é previsível, mas é revelado no código do cliente. A solução localiza o caminho no JavaScript da página e o acessa. A correção não depende da obscuridade do caminho e impõe controle de acesso efetivo.

### User role controlled by request parameter

O papel do usuário é determinado por um parâmetro enviado na requisição, como um campo no corpo ou em um cookie. A solução altera esse parâmetro para o valor correspondente ao administrador. A correção define o papel no servidor, a partir da sessão autenticada, e nunca a partir de dados do cliente.

### User role can be modified in user profile

A atualização de perfil aceita um campo que define o papel do usuário, ainda que esse campo não apareça na interface. A solução inclui o campo de papel na requisição de atualização, elevando o próprio privilégio. A correção restringe os campos aceitos na atualização e impede a alteração do papel por essa via.

### User ID controlled by request parameter

Os dados de um usuário são obtidos por um identificador enviado na requisição, sem verificação de propriedade. A solução substitui o identificador pelo de outro usuário e acessa os seus dados. A correção valida que o recurso pertence ao usuário autenticado.

### User ID controlled by request parameter, with unpredictable user IDs

O identificador do usuário é um valor não sequencial, mas fica exposto em outra parte da aplicação. A solução obtém o identificador do usuário-alvo em um ponto que o revela e o utiliza para acessar os dados. A correção aplica a verificação de propriedade, independentemente de o identificador ser previsível.

### User ID controlled by request parameter with data leakage in redirect

A aplicação redireciona o usuário, mas inclui dados sensíveis no corpo da resposta de redirecionamento. A solução altera o identificador e lê os dados vazados antes do redirecionamento. A correção não inclui dados sensíveis em respostas de redirecionamento e valida a propriedade do recurso.

### User ID controlled by request parameter with password disclosure

A página de perfil preenche um campo com a senha do usuário, exposto no código da página. A solução altera o identificador do usuário para a conta-alvo e lê a senha presente na resposta. A correção nunca devolve a senha ao cliente, nem mesmo em campos ocultos, e valida a propriedade.

### Insecure direct object references

Uma funcionalidade de atendimento armazena registros em arquivos nomeados por um identificador sequencial, acessíveis diretamente. A solução ajusta o identificador para obter o registro de outro usuário. A correção utiliza referências indiretas por sessão e valida a autorização de acesso a cada registro.

## Síntese

Os desafios desta atividade separam dois temas complementares. As falhas de autenticação, como a enumeração de usuários, a ausência de limite de tentativas e o desvio do segundo fator, comprometem a confirmação da identidade, e corrigem-se com mensagens genéricas, limitação de tentativas, autenticação de múltiplos fatores e controle rigoroso do fluxo de sessão. As falhas de controle de acesso, como as referências diretas inseguras a objeto e a determinação do papel por parâmetro do cliente, comprometem a autorização, e corrigem-se com a negação por padrão, a verificação da propriedade do recurso no servidor e a definição do papel a partir da sessão, nunca a partir de dados fornecidos pelo usuário.
