package com.backend.pdfs.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="pdfs")
public class PDF {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    private String name;

    private LocalDateTime dateTime;

    private int numOfPages;

    private float size;

    @Transient
    private String url;

    public PDF(){}

    public PDF(String name, LocalDateTime dateTime, int numOfPages, float size){
        this.name = name;
        this.dateTime = dateTime;
        this.numOfPages = numOfPages;
        this.size = size;
    }
    public PDF(long id, String name){
        this.id=id;
        this.name =name;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDateTime(LocalDateTime dateTime){
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime(){
        return this.dateTime;
    }

    public void setNumOfPages(int number){
        this.numOfPages = number;
    }

    public int getNumOfPages(){
        return this.numOfPages;
    }

    public void setSize(float size){
        this.size = size;
    }

    public float getSize(){
        return this.size;
    }

    public void setUrl(String url){ this.url = url; }

    public String getUrl(){
        return this.url;
    }

}
