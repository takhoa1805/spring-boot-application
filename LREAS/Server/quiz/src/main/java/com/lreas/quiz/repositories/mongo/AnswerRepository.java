package com.lreas.quiz.repositories.mongo;

import com.lreas.quiz.models.Answer;
import com.lreas.quiz.models.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends MongoRepository<Answer, String> {
    List<Answer> findByQuestion(Question question);
}
