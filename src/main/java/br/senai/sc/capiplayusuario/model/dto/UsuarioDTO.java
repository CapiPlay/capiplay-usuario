package br.senai.sc.capiplayusuario.model.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.sql.Date;

@Data
public class UsuarioDTO {


    @NotBlank(message = "O nome não pode ser vazio")
    @Size(max = 50, message = "Nome execede o tamanho máximo")
    private String nome;

    @NotBlank(message = "A senha não pode ser vazia")
    @Size(max = 20, message = "Senha execede o tamanho máximo")
    private String senha;

    @NotEmpty(message = "Email não pode ser vazio")
    @Email(message = "Email inválido")
    @Size(max = 60, message = "Email execede o tamanho máximo")
    private String email;


    @Size(max = 20, message = "Apelido execede o tamanho máximo")
    private String perfil; // Apelido

    private String foto;

    @NotNull(message = "Data de nascimento não pode ser")
    @Past(message = "Data de nascimento está no futuro")
    private Date dataNascimento;
}
