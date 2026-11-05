# language: pt
@dashboard
Funcionalidade: Dashboard de resultados e insights de IA para a liderança
  Como líder
  Quero acompanhar o investimento, o retorno e o ROI dos projetos de inovação
  Para tomar decisões transparentes sobre a carteira ESG da empresa

  Contexto:
    Dado que o usuário "lider" está autenticado com o perfil "LEADER"
    E que o usuário "gestor" está autenticado com o perfil "MANAGER"

  @positivo @contrato
  Cenário: Métricas consolidadas dos projetos
    Dado que o usuário "gestor" cadastrou em "/api/projects" os registros:
      | title                          | investment | financialReturn |
      | Ônibus elétricos               | 100000     | 150000          |
      | Painéis solares nas garagens   | 50000      | 80000           |
      | Reaproveitamento de água       | 50000      | 20000           |
    Quando o usuário "lider" envia uma requisição GET para "/api/dashboard"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "dashboard.json"
    E o campo "totalProjects" da resposta deve ser o número 3
    E o campo "totalInvestment" da resposta deve ser o número 200000.0
    E o campo "totalFinancialReturn" da resposta deve ser o número 250000.0
    E o campo "totalProfit" da resposta deve ser o número 50000.0
    E o campo "globalRoiPercentage" da resposta deve ser o número 25.0

  @positivo @contrato
  Cenário: Dashboard sem projetos cadastrados retorna métricas zeradas
    Quando o usuário "lider" envia uma requisição GET para "/api/dashboard"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "dashboard.json"
    E o campo "totalProjects" da resposta deve ser o número 0
    E o campo "globalRoiPercentage" da resposta deve ser o número 0.0

  @positivo @contrato
  Cenário: Projetos sem investimento informado não quebram o cálculo do ROI
    Dado que o usuário "gestor" cadastrou em "/api/projects" os registros:
      | title                     |
      | Programa de voluntariado  |
    Quando o usuário "lider" envia uma requisição GET para "/api/dashboard"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "dashboard.json"
    E o campo "totalProjects" da resposta deve ser o número 1
    E o campo "globalRoiPercentage" da resposta deve ser o número 0.0

  @positivo @contrato
  Cenário: Líder recebe insights gerados pela IA
    Dado que o serviço de IA está disponível e responde "Priorize a eletrificação da frota: maior ROI da carteira."
    Quando o usuário "lider" envia uma requisição GET para "/api/dashboard/insights"
    Então o status code da resposta deve ser 200
    E a resposta deve respeitar o contrato "ai-insight.json"
    E o campo "aiInsight" da resposta deve ser "Priorize a eletrificação da frota: maior ROI da carteira."

  @negativo @contrato
  Cenário: Serviço de IA indisponível
    Dado que o serviço de IA está indisponível
    Quando o usuário "lider" envia uma requisição GET para "/api/dashboard/insights"
    Então o status code da resposta deve ser 503
    E a resposta deve respeitar o contrato "ai-insight-error.json"
    E o campo "error" da resposta deve ser "Não foi possível gerar os insights no momento. Tente novamente mais tarde."

  @negativo
  Esquema do Cenário: Somente a liderança acessa o dashboard
    Dado que o usuário "operador" está autenticado com o perfil "OPERATOR"
    Quando o usuário "<usuario>" envia uma requisição GET para "<caminho>"
    Então o status code da resposta deve ser 403

    Exemplos:
      | usuario  | caminho                 |
      | gestor   | /api/dashboard          |
      | operador | /api/dashboard          |
      | gestor   | /api/dashboard/insights |
      | operador | /api/dashboard/insights |
