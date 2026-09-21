# InovaGAB - Plataforma de Inovação Corporativa (Backend / API)

Repositório destinado ao código-fonte do backend da plataforma **InovaGAB**, desenvolvido para Challenge FIAP em parceria com o **Grupo Águia Branca**. 

Este serviço atua como o motor lógico da aplicação, fornecendo APIs seguras para gestão de usuários, controle de ideias, acompanhamento de projetos e inteligência analítica baseada em IA.

---

##  Tecnologias Utilizadas

O backend cumpre com rigor as exigências arquiteturais do projeto:

*   **Linguagem:** Java 17+
*   **Framework:** Spring Boot 3
*   **Segurança:** Spring Security + Autenticação via JWT (JSON Web Tokens)
*   **Banco de Dados:** MongoDB (NoSQL) via Docker
*   **Persistência:** Spring Data MongoDB
*   **Inteligência Artificial:** Google Gemini API (Geração de Insights Analíticos)
*   **Gerenciador de Dependências:** Maven

---

##  Controle de Acesso (RBAC)

A API protege suas rotas validando o JWT e verificando as _Roles_ do usuário autenticado:

1.  **OPERATOR:** Pode cadastrar ideias e consultar a estratégia vigente.
2.  **MANAGER:** Pode consultar todas as ideias, aprová-las, cadastrar novos projetos e atualizar o progresso financeiro e as etapas.
3.  **LEADER:** Possui acesso total (CRUD) às Estratégias, consulta do andamento de todos os projetos e acesso irrestrito aos endpoints de Dashboard e Insights.

---

##  Integração com Inteligência Artificial

Para elevar a capacidade analítica da alta gestão, a API integra nativamente a **Google Gemini API**. 
Um endpoint específico avalia as métricas globais de investimento, lucro e o total de projetos cadastrados no banco, enviando esse contexto para o modelo (LLM). O modelo, então, retorna insights estruturados e sugestões de melhoria em formato JSON para exibição no aplicativo da liderança.

---

##  Como Executar a API Localmente

### Pré-requisitos
*   [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) instalado.
*   [Docker](https://www.docker.com/) e Docker Compose instalados (para o banco de dados).
*   Chave de API válida do Google Gemini.

### 1. Clonar o Repositório
```bash
git clone [https://github.com/seu-usuario/inovagab-backend.git](https://github.com/seu-usuario/inovagab-backend.git)
cd inovagab-backend
```

### 2. Subir o Banco de Dados (Docker)
O projeto já conta com um arquivo `docker-compose.yml` pré-configurado para facilitar a execução do MongoDB. Na raiz do projeto, execute:

```bash
docker-compose up -d
```
*Isso fará o download da imagem oficial do MongoDB e iniciará o banco de dados na porta padrão `27017` em segundo plano.*

### 3. Configurar Variáveis de Ambiente
Abra o arquivo `src/main/resources/application.properties` (ou `.yml`) e insira suas credenciais e chaves:

```properties
# Conexão com o MongoDB (via Docker)
spring.data.mongodb.uri=mongodb://localhost:27017/inovagab

# Segurança JWT (Altere para uma chave forte e secreta em produção)
jwt.secret=SUA_CHAVE_SECRETA_JWT_AQUI
jwt.expiration=86400000

# Chave da Inteligência Artificial Generativa
gemini.api.key=SUA_CHAVE_GOOGLE_GEMINI_AQUI
```

### 4. Iniciar o Servidor
Execute o Maven Wrapper para baixar dependências e iniciar o Spring Boot:

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```
**Linux / macOS:**
```bash
./mvnw spring-boot:run
```
A API estará escutando requisições na porta `8080`.

---

## 📚 Endpoints Principais

*   **Autenticação:** `POST /api/auth/authenticate`
*   **Estratégias:** `GET`, `POST`, `PUT`, `DELETE /api/strategies`
*   **Ideias:** `GET`, `POST`, `PUT`, `DELETE /api/ideas`
*   **Projetos:** `GET`, `POST`, `PUT`, `DELETE /api/projects`
*   **Dashboard:** `GET /api/dashboard` (Métricas) e `GET /api/dashboard/insights` (Gemini IA)

---

##  Autor
*   **Higor Proença** - Análise e Desenvolvimento de Sistemas (FIAP)
