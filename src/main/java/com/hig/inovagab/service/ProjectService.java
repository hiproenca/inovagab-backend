package com.hig.inovagab.service;

import com.hig.inovagab.model.Project;
import com.hig.inovagab.model.ProjectStage;
import com.hig.inovagab.model.ProjectStatus;
import com.hig.inovagab.model.User;
import com.hig.inovagab.repository.ProjectRepository;
import com.hig.inovagab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public Project createProject(Project project){
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User manager = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        project.setManagerId(manager.getId());

        if(project.getStatus() == null)project.setStatus(ProjectStatus.ACTIVE);
        if(project.getStage()== null)project.setStage(ProjectStage.PLANNING);

        return projectRepository.save(project);
    }

    public List<Project> getAllProjects(){
        return projectRepository.findAll();
    }

    public Project getProjectById(String id){
        return projectRepository.findById(id).orElseThrow(() -> new RuntimeException("O projeto não foi encontrado"));
    }

    public Project updateProject(String id, Project updatedProject){
    Project existingProject = getProjectById(id);

        if (updatedProject.getTitle() != null) existingProject.setTitle(updatedProject.getTitle());
        if (updatedProject.getStage() != null) existingProject.setStage(updatedProject.getStage());
        if (updatedProject.getStatus() != null) existingProject.setStatus(updatedProject.getStatus());
        if (updatedProject.getInvestment() != null) existingProject.setInvestment(updatedProject.getInvestment());
        if (updatedProject.getDeadline() != null) existingProject.setDeadline(updatedProject.getDeadline());
        if (updatedProject.getFinancialReturn() != null) existingProject.setFinancialReturn(updatedProject.getFinancialReturn());
        if (updatedProject.getDescription() != null) existingProject.setDescription(updatedProject.getDescription());
        if (updatedProject.getStrategyId() != null) existingProject.setStrategyId(updatedProject.getStrategyId());
        if (updatedProject.getResults() != null) existingProject.setResults(updatedProject.getResults());


        return projectRepository.save(existingProject);
    }

    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }
}
