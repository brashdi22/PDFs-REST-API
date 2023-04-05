package com.backend.pdfs.controllers;

import com.backend.pdfs.entities.PDF;
import com.backend.pdfs.entities.Sentence;
import com.backend.pdfs.entities.SentenceBody;
import com.backend.pdfs.services.MinioService;
import com.backend.pdfs.services.PDFService;
import com.backend.pdfs.services.SentenceService;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PDFController {
    @Autowired
    PDFService pdfService;

    @Autowired
    SentenceService sentenceService;

    @Autowired
    MinioService minioService;

    @PostMapping("/uploadPdf")
    public ResponseEntity<?> pdf(@RequestParam(value = "file") MultipartFile file) throws Exception {

        try {
            ArrayList textAndPages = pdfService.parsePDF(file);

            String text = (String) textAndPages.get(0);
            int numOfPages = (int) textAndPages.get(1);

            // Create a new PDF instance for the input file
            String name = file.getOriginalFilename();
            LocalDateTime dateTime = LocalDateTime.now();
            float size = file.getSize();
            PDF pdfFile = new PDF(name, dateTime, numOfPages, size);

            // Initialise the model that will be used to extract the sentences
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream modelIn = classLoader.getResourceAsStream("static/en-sent.bin");
            SentenceModel model = new SentenceModel(modelIn);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

            // Extract the sentences
            String[] sentences = sentenceDetector.sentDetect(text);

            // Save the pdf's metadata
            pdfService.save(pdfFile);

            // Save the Sentences
            for (String sentence : sentences) {
                sentenceService.save(new Sentence(pdfFile, sentence));
            }

            // Save the actual pdf
            minioService.uploadFile(file, "pdfs", name);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Message", "The PDF was uploaded successfully.");

            return new ResponseEntity<>(pdfFile, headers, HttpStatus.OK);

        } catch (Exception e) {
            throw e;
//            return new ResponseEntity<>("Error uploading the file.", HttpStatus.OK);
        }

    }

    @GetMapping("/pdfs")
    public ResponseEntity<ArrayList<PDF>> getPdfs() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        ArrayList<PDF> pdfs = pdfService.findAll();
        minioService.setUrls(pdfs);
        return ResponseEntity.ok(pdfs);
    }

    @GetMapping(path = "/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable long id, HttpServletResponse response) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        PDF pdf = pdfService.findById(id).orElseThrow(FileNotFoundException::new);
        InputStream inputStream = minioService.getPdf(pdf.getName(), "pdfs");

        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + pdf.getName());

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return ResponseEntity.ok(inputStreamResource);
    }

    @GetMapping("/pdf/{id}/sentences")
    public ResponseEntity<?> getPdfSentences(@PathVariable long id) throws IOException {
        PDF pdf = pdfService.findById(id).orElseThrow(FileNotFoundException::new);
        List<SentenceBody> sentences = sentenceService.findAllByParentId(pdf.getId());
        return ResponseEntity.ok(sentences);
    }

    @DeleteMapping("/pdf/{id}")
    public ResponseEntity<?> deletePdf(@PathVariable long id) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        PDF pdf = pdfService.findById(id).orElseThrow(FileNotFoundException::new);
        minioService.deletePdf(pdf.getName(), "pdfs");
        pdfService.deleteById(id);
        return ResponseEntity.ok("PDF deleted");
    }

    @GetMapping("/pdf/{id}/{page}")
    public ResponseEntity<byte[]> getPdfSentences(@PathVariable long id, @PathVariable int page) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        PDF pdf = pdfService.findById(id).orElseThrow(FileNotFoundException::new);
        InputStream inputStream = minioService.getPdf(pdf.getName(), "pdfs");

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(pdfService.getPage(inputStream, page-1));
    }

    @PostMapping("/pdf/{id}")
    public ResponseEntity<?> getOccurrences(@PathVariable long id, @RequestParam(value = "keyword") String keyword){
//        List<SentenceBody> sentences = sentenceService.findAllByParentId(id);
        List<String> sentences = sentenceService.findSentenceContaining(keyword);

        return ResponseEntity.ok(sentences);
    }

}


