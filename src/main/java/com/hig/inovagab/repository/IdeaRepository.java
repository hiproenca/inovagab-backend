package com.hig.inovagab.repository;

import com.hig.inovagab.model.Idea;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeaRepository extends MongoRepository <Idea, String> {
    List <Idea> findIdeaByauthorId(String authorId);
}
