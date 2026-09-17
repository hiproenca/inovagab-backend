package com.hig.inovagab.controller;

import com.hig.inovagab.model.Project;
import com.hig.inovagab.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    public final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Project> createProject(@RequestBody Project project){
        return ResponseEntity.ok(projectService.createProject(project));
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    public ResponseEntity<List<Project>> getAllProjects(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'LEADER')")
    public ResponseEntity<Project> getProjectById(@PathVariable String id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Project> updateProject(@PathVariable String id, @RequestBody Project project){
        return ResponseEntity.ok(projectService.updateProject(id, project));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteProject(@PathVariable String id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
