package com.hig.inovagab.controller;


import com.hig.inovagab.model.Idea;
import com.hig.inovagab.service.IdeaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas")
@RequiredArgsConstructor
public class IdeaController {
    public final IdeaService ideaService;

    @PostMapping
    public ResponseEntity<Idea> createIdea(@RequestBody Idea idea){
        return ResponseEntity.ok(ideaService.createIdea(idea));
    }

    @GetMapping("/my-ideas")
    public ResponseEntity<List<Idea>> listAuthorIdeas(){
        return ResponseEntity.ok(ideaService.listAuthorIdeas());
    }

    @GetMapping
    public ResponseEntity<List<Idea>> getAllIdeas(){
        return ResponseEntity.ok(ideaService.getAllIdeas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Idea> getIdeaById(@PathVariable String id){
        return ResponseEntity.ok(ideaService.getIdeaById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Idea> updateIdea(@PathVariable String id, @RequestBody Idea idea){
        return ResponseEntity.ok(ideaService.updateIdea(id, idea));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdea(@PathVariable String id){
        ideaService.deleteIdea(id);
        return ResponseEntity.noContent().build();
    }

}
