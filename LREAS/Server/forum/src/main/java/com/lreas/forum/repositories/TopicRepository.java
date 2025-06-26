package com.lreas.forum.repositories;

import com.lreas.forum.models.Institution;
import com.lreas.forum.models.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, String> {
    List<Topic> findByInstitution(Institution institution);
    Page<Topic> findByInstitutionAndWorkflowState(Institution institution, Topic.STATE workflowState, Pageable pageable);
    Topic findByIdAndWorkflowState(String id, Topic.STATE workflowState);
}
