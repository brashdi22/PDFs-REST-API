package com.backend.pdfs.services;

import com.backend.pdfs.entities.PDF;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
//import com.itextpdf.pdfrenderersdk.render.PdfRenderer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PDFService {
    @Autowired
    private PDFRepository pdfRepository;

    public boolean existsByName(String name){
        return pdfRepository.existsByName(name);
    }

    public boolean existsById(String id){
        return pdfRepository.existsById(id);
    }

    public Optional<PDF> findById(String id) {
        return pdfRepository.findById(id);
    }

    public ArrayList<PDF> findAll(){
        return (ArrayList<PDF>) pdfRepository.findAll();
    }

    public void insert(PDF pdf) {
        pdfRepository.insert(pdf);
    }

    public void deleteById(String id){
        pdfRepository.deleteById(id);
    }

    public ArrayList parsePDF(MultipartFile pdf) throws IOException {
        // Read the pdf file page by page and store the text in a StringBuilder
        InputStream inputStream = pdf.getInputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
            String textFromPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
            stringBuilder.append(textFromPage);
        }

        // Remove unwanted white spaces then split into sentences
        String text = stringBuilder.toString();
        text = text.replaceAll("\s{2,}|[\n\t]", " ");

        ArrayList myList = new ArrayList();
        myList.add(text);
        myList.add(pdfDoc.getNumberOfPages());

        pdfDoc.close();

        return myList;
    }

    public byte[] getPage(InputStream pdf, int pageNum) throws IOException {
        PDDocument document = PDDocument.load(pdf);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        if (pageNum < 0 || pageNum >= document.getNumberOfPages()) {
            throw new IllegalArgumentException("Invalid page number");
        }

        BufferedImage image = pdfRenderer.renderImageWithDPI(pageNum, 300, ImageType.RGB);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        return baos.toByteArray();
    }

}
