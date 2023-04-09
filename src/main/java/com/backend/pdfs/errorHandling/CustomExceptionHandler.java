package com.backend.pdfs.errorHandling;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InternalException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartException;

import java.net.ConnectException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorClass> handleException(Exception ex) {
        ErrorClass err = new ErrorClass(HttpStatus.BAD_REQUEST, ex);
        return new ResponseEntity<>(err, err.getStatus());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorClass> handleRestClientException(HttpServerErrorException ex) {
        ErrorClass err = new ErrorClass(HttpStatus.valueOf(ex.getStatusCode().value()), ex);
        return new ResponseEntity<>(err, err.getStatus());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorClass> handleRestClientExceptio(HttpClientErrorException ex) {
        ErrorClass err = new ErrorClass(HttpStatus.valueOf(ex.getStatusCode().value()), ex);
        return new ResponseEntity<>(err, err.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorClass> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        ErrorClass err = new ErrorClass(HttpStatus.valueOf(ex.getStatusCode().value()), ex);
        return new ResponseEntity<>(err, err.getStatus());
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorClass> handleCustomException(CustomException ex) {
        ErrorClass err = new ErrorClass(ex.getStatus(), ex);
        return new ResponseEntity<>(err, err.getStatus());
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorClass> handleMultipartExceptionException(MultipartException ex) {
        ErrorClass err = new ErrorClass(HttpStatus.BAD_REQUEST, "No file was provided", ex);
        return new ResponseEntity<>(err, err.getStatus());
    }

    @ExceptionHandler({MinioException.class, ServerException.class, ErrorResponseException.class,
            InternalException.class, ConnectException.class, DataAccessResourceFailureException.class})
    public ResponseEntity<ErrorClass> handleInternalConnectionErrors(Exception ex) {
        ErrorClass err = new ErrorClass(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex);
        return new ResponseEntity<>(err, err.getStatus());
    }

}
