package com.lreas.generator.repositories.jpa;

import com.lreas.generator.models.Resource;
import com.lreas.generator.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {
    Resource findByMongoId(String mongoId);
}
