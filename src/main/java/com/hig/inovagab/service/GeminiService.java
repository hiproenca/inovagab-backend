package com.hig.inovagab.service;

import com.hig.inovagab.dto.DtoDashboardResponse;
import com.hig.inovagab.util.PromptConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private static final int MAX_ATTEMPTS = 2;
    //private static final String FALLBACK_MESSAGE = "Nenhum modelo Gemini disponível no momento.";

    @Value("${gemini.api.base-url}")
    private String baseUrl;

    @Value("${gemini.api.models}")
    private List<String> models;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeminiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(20_000);
        this.restTemplate = new RestTemplate(factory);
    }

    public Optional<String> generateDashInsights(DtoDashboardResponse metrics) {
        String prompt = String.format(Locale.forLanguageTag("pt-BR"),
                PromptConstants.DASHBOARD_INSIGHT_PROMPT,
                metrics.getTotalProjects(), metrics.getTotalInvestment(),
                metrics.getTotalFinancialReturn(), metrics.getTotalProfit(),
                metrics.getGlobalRoiPercentage());

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        return callWithFallback(new HttpEntity<>(requestBody, headers));
    }

    private Optional<String> callWithFallback(HttpEntity<Map<String, Object>> request) {
        for (String model : models) {
            String url = baseUrl + "/" + model + ":generateContent";

            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                try {
                    JsonNode body = restTemplate.postForEntity(url, request, JsonNode.class).getBody();
                    String text = extractText(body);
                    if (text != null) {
                        return Optional.of(text);
                    }
                    log.warn("Gemini respondeu sem texto utilizável (modelo {}).", model);
                    break;

                } catch (HttpServerErrorException e) {
                    log.warn("Gemini {} (modelo {}, tentativa {}/{})",
                            e.getStatusCode().value(), model, attempt, MAX_ATTEMPTS);
                    if (attempt < MAX_ATTEMPTS) {
                        sleep(1000L * attempt);
                    }

                } catch (HttpClientErrorException e) {
                    log.warn("Gemini rejeitou a chamada com {} (modelo {}). Próximo modelo.",
                            e.getStatusCode().value(), model);
                    break;

                } catch (Exception e) { // timeout, DNS, JSON inválido...
                    log.warn("Falha ao chamar Gemini (modelo {}): {}", model, e.getClass().getSimpleName());
                    break;
                }
            }
        }
        return Optional.empty();
    }

    private String extractText(JsonNode body) {
        if (body == null) {
            return null;
        }
        String text = body.path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text").asString();
        return text.isBlank() ? null : text.trim();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}