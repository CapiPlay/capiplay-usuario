package br.senai.sc.capiplayusuario.exceptions;

public class BaseException extends RuntimeException {
    BaseException(String message){
        super(message, null, false, false);
    }
}