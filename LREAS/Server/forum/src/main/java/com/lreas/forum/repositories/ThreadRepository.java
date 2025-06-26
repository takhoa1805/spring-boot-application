package com.lreas.forum.repositories;

import com.lreas.forum.models.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lreas.forum.models.Thread;

import java.util.List;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, String> {
    List<Thread> findByTopic(Topic topic);

    Page<Thread> findByTopicAndWorkflowState(Topic topic, Thread.STATE workflowState, Pageable pageable);
    Thread findByIdAndWorkflowState(String id, Thread.STATE state);
}
