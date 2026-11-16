# Especificação da política; os passos não têm implementação Cucumber neste projeto.
Feature: Controle de acesso a perfis e relatórios
  Para proteger dados de usuários e funções administrativas
  Como serviço de perfis
  Quero autorizar cada solicitação conforme o titular e o papel

  Scenario: Usuário comum acessa somente o próprio perfil
    Given que estou autenticado como "usuario-1" com papel comum
    When solicito GET "/secure/profile?userId=1"
    Then a resposta deve ter status 200

  Scenario: Usuário comum não acessa perfil de outra pessoa
    Given que estou autenticado como "usuario-1" com papel comum
    When solicito GET "/secure/profile?userId=2"
    Then a resposta deve ter status 403
    And a resposta não deve conter dados de "usuario-2"

  Scenario: Usuário comum não acessa relatório administrativo
    Given que estou autenticado como "usuario-1" com papel comum
    When solicito GET "/secure/admin/report"
    Then a resposta deve ter status 403

  Scenario: Administrador acessa relatório administrativo
    Given que estou autenticado como "admin" com papel administrativo
    When solicito GET "/secure/admin/report"
    Then a resposta deve ter status 200

