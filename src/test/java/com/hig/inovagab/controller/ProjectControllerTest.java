package com.hig.inovagab.controller;


import com.hig.inovagab.model.Project;
import com.hig.inovagab.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ProjectController projectController;

    @Test
    void testListProjects() {
        Project project1 = new Project();
        Project project2 = new Project();
        List<Project> fakeList = Arrays.asList(project1, project2);

        when(projectService.getAllProjects()).thenReturn(fakeList);

        ResponseEntity<List<Project>> projectsControllerTestOutput = projectController.getAllProjects();

        assertEquals(HttpStatus.OK, projectsControllerTestOutput.getStatusCode());

        assertEquals(2, projectsControllerTestOutput.getBody().size(), "A API deveria ter retornado 200 OK ao listar os projetos");
        assertEquals(fakeList, projectsControllerTestOutput.getBody());
    }

    @Test
    void testCreateProjectsReturnStatus200() {

        Project mockSaveProject = new Project();
        Project mockProjectSaved= new Project();

        when(projectService.createProject(any(Project.class))).thenReturn(mockProjectSaved);


        ResponseEntity<Project> projectControllerTestOutput = projectController.createProject(mockSaveProject);


        assertEquals(HttpStatus.OK, projectControllerTestOutput.getStatusCode());
        assertEquals(mockProjectSaved, projectControllerTestOutput.getBody(), "Deve retornar o projeto que foi salvo");
    }

    @Test
    void testGetProjectByIdReturnStatus200() {
        // 1. ARRANGE
        String mockSearchId = "12345";
        Project mockFoundProject = new Project();

        when(projectService.getProjectById(mockSearchId)).thenReturn(mockFoundProject);

        // 2. ACT
        ResponseEntity<Project> projectControllerTestOutput = projectController.getProjectById(mockSearchId);

        // 3. ASSERT
        assertEquals(HttpStatus.OK, projectControllerTestOutput.getStatusCode());
        assertEquals(mockFoundProject, projectControllerTestOutput.getBody(), "O corpo da resposta deve ser o projeto encontrado");
    }

    @Test
    void testDeleteProjectReturnStatus204NoContent() {

        String mockIdforDeletion = "999";



        ResponseEntity<Void> projectControllerTestOutput = projectController.deleteProject(mockIdforDeletion);

        assertEquals(HttpStatus.NO_CONTENT, projectControllerTestOutput.getStatusCode(), "Deleção deve retornar 204 No Content");


        verify(projectService, times(1)).deleteProject(mockIdforDeletion);
    }


}
