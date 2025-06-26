package com.lreas.file_management.repositories.mongo;

import com.lreas.file_management.models.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {
}
