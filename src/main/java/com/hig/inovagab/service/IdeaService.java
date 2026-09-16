package com.hig.inovagab.service;

import com.hig.inovagab.model.Idea;
import com.hig.inovagab.model.IdeaStatus;
import com.hig.inovagab.model.User;
import com.hig.inovagab.repository.IdeaRepository;
import com.hig.inovagab.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdeaService {
    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    public Idea createIdea(Idea idea){

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        idea.setAuthorId(author.getId());
        idea.setStatus(IdeaStatus.PENDING);
        return ideaRepository.save(idea);
    }

    public List<Idea> listAuthorIdeas(){
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User author = userRepository.findByEmail(currentUserEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));


        return ideaRepository.findIdeaByauthorId(author.getId());
    }


    public List<Idea> getAllIdeas() {
        return ideaRepository.findAll();
    }


    public Idea getIdeaById(String id) {
        return ideaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ideia não encontrada."));
    }

    public Idea updateIdea(String id, Idea updatedIdea) {
        Idea existingIdea = getIdeaById(id);

        if (updatedIdea.getTitle() != null) existingIdea.setTitle(updatedIdea.getTitle());
        if (updatedIdea.getDescription() != null) existingIdea.setDescription(updatedIdea.getDescription());
        if (updatedIdea.getStatus() != null) existingIdea.setStatus(updatedIdea.getStatus());
        if (updatedIdea.getStrategyId() != null) existingIdea.setStrategyId(updatedIdea.getStrategyId());

        return ideaRepository.save(existingIdea);
    }

    public void deleteIdea(String id) {
        ideaRepository.deleteById(id);
    }
}
