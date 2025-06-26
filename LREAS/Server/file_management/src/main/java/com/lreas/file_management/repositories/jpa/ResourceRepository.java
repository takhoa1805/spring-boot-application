package com.lreas.file_management.repositories.jpa;

import com.lreas.file_management.models.Resource;
import com.lreas.file_management.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, String> {
    public List<Resource> findByUser(User user);

    List<Resource> findByParent(Resource parent);

    @Query(
            value =
                    """
                        select r from Resource r
                        where r.id = ?1
                    """)
    Resource findByResourceId(String id);


}
