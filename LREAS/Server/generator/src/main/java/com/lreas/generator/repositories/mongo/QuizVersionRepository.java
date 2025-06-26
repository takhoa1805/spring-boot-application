package com.lreas.generator.repositories.mongo;

import com.lreas.generator.models.QuizVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizVersionRepository extends MongoRepository<QuizVersion, String> {
}
