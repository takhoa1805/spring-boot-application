package com.lreas.generator.repositories.jpa;

import com.lreas.generator.models.File;
import com.lreas.generator.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    File findByResource(Resource resource);
}
