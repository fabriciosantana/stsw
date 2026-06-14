# language: pt

Funcionalidade: API de Posts - JSONPlaceholder
  Como consumidor da API JSONPlaceholder
  Quero testar os endpoints de posts
  Para garantir que a API responde corretamente

  Cenário: Buscar todos os posts com sucesso
    Dado que a API está disponível em "https://jsonplaceholder.typicode.com"
    Quando eu faço um GET em "/posts"
    Então o status da resposta deve ser 200
    E a resposta deve conter uma lista de posts

  Cenário: Buscar um post específico por ID
    Dado que a API está disponível em "https://jsonplaceholder.typicode.com"
    Quando eu faço um GET em "/posts/1"
    Então o status da resposta deve ser 200
    E o campo "id" da resposta deve ser 1
    E o campo "userId" da resposta deve ser 1

  Cenário: Criar um novo post com sucesso
    Dado que a API está disponível em "https://jsonplaceholder.typicode.com"
    Quando eu faço um POST em "/posts" com o corpo:
      """
      {
        "title": "Seminário SerenityBDD",
        "body": "Demonstração prática de testes automatizados de API",
        "userId": 1
      }
      """
    Então o status da resposta deve ser 201
    E o campo "title" da resposta deve ser "Seminário SerenityBDD"

  Cenário: Buscar post inexistente retorna 404
    Dado que a API está disponível em "https://jsonplaceholder.typicode.com"
    Quando eu faço um GET em "/posts/99999"
    Então o status da resposta deve ser 404
