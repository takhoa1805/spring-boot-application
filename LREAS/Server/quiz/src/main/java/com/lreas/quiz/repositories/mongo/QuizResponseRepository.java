package com.lreas.quiz.repositories.mongo;

import com.lreas.quiz.models.QuizResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResponseRepository extends MongoRepository<QuizResponse, String> {
    List<QuizResponse> findByDoQuizId(String doQuizId);

    @Query(
        value = """
            { 
                'question': ObjectId(?0),
                'do_quiz_id': ?1, 
            }
        """
    )
    List<QuizResponse> getByQuestionIdAndDoQuizId(String questionId, String doQuizId);
}
