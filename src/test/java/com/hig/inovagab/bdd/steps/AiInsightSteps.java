package com.hig.inovagab.bdd.steps;

import com.hig.inovagab.service.GeminiService;
import io.cucumber.java.pt.Dado;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/** Controla o comportamento do serviço de IA (Google Gemini), que é mockado nos testes. */
public class AiInsightSteps {

    private final GeminiService geminiService;

    public AiInsightSteps(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @Dado("que o serviço de IA está disponível e responde {string}")
    public void servicoIaDisponivel(String insight) {
        when(geminiService.generateDashInsights(any())).thenReturn(Optional.of(insight));
    }

    @Dado("que o serviço de IA está indisponível")
    public void servicoIaIndisponivel() {
        when(geminiService.generateDashInsights(any())).thenReturn(Optional.empty());
    }
}
