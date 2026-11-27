# Prompt 15: Security Misconfiguration (A05:2021)

**Aluno:** Guilherme Abade
**Referência:** [OWASP A05:2021](https://owasp.org/Top10/A05_2021-Security_Misconfiguration/)

As configurações e os roteiros com o Burp Suite e o Juice Shop destinam-se a laboratório próprio ou autorizado.

## 1. Introdução

A *Security Misconfiguration* é a falha de segurança decorrente de configuração inadequada, incompleta ou padrão da aplicação, do servidor, do framework, do contêiner ou da nuvem, e não de um defeito no código em si. Ascendeu ao quinto lugar em 2021, com 90% das aplicações testadas apresentando alguma de suas formas, impulsionada pela crescente complexidade de configuração dos sistemas.

Entre os exemplos mais comuns estão as contas e senhas padrão ainda ativas, os recursos desnecessários habilitados, como portas, serviços, páginas de exemplo e consoles, as mensagens de erro detalhadas expostas ao usuário, os diretórios navegáveis e os arquivos sensíveis acessíveis, a exemplo de `.git`, `.env` e cópias de segurança, a ausência de cabeçalhos de segurança, a configuração permissiva de CORS, os consoles de administração ou depuração abertos e as permissões excessivas em repositórios de objetos na nuvem. As configurações padrão representam risco porque são públicas e conhecidas, documentadas e idênticas em milhares de instalações, de maneira que um atacante as testa em primeiro lugar; tais padrões priorizam a facilidade de funcionamento, e não a segurança.

Os impactos possíveis abrangem o acesso não autorizado, o vazamento de informação interna do sistema, de código ou de credenciais, a tomada de conta administrativa e a execução remota quando há um console aberto, servindo ainda de ponto de partida para outros ataques. Convém distinguir os níveis em que a falha ocorre. Na aplicação, manifestam-se as flags de depuração, as contas padrão e os endpoints de gestão expostos. No servidor e no sistema operacional, surgem a navegação de diretórios, os métodos HTTP perigosos, o TLS fraco e a versão revelada no banner. No framework, aparecem os recursos inseguros por padrão, as aplicações de exemplo e o modo de desenvolvimento em produção. A categoria permanece no Top 10 pela alta prevalência e pela facilidade de exploração automatizada, e sua gravidade varia conforme o que é exposto, da simples informação até a execução remota.

## 2. Exemplos práticos

A configuração a seguir, do Spring Boot Actuator, é insegura.

```properties
# application.properties: inseguro
management.endpoints.web.exposure.include=*
management.endpoint.env.show-values=ALWAYS
server.error.include-stacktrace=always
spring.h2.console.enabled=true
spring.h2.console.settings.web-allow-others=true
```

O problema reside em expor publicamente todos os endpoints do Actuator, incluindo `/actuator/env`, `/actuator/heapdump` e `/actuator/mappings`, o que revela variáveis de ambiente com eventuais segredos, descarregamentos de memória e a configuração interna; o console H2 torna-se acessível remotamente e o rastreamento completo de pilha chega ao cliente. Um atacante consultaria `/actuator/env` para obter credenciais, `/actuator/heapdump` para capturar memória com tokens e o console H2 para executar SQL, enquanto o rastreamento de pilha informaria versões e estrutura. A configuração segura restringe a exposição e desativa recursos.

```properties
# application.properties: seguro
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when-authorized
server.error.include-stacktrace=never
server.error.include-message=never
spring.h2.console.enabled=false
```

Convém ainda proteger as rotas de gestão com autenticação, expondo apenas os endpoints de verificação de saúde e de informação. Quanto aos diretórios sensíveis sem autenticação, um cenário em que `https://app.exemplo.com/.git/` ou um diretório de cópias de segurança está acessível permite ao atacante baixar o repositório, recuperando o código-fonte e segredos do histórico, ou obter cópias do banco. Para evitar a exposição de endpoints administrativos, cumpre desativar a navegação de diretórios, bloquear arquivos como `.git`, `.env`, cópias e exportações, colocar consoles e áreas administrativas atrás de autenticação e de rede restrita, remover aplicações de exemplo e revisar as rotas efetivamente publicadas.

## 3. Atividade prática de ataques e correções

O Burp Suite detecta diversas configurações incorretas, como a ausência de cabeçalhos de segurança, o CORS permissivo, os cookies sem os atributos `Secure` e `HttpOnly`, a navegação de diretórios, as mensagens de erro verbosas, os métodos HTTP perigosos habilitados e as versões expostas em banners. Um roteiro no Juice Shop, em laboratório local, começa por subir a aplicação com `docker run --rm -p 3000:3000 bkimminich/juice-shop` e configurar o navegador para passar pelo Proxy do Burp. A partir do mapa do site, analisam-se as respostas em busca de cabeçalhos ausentes, erros e caminhos incomuns; com o Repeater, provoca-se um erro ao enviar um tipo de dado inesperado e observa-se a resposta.

O desafio correspondente na disciplina, enunciado como provocar um erro tratado de forma pouco elegante e inconsistente, ilustra a categoria: ao receber uma entrada malformada, por exemplo um objeto onde se espera texto, a aplicação responde com um erro detalhado que expõe o rastreamento de pilha e informações internas, o que configura uma *Security Misconfiguration* por tratamento de erro inseguro. A correção adota um tratamento genérico e consistente, por meio de um manipulador global, sem rastreamento de pilha ao cliente, com o registro do erro completo apenas no servidor, identificado por um código de correlação, além da validação das entradas, do retorno padronizado de `400` e da desativação do modo de depuração em produção.

```java
@RestControllerAdvice
class TratadorErros {
    private static final Logger log = LoggerFactory.getLogger(TratadorErros.class);

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, String>> generico(Exception e) {
        String cid = UUID.randomUUID().toString();
        log.error("Erro inesperado cid={}", cid, e);     // detalhe apenas no log
        return ResponseEntity.status(500)
            .body(Map.of("erro", "Falha ao processar a requisição", "id", cid));
    }
}
```

## 4. Aplicação em cenários reais

No cenário de um painel administrativo exposto, sem autenticação e com credenciais padrão, as consequências são severas: o atacante ingressa como administrador, altera configurações, lê e exporta dados, cria contas e instala mecanismos de acesso persistente, o que resulta em comprometimento total. O endurecimento que evitaria esse quadro inclui a troca ou remoção das contas padrão, a restrição do painel por rede, com VPN ou lista de endereços permitidos, a exigência de múltiplos fatores, o menor privilégio, a desativação dos serviços não utilizados e a automação revisada da configuração por infraestrutura como código. O checklist a seguir resume os pontos de configuração de um servidor web.

- [ ] Remover contas e senhas padrão.
- [ ] Desativar a navegação de diretórios e os métodos HTTP não utilizados.
- [ ] Exigir TLS 1.2 ou superior, com cifras fortes e HSTS ativo.
- [ ] Definir cabeçalhos de segurança, como a política de segurança de conteúdo, `X-Content-Type-Options`, `Referrer-Policy` e proteção contra enquadramento.
- [ ] Apresentar erros genéricos, sem rastreamento de pilha, em produção.
- [ ] Ocultar versões em banners.
- [ ] Bloquear arquivos sensíveis, como `.git`, `.env` e cópias de segurança.
- [ ] Manter consoles e áreas administrativas atrás de autenticação e de rede restrita.
- [ ] Restringir o CORS a origens confiáveis.
- [ ] Manter o registro e o monitoramento ativos.
- [ ] Estabelecer um processo de atualização e de revisão periódica.

Um caso público ilustrativo são os vazamentos causados por repositórios de objetos mal configurados, com acesso público, que atingiram diversas empresas e expuseram milhões de registros. A lição é que a configuração padrão ou permissiva na nuvem figura entre as principais causas de vazamento, o que exige padrões seguros, varredura contínua de configuração e revisão de permissões.

## 5. Conclusão

A *Security Misconfiguration* resulta da soma de pequenas decisões de configuração inseguras, como padrões não alterados, recursos desnecessários ativos, erros verbosos e permissões amplas, que, combinadas, abrem caminho ao atacante; é muito prevalente e de exploração automatizável. Sua mitigação apoia-se em um processo de endurecimento repetível e automatizado, com infraestrutura como código e revisão, em padrões seguros, na remoção do que não se utiliza, na separação de ambientes com configurações distintas, na varredura contínua de configuração, na gestão de segredos fora do código e na revisão periódica. Ao revisar um sistema, examinaria em primeiro lugar as contas e senhas padrão e os consoles administrativos expostos, a exposição de erros e rastreamentos, os endpoints de gestão e os arquivos sensíveis acessíveis, os cabeçalhos de segurança e a configuração de TLS e as permissões de recursos na nuvem, por serem os pontos de maior impacto e de exploração mais fácil.
