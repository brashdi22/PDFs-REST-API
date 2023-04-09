package com.backend.pdfs.services;

import com.backend.pdfs.entities.PDF;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PDFRepository extends MongoRepository<PDF, String> {
    boolean existsByName(String name);

    boolean existsById(String id);
}