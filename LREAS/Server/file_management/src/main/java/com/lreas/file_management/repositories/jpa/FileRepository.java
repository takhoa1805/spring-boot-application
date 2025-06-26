package com.lreas.file_management.repositories.jpa;

import com.lreas.file_management.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lreas.file_management.models.File;

@Repository
public interface FileRepository extends JpaRepository<File, String>{
    File findByResource(Resource resource);
}
