package com.hig.inovagab.bdd.config;

import com.hig.inovagab.service.GeminiService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Sobe a aplicação completa (Tomcat em porta aleatória + MongoDB local) para que os
 * cenários façam chamadas HTTP reais na API.
 *
 * O GeminiService é o único componente substituído por um mock, porque depende de uma
 * API externa paga (Google Gemini). Assim os testes são determinísticos e rodam offline.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    @MockitoBean
    private GeminiService geminiService;
}
