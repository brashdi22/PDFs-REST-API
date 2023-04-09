package com.backend.pdfs.controllers;

import com.backend.pdfs.entities.PDF;
import com.backend.pdfs.entities.Sentences;
import com.backend.pdfs.errorHandling.CustomException;
import com.backend.pdfs.services.*;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PDFController {
    @Autowired
    PDFService pdfService;

    @Autowired
    MinioService minioService;

    @Autowired
    SentencesService sentencesService;

    @PostMapping("/upload")
    public ResponseEntity<?> pdf(@RequestParam(value = "file") MultipartFile file) throws MinioException, IOException,
            CustomException
    {
        if (pdfService.existsByName(file.getOriginalFilename()))
                throw new CustomException("A file with the specified name already exists",
                        HttpStatus.CONFLICT);
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
        pdfService.insert(pdfFile);

        // Save the pdf's sentences
        sentencesService.insert(new Sentences(pdfFile.getId(), sentences));

        // Save the actual pdf
        minioService.uploadFile(file, pdfFile.getMinioBucket(), name);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Message", "The PDF was uploaded successfully.");

        return new ResponseEntity<>(pdfFile, headers, HttpStatus.OK);
    }

    @GetMapping("/pdfs")
    public ResponseEntity<ArrayList<PDF>> getPdfs() throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException
    {
        ArrayList<PDF> pdfs = pdfService.findAll();
        minioService.setUrls(pdfs);
        return ResponseEntity.ok(pdfs);
    }

    @GetMapping(path = "/pdf/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getPdf(@PathVariable String id, HttpServletResponse response) throws
            IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, CustomException
    {
        PDF pdf = pdfService.findById(id).orElseThrow(() -> new CustomException("PDF not found", HttpStatus.NOT_FOUND));
        InputStream inputStream = minioService.getPdf(pdf.getName(), pdf.getMinioBucket());

        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + pdf.getName());

        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        return ResponseEntity.ok(inputStreamResource);
    }

    @PostMapping("/pdf/{id}")
    public ResponseEntity<?> getOccurrences(@PathVariable String id, @RequestParam(value = "keyword") String keyword)
            throws CustomException
    {
        if (!pdfService.existsById(id))
            throw new CustomException("PDF not found", HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(sentencesService.countOccurrences(id, keyword));
    }

    @DeleteMapping("/pdf/{id}")
    public ResponseEntity<?> deletePdf(@PathVariable String id) throws IOException, ServerException,
            InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException, CustomException
    {
        PDF pdf = pdfService.findById(id).orElseThrow(() ->
                new CustomException("PDF not found", HttpStatus.NOT_FOUND));

        minioService.deletePdf(pdf.getName(), pdf.getMinioBucket());
        sentencesService.deleteById(id);
        pdfService.deleteById(id);
        return ResponseEntity.ok("PDF deleted");
    }

    @GetMapping("/pdf/{id}/sentences")
    public ResponseEntity<?> getPdfSentences(@PathVariable String id) throws CustomException {
        Sentences sentences = sentencesService.findById(id).orElseThrow(() ->
                new CustomException("PDF not found", HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(sentences);
    }

    @GetMapping("/pdf/{id}/{page}")
    public ResponseEntity<byte[]> getPdfSentences(@PathVariable String id, @PathVariable int page) throws IOException,
            ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException,
            InvalidKeyException, InvalidResponseException, XmlParserException, InternalException, CustomException
    {
        PDF pdf = pdfService.findById(id).orElseThrow(() ->
                new CustomException("PDF not found", HttpStatus.NOT_FOUND));
        InputStream inputStream = minioService.getPdf(pdf.getName(), pdf.getMinioBucket());

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                .body(pdfService.getPage(inputStream, page-1));
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchForKeyword(@RequestParam(value = "keyword") String keyword) {
        return ResponseEntity.ok(sentencesService.findBySentencesContaining(keyword));
    }

    @GetMapping("/pdf/{id}/mostCommon")
    public ResponseEntity<?> getMostOccurring(@PathVariable String id) throws CustomException, IOException {
        if (!pdfService.existsById(id))
            throw new CustomException("PDF not found", HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(sentencesService.getMostOccurringWords(id));
    }
}


