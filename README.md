# InovaGAB - Plataforma de Inovação Corporativa (Backend / API)

Repositório destinado ao código-fonte do backend da plataforma **InovaGAB**, desenvolvido para o Challenge FIAP em parceria com o **Grupo Águia Branca**. 

Este serviço atua como o motor lógico da aplicação, fornecendo APIs seguras para gestão de usuários, controle de ideias, acompanhamento de projetos, inteligência analítica baseada em IA e automação completa de infraestrutura (DevOps).

---

## 🚀 Tecnologias Utilizadas

O backend cumpre com rigor as exigências arquiteturais e de engenharia de software do projeto:

* **Linguagem:** Java 17
* **Framework:** Spring Boot (Spring Security + JWT para autenticação)
* **Banco de Dados:** MongoDB (NoSQL) — *MongoDB Atlas* em nuvem (Staging/Prod) e Docker local
* **Inteligência Artificial:** Google Gemini API (Geração de Insights Analíticos para a liderança)
* **Gerenciador de Dependências:** Maven
* **Containerização:** Docker (Multi-stage build com usuário não-root) & Docker Compose
* **Cloud & Infraestrutura (Azure):** Azure App Service para Contêineres, Azure Container Registry (ACR), Grupo de Recursos (`rg-inovagab`)
* **CI/CD:** Azure DevOps Pipelines (4 estágios automatizados)

---

## 🔒 Controle de Acesso (RBAC)

A API protege suas rotas validando o token JWT e verificando as *Roles* do usuário autenticado:

1. **OPERATOR:** Pode cadastrar ideias, consultar suas próprias ideias e consultar a estratégia vigente. (*Nota: Por padrão, novos cadastros iniciam com este perfil para posterior gestão de acessos*).
2. **MANAGER:** Pode consultar todas as ideias, aprová-las, cadastrar novos projetos e atualizar o progresso e etapas.
3. **LEADER:** Possui acesso total (CRUD) às Estratégias, consulta do andamento de todos os projetos e acesso irrestrito aos endpoints de Dashboard e Insights de IA.

---

## 🤖 Integração com Inteligência Artificial (Google Gemini)

Para elevar a capacidade analítica da alta gestão, a API integra nativamente a **Google Gemini API**. 
Um endpoint específico avalia as métricas globais de investimento, lucro e o total de projetos cadastrados no banco, enviando esse contexto para o modelo (LLM). O modelo retorna insights estruturados e sugestões de melhoria em formato JSON para exibição no aplicativo.

---

## ⚙️ Pipeline CI/CD (Azure DevOps)

O pipeline de Integração Contínua e Entrega Contínua está totalmente automatizado no **Azure DevOps** (`azure-pipelines.yml`) e é executado em **4 estágios**:

1. **Build e Testes (CI):** Sobe um MongoDB isolado em contêiner no agente (fixado em `ubuntu-24.04`), executa os testes automatizados (`mvn clean verify`) e gera o artefato `.jar`.
2. **Build e Push da Imagem:** Constrói a imagem otimizada via Dockerfile multi-stage e envia para o Azure Container Registry (`acrinovagabnew.azurecr.io`).
3. **Deploy em Staging:** Publica automaticamente a nova versão no Azure App Service de homologação (`inovagab-stg-new`) e executa testes de fumaça (*smoke tests*).
4. **Deploy em Produção:** Publica no App Service de produção (`inovagab-prod-new`) mediante **aprovação manual** obrigatória no painel do Azure DevOps.

---

## 🐳 Como Executar a API Localmente

### Pré-requisitos
* [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) instalado.
* [Docker](https://www.docker.com/) e Docker Compose instalados.
* Chave de API válida do Google Gemini.

### 1. Clonar o Repositório
```bash
git clone [https://github.com/hiproenca/inovagab-backend.git](https://github.com/hiproenca/inovagab-backend.git)
cd inovagab-backend
