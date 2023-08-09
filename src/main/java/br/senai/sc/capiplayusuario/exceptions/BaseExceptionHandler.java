package br.senai.sc.capiplayusuario.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleNegocioException(BaseException ex, WebRequest request) {
        ApiError apiError = new ApiError(ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(ex.getClass().getSimpleName(), ex.getMessage());
        return ResponseEntity.internalServerError().body(apiError);
    }

    // Adicione outros manipuladores de exceção conforme necessário

    class ApiError {
        private String type;
        private String error;

        ApiError(String type, String message) {
            this.type = type;
            this.error = message;
        }

        public String getType() {
            return type;
        }

        public String getError() {
            return error;
        }
    }
}
