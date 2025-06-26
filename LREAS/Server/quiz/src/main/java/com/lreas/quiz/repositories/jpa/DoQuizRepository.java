package com.lreas.quiz.repositories.jpa;

import com.lreas.quiz.models.DoQuiz;
import com.lreas.quiz.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DoQuizRepository extends JpaRepository<DoQuiz, String> {
    List<DoQuiz> findByQuizVersionIdAndUserAndSubmitTime(String quizVersionId, User user, Date submitTime);
    List<DoQuiz> findByQuizVersionIdAndUser(String quizVersionId, User user);
    List<DoQuiz> findByQuizVersionId(String quizVersionId);
    List<DoQuiz> findByQuizVersionIdAndSubmitTime(String quizVersionId, Date submitTime);
}
