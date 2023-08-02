package br.senai.sc.capiplayusuario.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class UsuarioDTO {
    @NotNull
    private String nome;

    @NotBlank
    @NotNull
    private String senha;

    @NotNull
    @Email
    private String email;

    @NotNull
    private String perfil; // Apelido

    private String foto;

    @NotNull
    private Date dataNascimento;
}
