package com.backend.pdfs.services;

import com.backend.pdfs.entities.PDF;
import com.backend.pdfs.entities.Sentence;
import com.backend.pdfs.entities.SentenceBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SentenceService {
    @Autowired
    private SentenceRepository sentenceRepository;

    public List<Sentence> findAllByParentPDF(PDF parentPdf){
        return sentenceRepository.findAllByParentPDF(parentPdf);
    }

    public List<SentenceBody> findAllByParentId(long parentId) {
        return (List<SentenceBody>) sentenceRepository.findAllByParentId(parentId);
    }

    public List<String> findSentenceContaining(String keyword){
        return sentenceRepository.findSentenceContaining(keyword);
    }

    public void save(Sentence sentence){
        sentenceRepository.save(sentence);
    }
}
