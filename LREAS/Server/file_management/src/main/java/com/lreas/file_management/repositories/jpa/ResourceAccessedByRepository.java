package com.lreas.file_management.repositories.jpa;

import com.lreas.file_management.models.ResourceAccessedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceAccessedByRepository extends JpaRepository<ResourceAccessedBy, String> {
    @Query(
            value = """
                select r from ResourceAccessedBy r
                where r.resource.user.id = ?1
                and r.resource.resource.id = ?2
            """
    )
    ResourceAccessedBy findByUserAndResource(String userId, String resourceId);

    @Query(
            value = """
                select r from ResourceAccessedBy r
                where r.resource.user.id = ?1
                and r.role = ?2
            """
    )
    List<ResourceAccessedBy> findByUserAndRole(String userId, ResourceAccessedBy.ROLE role);

    @Query(
            value = """
                select r from ResourceAccessedBy r
                where r.resource.resource.id = ?1
                and r.role = ?2
            """
    )
    List<ResourceAccessedBy> findByResourceAndRole(String resourceId, ResourceAccessedBy.ROLE role);

    @Query(
            """
            SELECT r FROM ResourceAccessedBy r
            WHERE r.resource.resource.id IN (
                SELECT f.resource.id FROM File f WHERE f.file_path = ?2
            )
            AND r.resource.user.id = ?1
            """
    )
    ResourceAccessedBy findByUserAndMongoId(String userId, String mongoId);

    @Query(
            value = """
                select r from ResourceAccessedBy r
                where r.resource.resource.id = ?1
            """
    )
    List<ResourceAccessedBy> findByResource(String resourceId);
}