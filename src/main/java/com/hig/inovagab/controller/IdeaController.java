package com.hig.inovagab.controller;


import com.hig.inovagab.model.Idea;
import com.hig.inovagab.service.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas")
@RequiredArgsConstructor
public class IdeaController {
    public final IdeaService ideaService;

    @PostMapping
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<Idea> createIdea(@RequestBody Idea idea){
        return ResponseEntity.ok(ideaService.createIdea(idea));
    }

    @GetMapping("/my-ideas")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<List<Idea>> listAuthorIdeas(){
        return ResponseEntity.ok(ideaService.listAuthorIdeas());
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Idea>> getAllIdeas(){
        return ResponseEntity.ok(ideaService.getAllIdeas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Idea> getIdeaById(@PathVariable String id){
        return ResponseEntity.ok(ideaService.getIdeaById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Idea> updateIdea(@PathVariable String id, @RequestBody Idea idea){
        return ResponseEntity.ok(ideaService.updateIdea(id, idea));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteIdea(@PathVariable String id){
        ideaService.deleteIdea(id);
        return ResponseEntity.noContent().build();
    }

}
