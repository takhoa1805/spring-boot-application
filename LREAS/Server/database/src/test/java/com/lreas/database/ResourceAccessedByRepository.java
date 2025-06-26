package com.lreas.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceAccessedByRepository extends JpaRepository<ResourceAccessedBy, String> {
    ResourceAccessedBy findByResourceResourceAndResourceUser(Resource resource, User user);
}
