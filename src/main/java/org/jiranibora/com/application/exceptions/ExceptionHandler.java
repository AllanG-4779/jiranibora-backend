package org.jiranibora.com.application.exceptions;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {
     @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
     @ResponseStatus(HttpStatus.BAD_REQUEST)
     public LinkedHashMap<?,?> handleMethodArgument(MethodArgumentNotValidException ex){
         LinkedHashMap <String,String> result = new LinkedHashMap<>();
//         get the list of field errors
         List<FieldError> list = ex.getBindingResult().getFieldErrors();
          list.forEach(error->result.put(error.getField(), error.getDefaultMessage()));
          return result;

    }
    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<?> handleSqlIntegrityException(SQLIntegrityConstraintViolationException ex){
         Map<String,String> map = new LinkedHashMap<>();
         map.put("message",ex.getMessage().split(" ")[2]);
         return ResponseEntity.status(611).body(map);



    }
}
