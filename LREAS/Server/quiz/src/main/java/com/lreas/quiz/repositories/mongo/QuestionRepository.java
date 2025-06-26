package com.lreas.quiz.repositories.mongo;

import com.lreas.quiz.models.Question;
import com.lreas.quiz.models.QuizVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    Question findByIdAndQuizVersion(String id, QuizVersion quizVersion);
    List<Question> findByQuizVersion(QuizVersion quizVersion);
    Integer countByQuizVersion(QuizVersion quizVersion);
}
