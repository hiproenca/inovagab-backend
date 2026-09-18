package com.hig.inovagab.controller;

import com.hig.inovagab.dto.DtoDashboardResponse;
import com.hig.inovagab.service.DashboardService;
import com.hig.inovagab.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    private final GeminiService geminiService;


    @GetMapping
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<DtoDashboardResponse> getDashboard(){
        return ResponseEntity.ok(dashboardService.getDashboardMetrics());
    }

    @GetMapping("/insights")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<Map<String, String >> getDashboardInsights(){
        DtoDashboardResponse metrics = dashboardService.getDashboardMetrics();


        return geminiService.generateDashInsights(metrics)
                .map(insight -> ResponseEntity.ok(Map.of("aiInsight", insight)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Map.of("error",
                                "Não foi possível gerar os insights no momento. Tente novamente mais tarde.")));
    }
}
