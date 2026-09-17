package com.hig.inovagab.service;

import com.hig.inovagab.dto.DtoDashboardResponse;
import com.hig.inovagab.model.Project;
import com.hig.inovagab.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ProjectRepository projectRepository;

    public DtoDashboardResponse getDashboardMetrics(){
        List<Project> projects = projectRepository.findAll();
        int totalProjects = projects.size();
        double totalInvestment = projects.stream().mapToDouble(p -> p.getInvestment() != null ? p.getInvestment() : 0.0).sum();
        double totalFinancialReturn = projects.stream().mapToDouble(p -> p.getFinancialReturn() != null ? p.getFinancialReturn() : 0.0).sum();
        double totalProfit = totalFinancialReturn - totalInvestment;

        double globalRoiPercentage = totalProjects > 0 ? (totalProfit / totalInvestment) * 100 : 0.0;




        return DtoDashboardResponse.builder()
                .totalProjects(totalProjects)
                .totalInvestment(totalInvestment)
                .totalFinancialReturn(totalFinancialReturn)
                .totalProfit(totalProfit)
                .globalRoiPercentage(Math.round(globalRoiPercentage * 100.0) / 100.0).build();
    }


}
