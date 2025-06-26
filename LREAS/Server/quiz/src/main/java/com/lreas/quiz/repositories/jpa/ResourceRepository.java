package com.lreas.quiz.repositories.jpa;

import com.lreas.quiz.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {
}
