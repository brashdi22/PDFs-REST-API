package com.backend.pdfs.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection="pdfs")
public class PDF {
    @Id
    private String id;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime uploadTime;

    private int numOfPages;

    private float size;

    private String minioBucket;

    private String url;

    public PDF(){}

    public PDF(String name, LocalDateTime uploadTime, int numOfPages, float size){
        this.name = name;
        this.uploadTime = uploadTime;
        this.numOfPages = numOfPages;
        this.size = size;
        this.minioBucket = "pdfs";
    }
    public PDF(String name, LocalDateTime uploadTime, int numOfPages, float size, String minioBucket){
        this.name = name;
        this.uploadTime = uploadTime;
        this.numOfPages = numOfPages;
        this.size = size;
        this.minioBucket = minioBucket;
    }

}
