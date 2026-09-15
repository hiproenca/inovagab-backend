package com.hig.inovagab.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.hig.inovagab.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
}
