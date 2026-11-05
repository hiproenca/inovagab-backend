# language: pt
@ideias
Funcionalidade: Submissão e avaliação de ideias sustentáveis
  Como colaborador operacional
  Quero submeter ideias de inovação ligadas às estratégias ESG
  Para que a gestão avalie e transforme as melhores em projetos

  Contexto:
    Dado que o usuário "operador" está autenticado com o perfil "OPERATOR"
    E que o usuário "gestor" está autenticado com o perfil "MANAGER"

  @positivo @contrato
  Cenário: Colaborador submete uma ideia de coleta inteligente de resíduos
    Quando o usuário "operador" envia uma requisição POST para "/api/ideas" com o corpo:
      """
      {
        "title": "Coleta inteligente de resíduos nas rodoviárias",
        "description": "Sensores nas lixeiras para otimizar as rotas de coleta e aumentar a reciclagem"
      }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "idea.json"
    E o campo "status" da resposta deve ser "PENDING"
    E o campo "authorId" da resposta deve ser o id do usuário "operador"

  @positivo @contrato
  Cenário: Status e autoria não podem ser forjados na submissão (rastreabilidade)
    Quando o usuário "operador" envia uma requisição POST para "/api/ideas" com o corpo:
      """
      { "title": "Ideia já aprovada?", "status": "APPROVED", "authorId": "outro-usuario" }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "idea.json"
    E o campo "status" da resposta deve ser "PENDING"
    E o campo "authorId" da resposta deve ser o id do usuário "operador"

  @positivo @contrato
  Cenário: Colaborador consulta apenas as próprias ideias
    Dado que o usuário "outro_operador" está autenticado com o perfil "OPERATOR"
    E que o usuário "operador" cadastrou em "/api/ideas" os registros:
      | title                                | description                       |
      | Painéis solares nas garagens         | Reduzir o consumo de energia      |
      | Cartilha de acessibilidade em Libras | Inclusão de passageiros surdos    |
    E que o usuário "outro_operador" cadastrou em "/api/ideas" os registros:
      | title                 | description                     |
      | Reuso de água         | Captação de chuva para lavagem  |
    Quando o usuário "operador" envia uma requisição GET para "/api/ideas/my-ideas"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "idea-list.json"
    E a resposta deve ser uma lista com 2 itens
    E todos os itens da resposta devem ter o campo "authorId" igual ao id do usuário "operador"

  @positivo @contrato
  Cenário: Gestor lista todas as ideias e aprova uma delas
    Dado o usuário "operador" envia uma requisição POST para "/api/ideas" com o corpo:
      """
      { "title": "Biometano a partir de resíduos orgânicos" }
      """
    E o id da resposta é salvo como "idIdeia"
    Quando o usuário "gestor" envia uma requisição GET para "/api/ideas"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "idea-list.json"
    E a resposta deve ser uma lista com 1 item
    Quando o usuário "gestor" envia uma requisição GET para "/api/ideas/{{idIdeia}}"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "idea.json"
    Quando o usuário "gestor" envia uma requisição PUT para "/api/ideas/{{idIdeia}}" com o corpo:
      """
      { "status": "APPROVED" }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "idea.json"
    E o campo "status" da resposta deve ser "APPROVED"
    E o campo "title" da resposta deve ser "Biometano a partir de resíduos orgânicos"
    E o campo "authorId" da resposta deve ser o id do usuário "operador"

  @positivo
  Cenário: Gestor exclui uma ideia
    Dado o usuário "operador" envia uma requisição POST para "/api/ideas" com o corpo:
      """
      { "title": "Ideia duplicada" }
      """
    E o id da resposta é salvo como "idIdeia"
    Quando o usuário "gestor" envia uma requisição DELETE para "/api/ideas/{{idIdeia}}"
    Então o status code da resposta deve ser 204
    E o corpo da resposta deve estar vazio
    Quando o usuário "gestor" envia uma requisição GET para "/api/ideas/{{idIdeia}}"
    Então o status code da resposta deve ser 404

  @negativo @contrato
  Cenário: Consulta de uma ideia inexistente
    Quando o usuário "gestor" envia uma requisição GET para "/api/ideas/id-inexistente"
    Então o status code da resposta deve ser 404
    E a resposta deve respeitar o contrato "error.json"
    E o campo "message" da resposta deve ser "Ideia não encontrada."

  @negativo
  Esquema do Cenário: Perfis sem permissão não acessam a avaliação de ideias
    Quando o usuário "<usuario>" envia uma requisição <metodo> para "<caminho>"
    Então o status code da resposta deve ser 403

    Exemplos:
      | usuario  | metodo | caminho             |
      | operador | GET    | /api/ideas          |
      | operador | GET    | /api/ideas/qualquer |
      | operador | DELETE | /api/ideas/qualquer |
      | gestor   | GET    | /api/ideas/my-ideas |

  @negativo
  Cenário: Gestor não pode submeter ideias em nome dos colaboradores
    Quando o usuário "gestor" envia uma requisição POST para "/api/ideas" com o corpo:
      """
      { "title": "Ideia do gestor" }
      """
    Então o status code da resposta deve ser 403
