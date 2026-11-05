# language: pt
@estrategias
Funcionalidade: Gestão das estratégias ESG da liderança
  Como líder do Grupo Águia Branca
  Quero publicar as estratégias de inovação alinhadas aos pilares ESG
  Para orientar os colaboradores sobre as campanhas prioritárias

  Contexto:
    Dado que o usuário "lider" está autenticado com o perfil "LEADER"

  @positivo @contrato
  Cenário: Líder publica uma estratégia de descarbonização da frota
    Quando o usuário "lider" envia uma requisição POST para "/api/strategies" com o corpo:
      """
      {
        "title": "Descarbonização da frota",
        "category": "Ambiental",
        "campaign": "Frota Verde 2027",
        "date": "2027-01-15",
        "description": "Reduzir 30% das emissões de CO2 com ônibus elétricos e biometano"
      }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "strategy.json"
    E o campo "title" da resposta deve ser "Descarbonização da frota"
    E o campo "category" da resposta deve ser "Ambiental"
    E o campo "leaderId" da resposta deve ser o id do usuário "lider"

  @positivo @contrato
  Cenário: Colaboradores de todos os perfis consultam as estratégias vigentes
    Dado que o usuário "operador" está autenticado com o perfil "OPERATOR"
    E que o usuário "lider" cadastrou em "/api/strategies" os registros:
      | title                     | category  | campaign          |
      | Descarbonização da frota  | Ambiental | Frota Verde 2027  |
      | Inclusão e diversidade    | Social    | Todos a Bordo     |
    Quando o usuário "operador" envia uma requisição GET para "/api/strategies"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "strategy-list.json"
    E a resposta deve ser uma lista com 2 itens

  @positivo @contrato
  Cenário: Ciclo de vida completo de uma estratégia (consultar, atualizar e excluir)
    Dado o usuário "lider" envia uma requisição POST para "/api/strategies" com o corpo:
      """
      { "title": "Gestão de resíduos nas garagens", "category": "Ambiental" }
      """
    E o id da resposta é salvo como "idEstrategia"
    Quando o usuário "lider" envia uma requisição GET para "/api/strategies/{{idEstrategia}}"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "strategy.json"
    E o campo "id" da resposta deve ser "{{idEstrategia}}"
    Quando o usuário "lider" envia uma requisição PUT para "/api/strategies/{{idEstrategia}}" com o corpo:
      """
      { "campaign": "Garagem Lixo Zero", "description": "Reciclagem de óleo, pneus e baterias" }
      """
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "strategy.json"
    E o campo "campaign" da resposta deve ser "Garagem Lixo Zero"
    E o campo "title" da resposta deve ser "Gestão de resíduos nas garagens"
    Quando o usuário "lider" envia uma requisição DELETE para "/api/strategies/{{idEstrategia}}"
    Então o status code da resposta deve ser 204
    E o corpo da resposta deve estar vazio
    Quando o usuário "lider" envia uma requisição GET para "/api/strategies/{{idEstrategia}}"
    Então o status code da resposta deve ser 404

  @negativo @contrato
  Cenário: Consulta de uma estratégia inexistente
    Quando o usuário "lider" envia uma requisição GET para "/api/strategies/id-inexistente"
    Então o status code da resposta deve ser 404
    E a resposta deve respeitar o contrato "error.json"
    E o campo "message" da resposta deve ser "Estratégia não encontrada."

  @negativo @contrato
  Cenário: Atualização de uma estratégia inexistente
    Quando o usuário "lider" envia uma requisição PUT para "/api/strategies/id-inexistente" com o corpo:
      """
      { "title": "Não existe" }
      """
    Então o status code da resposta deve ser 404
    E a resposta deve respeitar o contrato "error.json"

  @negativo
  Esquema do Cenário: Somente a liderança pode alterar estratégias (compliance de acesso)
    Dado que o usuário "<perfil_usuario>" está autenticado com o perfil "<perfil>"
    Quando o usuário "<perfil_usuario>" envia uma requisição <metodo> para "/api/strategies<caminho>" com o corpo:
      """
      { "title": "Tentativa sem permissão" }
      """
    Então o status code da resposta deve ser 403

    Exemplos:
      | perfil_usuario | perfil   | metodo | caminho     |
      | operador       | OPERATOR | POST   |             |
      | gestor         | MANAGER  | POST   |             |
      | operador       | OPERATOR | PUT    | /qualquer   |
      | gestor         | MANAGER  | DELETE | /qualquer   |
