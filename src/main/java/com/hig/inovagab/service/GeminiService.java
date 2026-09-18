package com.hig.inovagab.service;

import com.hig.inovagab.dto.DtoDashboardResponse;
import com.hig.inovagab.util.PromptConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    @Value( "${gemini.api.url}")
    private String apiUrl;
    @Value( "${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateDashInsights (DtoDashboardResponse metrics){
       String prompt = String.format(PromptConstants.DASHBOARD_INSIGHT_PROMPT,
               metrics.getTotalProjects(), metrics.getTotalInvestment(),
               metrics.getTotalFinancialReturn(), metrics.getTotalProfit(),
               metrics.getGlobalRoiPercentage());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(
                                Map.of("text", prompt)
                        ))
                )
        );

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, httpHeaders);
        try {
            String fullUrl = apiUrl + "?key=" + apiKey;


            ResponseEntity<JsonNode> response = restTemplate.postForEntity(fullUrl, request, JsonNode.class);

            JsonNode body = response.getBody();


            if (body != null && body.has("candidates")) {

                return body.path("candidates")
                        .get(0)
                        .path("content")
                        .path("parts")
                        .get(0)
                        .path("text")
                        .asText();
            }

            return "Nenhum insight retornado pela IA.";

        } catch (Exception e) {
            System.out.println("Erro aqui. Verifique a conexão ou a chave da API." + e.getMessage());
            return "Não foi possível gerar os insights no momento. Tente novamente mais tarde. ";
        }

    }




}
