package br.senai.sc.capiplayusuario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.internalServerError;

@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleNegocioException(BaseException ex) {
        ApiError apiError = new ApiError(ex.getClass().getSimpleName(), ex.getMessage());
        return badRequest().body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception ex) {
        ApiError apiError = new ApiError(ex.getClass().getSimpleName(), ex.getMessage());
        return internalServerError().body(apiError);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> validacoesInvalidas(BindException ex) {

        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();

        if (fieldError != null) {
            String fieldName = fieldError.getField();
            ApiError apiError = new ApiError(ex.getClass().getSimpleName(), fieldError.getDefaultMessage());
            return badRequest().body(apiError);
        }

        return badRequest().body("Erro de validação");
    }


    static record ApiError(String type, String error) { }
}