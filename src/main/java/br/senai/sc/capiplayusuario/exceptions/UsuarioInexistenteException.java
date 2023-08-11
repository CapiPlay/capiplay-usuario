package br.senai.sc.capiplayusuario.exceptions;

public class UsuarioInexistenteException extends BaseException {
    public UsuarioInexistenteException() {
        super("Usuário não encontrado");
    }
}
