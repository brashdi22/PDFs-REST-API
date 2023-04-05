package com.backend.pdfs.services;

import com.backend.pdfs.entities.PDF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PDFRepository extends JpaRepository<PDF, Long> {
}