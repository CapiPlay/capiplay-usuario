package br.senai.sc.capiplayusuario.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioEditDTO {
    @NotNull
    @Size(max = 50, message = "Nome execede o tamanho máximo")
    private String nome;

    @Size(max = 20, message = "Apelido execede o tamanho máximo")
    private String perfil;

    @NotBlank
    @NotNull
    @Size(max = 20, message = "Senha execede o tamanho máximo")
    private String senha;

    @Size(max = 250, message = "Descrição excede o tamanho máximo")
    private String descricao;

    private String foto;
}
