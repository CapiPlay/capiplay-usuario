package br.senai.sc.capiplayusuario.exceptions;

public class EmailEmUsoException extends BaseException{
    public EmailEmUsoException() {
        super("Este e-mail já está em uso");
    }
}
