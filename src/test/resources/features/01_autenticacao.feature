# language: pt
@autenticacao
Funcionalidade: Cadastro e autenticação de colaboradores
  Como colaborador do Grupo Águia Branca
  Quero me cadastrar e acessar a plataforma InovaGAB com segurança
  Para propor e acompanhar iniciativas de inovação e sustentabilidade

  # Governança (G do ESG): acesso controlado, rastreável e sem vazamento de dados

  @positivo @contrato
  Cenário: Cadastro de um novo colaborador com sucesso
    Quando um visitante envia uma requisição POST para "/api/auth/register" com o corpo:
      """
      { "name": "Ana Souza", "email": "ana.souza@aguiabranca.com.br", "password": "Senha@123" }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "auth-response.json"
    E o campo "user.name" da resposta deve ser "Ana Souza"
    E o campo "user.email" da resposta deve ser "ana.souza@aguiabranca.com.br"
    E o campo "user.role" da resposta deve ser "OPERATOR"
    E o campo "token" da resposta deve estar preenchido

  @negativo @contrato
  Cenário: Não permitir dois cadastros com o mesmo e-mail
    Dado que existe um usuário cadastrado com e-mail "ana.souza@aguiabranca.com.br" e senha "Senha@123"
    Quando um visitante envia uma requisição POST para "/api/auth/register" com o corpo:
      """
      { "name": "Outra Ana", "email": "ana.souza@aguiabranca.com.br", "password": "OutraSenha" }
      """
    Então o status code da resposta deve ser 409
    E a resposta deve respeitar o contrato "error.json"
    E o campo "message" da resposta deve ser "O e-mail ana.souza@aguiabranca.com.br já está cadastrado."

  @negativo @contrato
  Esquema do Cenário: Cadastro rejeitado quando faltam dados obrigatórios
    Quando um visitante envia uma requisição POST para "/api/auth/register" com o corpo:
      """
      { "name": "Sem dados", "email": "<email>", "password": "<senha>" }
      """
    Então o status code da resposta deve ser 400
    E a resposta deve respeitar o contrato "error.json"
    E o campo "message" da resposta deve ser "E-mail e senha são obrigatórios."

    Exemplos:
      | email                | senha     |
      |                      | Senha@123 |
      | sem.senha@empresa.com |           |

  @positivo @contrato
  Cenário: Login com credenciais válidas
    Dado que existe um usuário cadastrado com e-mail "carlos@aguiabranca.com.br" e senha "Senha@123"
    Quando um visitante envia uma requisição POST para "/api/auth/authenticate" com o corpo:
      """
      { "email": "carlos@aguiabranca.com.br", "password": "Senha@123" }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "auth-response.json"
    E o campo "user.email" da resposta deve ser "carlos@aguiabranca.com.br"

  @negativo @contrato
  Esquema do Cenário: Login recusado com credenciais inválidas
    Dado que existe um usuário cadastrado com e-mail "carlos@aguiabranca.com.br" e senha "Senha@123"
    Quando um visitante envia uma requisição POST para "/api/auth/authenticate" com o corpo:
      """
      { "email": "<email>", "password": "<senha>" }
      """
    Então o status code da resposta deve ser 401
    E a resposta deve respeitar o contrato "error.json"
    E o campo "message" da resposta deve ser "E-mail ou senha inválidos."

    Exemplos:
      | email                       | senha       |
      | carlos@aguiabranca.com.br   | senhaErrada |
      | naoexiste@aguiabranca.com.br | Senha@123   |

  @negativo
  Cenário: Acesso a uma rota protegida sem token
    Quando um visitante envia uma requisição GET para "/api/strategies"
    Então o status code da resposta deve ser 401

  @negativo
  Cenário: Acesso a uma rota protegida com token inválido
    Quando uma requisição GET é enviada para "/api/strategies" com o token "token.invalido.123"
    Então o status code da resposta deve ser 401

  @positivo
  Cenário: Token válido libera o endpoint de verificação de acesso
    Dado que o usuário "operador" está autenticado com o perfil "OPERATOR"
    Quando o usuário "operador" envia uma requisição GET para "/api/demo"
    Então o status code da resposta deve ser 200
    E o texto da resposta deve conter "Acesso liberado"

  @negativo
  Cenário: Endpoint de verificação de acesso exige token
    Quando um visitante envia uma requisição GET para "/api/demo"
    Então o status code da resposta deve ser 401
