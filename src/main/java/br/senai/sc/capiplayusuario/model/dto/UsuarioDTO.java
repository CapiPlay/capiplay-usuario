package br.senai.sc.capiplayusuario.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.sql.Date;

@Data
public class UsuarioDTO {

    @NotNull
    @Size(max = 50, message = "Nome execede o tamanho máximo")
    private String nome;

    @NotBlank
    @NotNull
    @Size(max = 20, message = "Senha execede o tamanho máximo")
    private String senha;

    @NotNull
    @NotEmpty
    @Email
    @Size(max = 60, message = "Email execede o tamanho máximo")
    private String email;

    @Size(max = 20, message = "Apelido execede o tamanho máximo")
    private String perfil; // Apelido

    private String foto;

    @NotNull
    @Past(message = "Data de nascimento está no futuro")
    private Date dataNascimento;
}
