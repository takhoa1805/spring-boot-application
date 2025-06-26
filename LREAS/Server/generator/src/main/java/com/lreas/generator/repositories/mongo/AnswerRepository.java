package com.lreas.generator.repositories.mongo;

import com.lreas.generator.models.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {
}
