package com.hig.inovagab.service;

import com.hig.inovagab.model.Strategy;
import com.hig.inovagab.model.User;
import com.hig.inovagab.repository.StrategyRepository;
import com.hig.inovagab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StrategyService {
    private final UserRepository userRepository;
    private final StrategyRepository strategyRepository;

    public Strategy createStrategy(Strategy strategy){
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User leader = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        strategy.setLeaderId(leader.getId());

        return strategyRepository.save(strategy);
    }

    public List<Strategy> getAllStrategies(){
        return strategyRepository.findAll();

    }

    public Strategy getStrategyById(String id){


        return strategyRepository.findById(id).orElseThrow(() -> new RuntimeException("Estratégia não encontrada."));

    }

    public Strategy updateStrategy(String id, Strategy updatedStrategy) {
        Strategy existingStrategy = getStrategyById(id);

        if (updatedStrategy.getTitle() != null) existingStrategy.setTitle(updatedStrategy.getTitle());
        if (updatedStrategy.getCategory() != null) existingStrategy.setCategory(updatedStrategy.getCategory());
        if (updatedStrategy.getCampaign() != null) existingStrategy.setCampaign(updatedStrategy.getCampaign());
        if (updatedStrategy.getDate() != null) existingStrategy.setDate(updatedStrategy.getDate());
        if (updatedStrategy.getDescription() != null) existingStrategy.setDescription(updatedStrategy.getDescription());

        return strategyRepository.save(existingStrategy);
    }

    public void deleteStrategy(String id) {
        strategyRepository.deleteById(id);
    }
}
