package br.senai.sc.capiplayusuario.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
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
    public ResponseEntity<ApiError> handleBaseException(BaseException ex) {
        return badRequest().body(new ApiError(ex.getClass().getSimpleName(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {
        return internalServerError().body( new ApiError(ex.getClass().getSimpleName(), ex.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleValidacao(BindException ex) {

        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();

        if (fieldError != null) {
            ApiError apiError = new ApiError(ex.getClass().getSimpleName(), fieldError.getDefaultMessage());
            return badRequest().body(apiError);
        }

        return badRequest().body(new ApiError("BindException", "Erro de validação"));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleException(ValidationException ex) {
        return badRequest().body(new ApiError(ex.getClass().getSimpleName(), ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleException(ConstraintViolationException ex) {
        var violation = ex.getConstraintViolations().stream().findFirst().orElse(null);

        if (violation != null) {
            return badRequest().body(new ApiError(ex.getClass().getSimpleName(), violation.getMessage()));
        }
        return badRequest().body(new ApiError("ConstraintViolationException", "Erro de validação"));
    }

    static record ApiError(String type, String error) { }
}