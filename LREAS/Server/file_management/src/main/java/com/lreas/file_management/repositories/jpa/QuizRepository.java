package com.lreas.file_management.repositories.jpa;

import com.lreas.file_management.models.Quiz;
import com.lreas.file_management.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {
    Quiz findByResource(Resource resource);
}
