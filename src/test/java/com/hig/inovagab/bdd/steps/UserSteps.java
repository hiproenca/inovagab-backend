package com.hig.inovagab.bdd.steps;

import com.hig.inovagab.bdd.support.ScenarioContext;
import com.hig.inovagab.model.User;
import com.hig.inovagab.model.UserRole;
import com.hig.inovagab.repository.UserRepository;
import io.cucumber.java.pt.Dado;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/** Passos de preparação de usuários (cadastro + perfil + login via API). */
public class UserSteps {

    public static final String DEFAULT_PASSWORD = "Senha@123";

    private final ScenarioContext context;
    private final UserRepository userRepository;

    public UserSteps(ScenarioContext context, UserRepository userRepository) {
        this.context = context;
        this.userRepository = userRepository;
    }

    @Dado("que existe um usuário cadastrado com e-mail {string} e senha {string}")
    public void existeUsuarioCadastrado(String email, String password) {
        register(email, email, password).then().statusCode(200);
    }

    /**
     * Cadastra o usuário pela API (todo cadastro nasce OPERATOR), ajusta o perfil
     * diretamente no banco — como faria um administrador — e faz login pela API.
     */
    @Dado("que o usuário {string} está autenticado com o perfil {string}")
    public void usuarioAutenticadoComPerfil(String alias, String role) {
        String email = alias + "@inovagab.com";
        register(alias, email, DEFAULT_PASSWORD).then().statusCode(200);

        UserRole userRole = UserRole.valueOf(role);
        if (userRole != UserRole.OPERATOR) {
            User user = userRepository.findByEmail(email).orElseThrow();
            user.setRole(userRole);
            userRepository.save(user);
        }

        Response login = given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + email + "\",\"password\":\"" + DEFAULT_PASSWORD + "\"}")
                .post("/api/auth/authenticate");
        login.then().statusCode(200);

        context.registerUser(alias, login.jsonPath().getString("user.id"), login.jsonPath().getString("token"));
    }

    private Response register(String name, String email, String password) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"name\":\"" + name + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .post("/api/auth/register");
    }
}
