package br.senai.sc.capiplayusuario.model.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class UsuarioDTO {
    private String nome;
    private String senha;
    private String email;
    private String perfil; // Apelido
    private String foto;
    private Date dataNascimento;
}
