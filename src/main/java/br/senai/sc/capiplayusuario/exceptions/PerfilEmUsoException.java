package br.senai.sc.capiplayusuario.exceptions;

public class PerfilEmUsoException extends BaseException{
    public PerfilEmUsoException() {
        super("Perfil já está em uso");
    }
}
