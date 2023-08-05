package br.senai.sc.capiplayusuario.exceptions;

public class UsuarioInexistente extends BaseException {
    public UsuarioInexistente() {
        super("Usuário não encontrado");
    }
}
