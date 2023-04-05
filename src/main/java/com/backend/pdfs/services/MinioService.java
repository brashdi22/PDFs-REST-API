package com.backend.pdfs.services;

import com.backend.pdfs.entities.PDF;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.RemoveObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

@Service
public class MinioService{
    @Autowired
    MinioClient minioClient;

    public void uploadFile(MultipartFile file, String bucketName, String fileName) throws MinioException {
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(args);
        } catch (Exception e) {
            throw new MinioException(e.getMessage(), e.toString());
        }
    }

    public InputStream getPdf(String fileName, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();
        InputStream inputStream = minioClient.getObject(args);
        return inputStream;
    }

    public void deletePdf(String fileName, String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build();

        minioClient.removeObject(args);
    }

    public void setUrls(ArrayList<PDF> pdfs) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        for (PDF pdf : pdfs) {
            GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket("pdfs")
                    .object(pdf.getName())
                    .expiry(3600)
                    .build();

            String url = minioClient.getPresignedObjectUrl(args);
            pdf.setUrl(url);
        }
    }
}