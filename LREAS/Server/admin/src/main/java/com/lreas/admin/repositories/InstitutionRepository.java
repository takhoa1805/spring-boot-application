package com.lreas.admin.repositories;

import com.lreas.admin.models.Institution;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InstitutionRepository extends JpaRepository<Institution, String> {
    public List<Institution> findByName(String institutionName);
    public List<Institution> findBySubdomain(String subdomain);
}
