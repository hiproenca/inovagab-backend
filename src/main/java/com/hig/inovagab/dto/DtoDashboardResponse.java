package com.hig.inovagab.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DtoDashboardResponse {
    private int totalProjects;
    private double totalInvestment;
    private double totalFinancialReturn;
    private double totalProfit;
    private double globalRoiPercentage;
}
