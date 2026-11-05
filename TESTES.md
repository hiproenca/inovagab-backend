# InovaGAB — Testes Automatizados (BDD + API)

Entrega do **Desafio de Testes Automatizados**. A suíte valida as funcionalidades críticas da
API InovaGAB usando **BDD com Gherkin (Cucumber)** e **testes de API com RestAssured**,
cobrindo **status code**, **corpo da resposta (JSON)** e **contrato (JSON Schema)** de **todas as APIs**.

| Item | Resultado |
|---|---|
| Cenários BDD (Gherkin) | **51 execuções** (19 positivos / 32 negativos) em 5 arquivos `.feature` |
| Endpoints cobertos | **21 de 21** (100%) |
| Contratos JSON Schema | 11 schemas |
| Execução | Local, com um comando (`./run-tests.sh` ou `run-tests.cmd`) |

---

## 1. Como executar (localmente)

### Pré-requisitos
- **JDK 17+**
- **Docker** (só para subir o MongoDB de teste) — ou um MongoDB já rodando em `localhost:27017`

Não é necessário instalar Maven (o projeto usa o Maven Wrapper) nem ter chave do Gemini
(a IA é simulada nos testes).

### Um comando
Linux / macOS / Git Bash:
```bash
./run-tests.sh
```
Windows (CMD / PowerShell):
```bat
run-tests.cmd
```
O script sobe o MongoDB de teste (`docker-compose.test.yml`) e executa `mvnw test`.

### Passo a passo (manual)
```bash
docker compose -f docker-compose.test.yml up -d --wait
./mvnw test
```

### Filtrar por tag
```bash
./run-tests.sh @negativo                    # só cenários de falha
./run-tests.sh @contrato                    # só cenários que validam JSON Schema
./run-tests.sh "@projetos and @positivo"    # combinações
```
Tags disponíveis: `@autenticacao @estrategias @ideias @projetos @dashboard @positivo @negativo @contrato`.

### Relatórios gerados
| Arquivo | Conteúdo |
|---|---|
| `target/cucumber-reports/cucumber.html` | Relatório visual do Cucumber (abrir no navegador) |
| `target/cucumber-reports/cucumber.json` | Resultado em JSON (integração com ferramentas) |
| `target/cucumber-reports/cucumber.xml` | Resultado JUnit XML |
| `target/surefire-reports/` | Relatórios do Maven Surefire |

Saída esperada no terminal:
```
[INFO] Tests run: 51, Failures: 0, Errors: 0, Skipped: 0 -- in com.hig.inovagab.bdd.CucumberTest
[INFO] Tests run: 56, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 2. Como funciona

```
Feature (.feature, Gherkin pt-BR)
   └─> Step definitions (Java, Cucumber)
          └─> RestAssured faz chamadas HTTP reais
                 └─> API Spring Boot completa (porta aleatória) ─> MongoDB local (banco inovagab_test)
```

- `@SpringBootTest(webEnvironment = RANDOM_PORT)` sobe **a aplicação real** (segurança JWT,
  controllers, services, repositórios). Nada é simulado, exceto o **Google Gemini** (`@MockitoBean`),
  que é uma API externa paga — assim os testes são determinísticos e funcionam offline.
- Antes de **cada cenário** o banco de teste é limpo (`Hooks.java`), garantindo isolamento.
- Usuários de cada perfil (OPERATOR, MANAGER, LEADER) são cadastrados e autenticados **pela própria API**;
  como todo cadastro nasce OPERATOR, o perfil é ajustado no banco (papel de um administrador).

### Estrutura
```
src/test/
├── java/com/hig/inovagab/bdd/
│   ├── CucumberTest.java                     # runner (JUnit Platform Suite)
│   ├── config/CucumberSpringConfiguration.java  # sobe a API + mock do Gemini
│   ├── support/ScenarioContext.java          # estado do cenário (tokens, ids, última resposta)
│   └── steps/
│       ├── Hooks.java                        # porta da API + limpeza do banco
│       ├── UserSteps.java                    # cadastro/login por perfil
│       ├── ApiSteps.java                     # requisições + validações (status, JSON, schema)
│       └── AiInsightSteps.java               # comportamento do serviço de IA
└── resources/
    ├── features/                             # cenários em Gherkin
    │   ├── 01_autenticacao.feature
    │   ├── 02_estrategias.feature
    │   ├── 03_ideias.feature
    │   ├── 04_projetos.feature
    │   └── 05_dashboard.feature
    ├── schemas/                              # contratos JSON Schema (draft-04)
    ├── application.properties                # configuração de teste
    └── junit-platform.properties             # configuração do Cucumber (glue, relatórios)
```

### Exemplo de cenário
```gherkin
# language: pt
Funcionalidade: Submissão e avaliação de ideias sustentáveis

  @positivo @contrato
  Cenário: Colaborador submete uma ideia de coleta inteligente de resíduos
    Quando o usuário "operador" envia uma requisição POST para "/api/ideas" com o corpo:
      """
      { "title": "Coleta inteligente de resíduos nas rodoviárias",
        "description": "Sensores nas lixeiras para otimizar as rotas de coleta" }
      """
    Então o status code da resposta deve ser 200                  # status code
    E a resposta deve respeitar o contrato "idea.json"            # contrato (JSON Schema)
    E o campo "status" da resposta deve ser "PENDING"             # corpo JSON
    E o campo "authorId" da resposta deve ser o id do usuário "operador"
```

---

## 3. Cobertura das APIs

Legenda: ✅ status code · 📄 corpo JSON · 📐 contrato JSON Schema · ❌ cenário negativo

| Endpoint | Perfil | Validações | Cenários negativos |
|---|---|---|---|
| `POST /api/auth/register` | público | ✅ 📄 📐 | e-mail duplicado (409), dados obrigatórios (400) |
| `POST /api/auth/authenticate` | público | ✅ 📄 📐 | senha errada / usuário inexistente (401) |
| `GET /api/demo` | autenticado | ✅ 📄 | sem token (401) |
| `POST /api/strategies` | LEADER | ✅ 📄 📐 | OPERATOR/MANAGER (403) |
| `GET /api/strategies` | todos | ✅ 📄 📐 | sem token / token inválido (401) |
| `GET /api/strategies/{id}` | todos | ✅ 📄 📐 | inexistente (404) |
| `PUT /api/strategies/{id}` | LEADER | ✅ 📄 📐 | inexistente (404), OPERATOR (403) |
| `DELETE /api/strategies/{id}` | LEADER | ✅ 📄 | MANAGER (403) |
| `POST /api/ideas` | OPERATOR | ✅ 📄 📐 | MANAGER (403), status/autor forjados ignorados |
| `GET /api/ideas/my-ideas` | OPERATOR | ✅ 📄 📐 | MANAGER (403) |
| `GET /api/ideas` | MANAGER | ✅ 📄 📐 | OPERATOR (403) |
| `GET /api/ideas/{id}` | MANAGER | ✅ 📄 📐 | inexistente (404), OPERATOR (403) |
| `PUT /api/ideas/{id}` | MANAGER | ✅ 📄 📐 | — |
| `DELETE /api/ideas/{id}` | MANAGER | ✅ 📄 | OPERATOR (403) |
| `POST /api/projects` | MANAGER | ✅ 📄 📐 | etapa inválida (400), LEADER/OPERATOR (403) |
| `GET /api/projects` | MANAGER, LEADER | ✅ 📄 📐 | OPERATOR (403) |
| `GET /api/projects/{id}` | MANAGER, LEADER | ✅ 📄 📐 | inexistente (404) |
| `PUT /api/projects/{id}` | MANAGER | ✅ 📄 📐 | LEADER (403) |
| `DELETE /api/projects/{id}` | MANAGER | ✅ 📄 | LEADER (403) |
| `GET /api/dashboard` | LEADER | ✅ 📄 📐 | MANAGER/OPERATOR (403) |
| `GET /api/dashboard/insights` | LEADER | ✅ 📄 📐 | IA indisponível (503), MANAGER/OPERATOR (403) |

> Os erros da API seguem o contrato `schemas/error.json`:
> `{ "timestamp", "status", "error", "message", "path" }`.

---

## 4. Relação com ESG

| Pilar | Como aparece nos testes |
|---|---|
| **Ambiental** | Cenários com estratégias e projetos de descarbonização da frota, ônibus elétricos, energia solar, coleta inteligente e reciclagem de resíduos, reuso de água. O dashboard valida o cálculo de investimento/retorno/ROI dessas iniciativas. |
| **Social** | Ideias de inclusão (cartilha de acessibilidade em Libras), campanha "Todos a Bordo" de diversidade, programa de voluntariado; qualquer colaborador pode propor ideias. |
| **Governança** | Controle de acesso por perfil (RBAC) testado em todas as rotas, rastreabilidade de autoria (`authorId`, `managerId`, `leaderId` vêm sempre do usuário autenticado e não podem ser forjados), status inicial obrigatório `PENDING`, respostas de erro padronizadas e sem vazamento de dados, e-mail único por colaborador. |

---

## 5. Ajustes feitos na API durante a criação dos testes

Os cenários negativos revelaram comportamentos incorretos, que foram corrigidos:

| Problema encontrado | Antes | Depois |
|---|---|---|
| Qualquer erro (senha errada, sem token, recurso inexistente) | `403` sem corpo | `401`, `404`, `409`, `400` com corpo JSON padronizado (`GlobalExceptionHandler`) |
| Cadastro com e-mail já existente era aceito (e depois o login desse e-mail quebrava) | `200` | `409 Conflict` |
| Campo `name` do cadastro era ignorado | `name` = e-mail | `name` salvo corretamente |
| Cadastro sem e-mail/senha | aceito | `400 Bad Request` |
| Token JWT malformado | erro interno | `401 Unauthorized` |
| ROI com investimento total zero | divisão por zero (`NaN`) | `0.0` |

Arquivos novos: `exception/ApiError`, `exception/GlobalExceptionHandler`,
`exception/ResourceNotFoundException`, `exception/EmailAlreadyRegisteredException`.

---

## 6. Ferramentas

| Ferramenta | Uso |
|---|---|
| Cucumber 8 (`cucumber-java`, `cucumber-spring`, `cucumber-junit-platform-engine`) | BDD / Gherkin |
| JUnit Platform Suite | Runner dos cenários dentro do `mvn test` |
| RestAssured 6 | Requisições HTTP e validação de status/corpo |
| RestAssured JSON Schema Validator | Testes de contrato |
| AssertJ | Asserções |
| Spring Boot Test + Mockito | Subir a API real e simular o Google Gemini |
| Docker (MongoDB 7) | Banco de dados de teste |
