package com.backend.pdfs.services;

import com.backend.pdfs.entities.PDF;
import com.backend.pdfs.entities.Sentence;
import com.backend.pdfs.entities.SentenceBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long>{
    List<Sentence> findAllByParentPDF(PDF parentPdf);

    @Query(value = "SELECT s.id as id, s.parent_id as parentId, s.content as content FROM sentences s WHERE s.parent_id = :parentId", nativeQuery = true)
    Collection<SentenceBody> findAllByParentId(@Param("parentId") long parentId);

    @Query("SELECT s.content FROM sentences s WHERE s.content LIKE %:keyword%")
    List<String> findSentenceContaining(@Param("keyword") String keyword);

}