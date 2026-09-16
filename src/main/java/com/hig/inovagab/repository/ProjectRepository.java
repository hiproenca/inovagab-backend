package com.hig.inovagab.repository;

import com.hig.inovagab.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepository extends MongoRepository <Project, String> {
}
