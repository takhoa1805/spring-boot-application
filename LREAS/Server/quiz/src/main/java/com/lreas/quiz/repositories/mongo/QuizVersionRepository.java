package com.lreas.quiz.repositories.mongo;

import com.lreas.quiz.models.QuizVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizVersionRepository extends MongoRepository<QuizVersion, String> {
    List<QuizVersion> findByQuizId(String quizId);
    QuizVersion findByQuizIdAndId(String quizId, String id);
}
