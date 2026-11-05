package com.hig.inovagab.bdd.steps;

import com.hig.inovagab.bdd.support.ScenarioContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import tools.jackson.databind.json.JsonMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Passos genéricos de API: envio de requisições e validação de
 * status code, corpo JSON e contrato (JSON Schema).
 */
public class ApiSteps {

    private static final Set<String> NUMERIC_FIELDS = Set.of("investment", "financialReturn");
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final ScenarioContext context;

    public ApiSteps(ScenarioContext context) {
        this.context = context;
    }

    // ------------------------------------------------------------------ requisições

    @Quando("o usuário {string} envia uma requisição {word} para {string}")
    public void usuarioEnviaRequisicao(String alias, String method, String path) {
        send(authenticated(alias), method, path);
    }

    @Quando("o usuário {string} envia uma requisição {word} para {string} com o corpo:")
    public void usuarioEnviaRequisicaoComCorpo(String alias, String method, String path, String body) {
        send(authenticated(alias).contentType(ContentType.JSON).body(context.resolve(body)), method, path);
    }

    @Quando("um visitante envia uma requisição {word} para {string}")
    public void visitanteEnviaRequisicao(String method, String path) {
        send(given(), method, path);
    }

    @Quando("um visitante envia uma requisição {word} para {string} com o corpo:")
    public void visitanteEnviaRequisicaoComCorpo(String method, String path, String body) {
        send(given().contentType(ContentType.JSON).body(context.resolve(body)), method, path);
    }

    @Quando("uma requisição {word} é enviada para {string} com o token {string}")
    public void requisicaoComToken(String method, String path, String token) {
        send(given().header("Authorization", "Bearer " + token), method, path);
    }

    /** Cadastra vários registros pela API a partir de uma tabela Gherkin. */
    @Dado("que o usuário {string} cadastrou em {string} os registros:")
    public void usuarioCadastrouRegistros(String alias, String path, DataTable table) {
        for (Map<String, String> row : table.asMaps()) {
            Map<String, Object> body = new LinkedHashMap<>();
            row.forEach((key, value) -> body.put(key,
                    NUMERIC_FIELDS.contains(key) ? Double.valueOf(value) : context.resolve(value)));
            authenticated(alias)
                    .contentType(ContentType.JSON)
                    .body(JSON.writeValueAsString(body))
                    .post(path)
                    .then().statusCode(200);
        }
    }

    // ------------------------------------------------------------------ validações

    @Entao("o status code da resposta deve ser {int}")
    public void statusCodeDeveSer(int expected) {
        assertThat(context.lastResponse().statusCode())
                .as("Status code. Corpo recebido: %s", context.lastResponse().asString())
                .isEqualTo(expected);
    }

    @E("a resposta deve respeitar o contrato {string}")
    public void respostaDeveRespeitarContrato(String schema) {
        context.lastResponse().then().assertThat()
                .body(matchesJsonSchemaInClasspath("schemas/" + schema));
    }

    @E("o campo {string} da resposta deve ser {string}")
    public void campoDeveSer(String field, String expected) {
        Object value = context.lastResponse().jsonPath().get(field);
        assertThat(String.valueOf(value)).as("Campo '%s'", field).isEqualTo(context.resolve(expected));
    }

    /** Número no formato com ponto decimal (ex.: 500000.0), independente do idioma do Gherkin. */
    @E("o campo {string} da resposta deve ser o número {word}")
    public void campoDeveSerNumero(String field, String expected) {
        Double value = context.lastResponse().jsonPath().getDouble(field);
        assertThat(value).as("Campo '%s'", field).isEqualTo(Double.parseDouble(expected));
    }

    @E("o campo {string} da resposta deve ser o id do usuário {string}")
    public void campoDeveSerIdDoUsuario(String field, String alias) {
        assertThat(context.lastResponse().jsonPath().getString(field))
                .as("Campo '%s'", field).isEqualTo(context.idOf(alias));
    }

    @E("o campo {string} da resposta deve estar preenchido")
    public void campoDeveEstarPreenchido(String field) {
        assertThat(context.lastResponse().jsonPath().getString(field)).as("Campo '%s'", field).isNotBlank();
    }

    @E("a resposta deve ser uma lista com {int} item/itens")
    public void respostaDeveSerListaCom(int size) {
        List<Object> items = context.lastResponse().jsonPath().getList("$");
        assertThat(items).hasSize(size);
    }

    @E("todos os itens da resposta devem ter o campo {string} igual ao id do usuário {string}")
    public void todosItensPertencemAoUsuario(String field, String alias) {
        List<String> values = context.lastResponse().jsonPath().getList(field);
        assertThat(values).isNotEmpty().allMatch(context.idOf(alias)::equals);
    }

    @E("o corpo da resposta deve estar vazio")
    public void corpoDeveEstarVazio() {
        assertThat(context.lastResponse().asString()).isEmpty();
    }

    @E("o texto da resposta deve conter {string}")
    public void textoDeveConter(String expected) {
        assertThat(context.lastResponse().asString()).contains(expected);
    }

    @E("o id da resposta é salvo como {string}")
    public void salvarId(String variable) {
        String id = context.lastResponse().jsonPath().getString("id");
        assertThat(id).as("id retornado pela API").isNotBlank();
        context.put(variable, id);
    }

    // ------------------------------------------------------------------ auxiliares

    private RequestSpecification authenticated(String alias) {
        return given().header("Authorization", "Bearer " + context.tokenOf(alias));
    }

    private void send(RequestSpecification spec, String method, String path) {
        String url = context.resolve(path);
        Response response = switch (method.toUpperCase()) {
            case "GET" -> spec.get(url);
            case "POST" -> spec.post(url);
            case "PUT" -> spec.put(url);
            case "DELETE" -> spec.delete(url);
            default -> throw new IllegalArgumentException("Método HTTP não suportado: " + method);
        };
        context.setLastResponse(response);
    }
}
