package com.lreas.database;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizVersionRepository extends MongoRepository<QuizVersion, String> {
}
