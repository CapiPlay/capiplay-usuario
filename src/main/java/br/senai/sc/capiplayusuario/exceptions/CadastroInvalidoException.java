package br.senai.sc.capiplayusuario.exceptions;

public class CadastroInvalidoException extends BaseException{
    public CadastroInvalidoException() {
        super("Erro ao cadastrar usuário");
    }
}
