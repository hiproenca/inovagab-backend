# language: pt
@projetos
Funcionalidade: Acompanhamento de projetos de inovação
  Como gestor
  Quero cadastrar e acompanhar os projetos originados das ideias aprovadas
  Para dar transparência ao investimento e ao retorno das iniciativas ESG

  Contexto:
    Dado que o usuário "gestor" está autenticado com o perfil "MANAGER"
    E que o usuário "lider" está autenticado com o perfil "LEADER"

  @positivo @contrato
  Cenário: Gestor cadastra um projeto e recebe os valores padrão de status e etapa
    Quando o usuário "gestor" envia uma requisição POST para "/api/projects" com o corpo:
      """
      {
        "title": "Ônibus elétricos na linha Vitória-Vila Velha",
        "investment": 500000.0,
        "deadline": "2027-12-31",
        "description": "Piloto com 5 ônibus elétricos"
      }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "project.json"
    E o campo "status" da resposta deve ser "ACTIVE"
    E o campo "stage" da resposta deve ser "PLANNING"
    E o campo "investment" da resposta deve ser o número 500000.0
    E o campo "managerId" da resposta deve ser o id do usuário "gestor"

  @positivo @contrato
  Cenário: Gestor e líder acompanham a lista de projetos
    Dado que o usuário "gestor" cadastrou em "/api/projects" os registros:
      | title                         | investment | financialReturn |
      | Telemetria para eco-direção   | 80000      | 120000          |
      | Reciclagem de pneus           | 20000      | 35000           |
    Quando o usuário "gestor" envia uma requisição GET para "/api/projects"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "project-list.json"
    E a resposta deve ser uma lista com 2 itens
    Quando o usuário "lider" envia uma requisição GET para "/api/projects"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "project-list.json"
    E a resposta deve ser uma lista com 2 itens

  @positivo @contrato
  Cenário: Gestor atualiza a etapa e os resultados de um projeto
    Dado o usuário "gestor" envia uma requisição POST para "/api/projects" com o corpo:
      """
      { "title": "Iluminação LED nas garagens", "investment": 40000.0 }
      """
    E o id da resposta é salvo como "idProjeto"
    Quando o usuário "gestor" envia uma requisição PUT para "/api/projects/{{idProjeto}}" com o corpo:
      """
      { "stage": "COMPLETED", "financialReturn": 65000.0, "results": "Economia de 45% de energia" }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "project.json"
    E o campo "stage" da resposta deve ser "COMPLETED"
    E o campo "financialReturn" da resposta deve ser o número 65000.0
    E o campo "results" da resposta deve ser "Economia de 45% de energia"
    E o campo "title" da resposta deve ser "Iluminação LED nas garagens"
    Quando o usuário "lider" envia uma requisição GET para "/api/projects/{{idProjeto}}"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "project.json"
    E o campo "stage" da resposta deve ser "COMPLETED"

  @positivo
  Cenário: Gestor exclui um projeto
    Dado o usuário "gestor" envia uma requisição POST para "/api/projects" com o corpo:
      """
      { "title": "Projeto cancelado" }
      """
    E o id da resposta é salvo como "idProjeto"
    Quando o usuário "gestor" envia uma requisição DELETE para "/api/projects/{{idProjeto}}"
    Então o status code da resposta deve ser 204
    E o corpo da resposta deve estar vazio
    Quando o usuário "gestor" envia uma requisição GET para "/api/projects/{{idProjeto}}"
    Então o status code da resposta deve ser 404

  @negativo @contrato
  Cenário: Consulta de um projeto inexistente
    Quando o usuário "lider" envia uma requisição GET para "/api/projects/id-inexistente"
    Então o status code da resposta deve ser 404
    E a resposta deve respeitar o contrato "error.json"
    E o campo "message" da resposta deve ser "O projeto não foi encontrado."

  @negativo @contrato
  Cenário: Envio de um valor inválido para a etapa do projeto
    Quando o usuário "gestor" envia uma requisição POST para "/api/projects" com o corpo:
      """
      { "title": "Etapa inválida", "stage": "ETAPA_QUE_NAO_EXISTE" }
      """
    Então o status code da resposta deve ser 400
    E a resposta deve respeitar o contrato "error.json"

  @negativo
  Esquema do Cenário: Perfis sem permissão não alteram projetos
    Dado que o usuário "operador" está autenticado com o perfil "OPERATOR"
    Quando o usuário "<usuario>" envia uma requisição <metodo> para "/api/projects<caminho>" com o corpo:
      """
      { "title": "Sem permissão" }
      """
    Então o status code da resposta deve ser 403

    Exemplos:
      | usuario  | metodo | caminho   |
      | lider    | POST   |           |
      | lider    | PUT    | /qualquer |
      | lider    | DELETE | /qualquer |
      | operador | POST   |           |
      | operador | GET    |           |
