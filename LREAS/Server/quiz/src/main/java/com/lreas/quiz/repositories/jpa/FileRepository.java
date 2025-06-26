package com.lreas.quiz.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lreas.quiz.models.File;

@Repository
public interface FileRepository extends JpaRepository<File, String>{
}
