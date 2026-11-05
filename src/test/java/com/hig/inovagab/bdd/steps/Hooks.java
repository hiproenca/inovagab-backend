package com.hig.inovagab.bdd.steps;

import com.hig.inovagab.repository.IdeaRepository;
import com.hig.inovagab.repository.ProjectRepository;
import com.hig.inovagab.repository.StrategyRepository;
import com.hig.inovagab.repository.UserRepository;
import io.cucumber.java.Before;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.springframework.beans.factory.annotation.Value;

/** Prepara o ambiente antes de cada cenário: porta da API e banco limpo. */
public class Hooks {

    @Value("${local.server.port}")
    private int port;

    private final UserRepository userRepository;
    private final IdeaRepository ideaRepository;
    private final ProjectRepository projectRepository;
    private final StrategyRepository strategyRepository;

    public Hooks(UserRepository userRepository, IdeaRepository ideaRepository,
                 ProjectRepository projectRepository, StrategyRepository strategyRepository) {
        this.userRepository = userRepository;
        this.ideaRepository = ideaRepository;
        this.projectRepository = projectRepository;
        this.strategyRepository = strategyRepository;
    }

    @Before(order = 0)
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.config = RestAssured.config()
                .jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Before(order = 1)
    public void cleanDatabase() {
        ideaRepository.deleteAll();
        projectRepository.deleteAll();
        strategyRepository.deleteAll();
        userRepository.deleteAll();
    }
}
