package br.senai.sc.capiplayusuario.exceptions;

public class UsuarioInexistente extends RuntimeException{
    public UsuarioInexistente() {
        super("Usuário não encontrado");
    }
}
