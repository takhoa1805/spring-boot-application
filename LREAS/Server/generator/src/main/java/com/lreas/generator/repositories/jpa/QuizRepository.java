package com.lreas.generator.repositories.jpa;

import com.lreas.generator.models.Quiz;
import com.lreas.generator.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {
    Quiz findByResource(Resource resource);
}
