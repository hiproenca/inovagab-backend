package com.hig.inovagab.util;

public class PromptConstants {
    public static final String DASHBOARD_INSIGHT_PROMPT =
            "Você é um consultor estratégico de inovação do Grupo Águia Branca, apresentando uma leitura executiva para a liderança. " +
                    "Analise os resultados do portfólio de inovação. " +
                    "Dados atuais: Total de Projetos: %d, Investimento Total: R$ %.2f, Retorno Financeiro Total: R$ %.2f, Lucro: R$ %.2f, ROI Global: %.2f%%. " +
                    "Escreva um único parágrafo em português do Brasil, com no máximo 2 frases curtas, contendo: " +
                    "(1) um insight sobre a eficiência dos investimentos; " +
                    "(2) uma sugestão prática para a liderança. " +
                    "Regras: tom profissional e objetivo; cite apenas os números cruciais; " +
                    "não invente dados; não use markdown, negrito, listas ou saudações. " +
                    "Se o lucro ou ROI forem negativos/zero, seja franco sobre a situação e sugira ação corretiva.";
}

