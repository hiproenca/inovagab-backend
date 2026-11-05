package com.hig.inovagab.bdd.support;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Estado compartilhado entre os passos de UM cenário (um novo objeto por cenário).
 * Guarda a última resposta HTTP, os tokens/ids dos usuários e variáveis salvas
 * (ex.: {{idEstrategia}}) para serem usadas em URLs e corpos de requisição.
 */
@Component
@ScenarioScope
public class ScenarioContext {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{([^}]+)}}");

    private final Map<String, String> tokens = new HashMap<>();
    private final Map<String, String> userIds = new HashMap<>();
    private final Map<String, String> variables = new HashMap<>();
    private Response lastResponse;

    public void registerUser(String alias, String id, String token) {
        userIds.put(alias, id);
        tokens.put(alias, token);
    }

    public String tokenOf(String alias) {
        String token = tokens.get(alias);
        if (token == null) {
            throw new IllegalStateException("Usuário '" + alias + "' não foi autenticado no cenário.");
        }
        return token;
    }

    public String idOf(String alias) {
        String id = userIds.get(alias);
        if (id == null) {
            throw new IllegalStateException("Usuário '" + alias + "' não existe no cenário.");
        }
        return id;
    }

    public void put(String name, String value) {
        variables.put(name, value);
    }

    /** Substitui {{variavel}} pelos valores salvos no cenário. */
    public String resolve(String text) {
        if (text == null) {
            return null;
        }
        Matcher matcher = PLACEHOLDER.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1).trim();
            String value = variables.get(name);
            if (value == null) {
                throw new IllegalStateException("Variável '{{" + name + "}}' não foi definida no cenário.");
            }
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public Response lastResponse() {
        if (lastResponse == null) {
            throw new IllegalStateException("Nenhuma requisição foi feita ainda neste cenário.");
        }
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }
}
