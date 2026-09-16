package com.hig.inovagab.controller;

import com.hig.inovagab.model.Strategy;

import com.hig.inovagab.service.StrategyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/strategies")
@RequiredArgsConstructor
public class StrategyController {
    private final StrategyService strategyService;

    @PostMapping
    public ResponseEntity<Strategy> createStrategy(@RequestBody Strategy strategy){
        return ResponseEntity.ok(strategyService.createStrategy(strategy));

    }
    @GetMapping()
    public ResponseEntity<List<Strategy>> getAllStrategies(){
        return ResponseEntity.ok(strategyService.getAllStrategies());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Strategy> getStrategyById(@PathVariable String id){
        return ResponseEntity.ok(strategyService.getStrategyById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Strategy> updateStrategy(@PathVariable String id, @RequestBody Strategy strategy){
        return ResponseEntity.ok(strategyService.updateStrategy(id, strategy));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStrategy(@PathVariable String id){
        strategyService.deleteStrategy(id);
        return ResponseEntity.noContent().build();
    }
}
