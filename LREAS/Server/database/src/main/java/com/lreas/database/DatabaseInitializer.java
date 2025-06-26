package com.lreas.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
    @PersistenceContext
    private EntityManager em;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void createIndex() {
        em.createNativeQuery(
            """
                CREATE UNIQUE INDEX IF NOT EXISTS unique_owner_per_resource_idx
                ON resource_accessed_by(resource_id)
                WHERE role = 0
            """
        ).executeUpdate();
    }
}
