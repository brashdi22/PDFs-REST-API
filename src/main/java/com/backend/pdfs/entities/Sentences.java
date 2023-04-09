package com.backend.pdfs.entities;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "pdf_content")
public class Sentences {

    @Id
    private String id;

    private String[] sentences;

    public Sentences (String id, String[] sentences){
        this.id = id;
        this.sentences = sentences;
    }
}
