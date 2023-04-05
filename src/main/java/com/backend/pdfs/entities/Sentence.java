package com.backend.pdfs.entities;

import jakarta.persistence.*;

@Entity
@Table(name="sentences")
public class Sentence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private PDF parentPDF;

    private String content;

    public Sentence(){}

    public Sentence(PDF parentPDF, String content){
        this.parentPDF = parentPDF;
        this.content = content;
    }

    public Sentence(long id, String content){
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public PDF getParentPDF() {
        return this.parentPDF;
    }

    public void setParentPDF(PDF parentPDF) {
        this.parentPDF = parentPDF;
    }
}
