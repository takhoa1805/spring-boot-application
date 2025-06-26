package com.lreas.file_management.repositories.mongo;

import com.lreas.file_management.models.DocumentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentEntity, String> {
}
