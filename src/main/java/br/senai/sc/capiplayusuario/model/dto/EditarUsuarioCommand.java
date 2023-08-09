package br.senai.sc.capiplayusuario.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EditarUsuarioCommand {

    @NotNull
    private String id;

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

    private MultipartFile foto;

    public EditarUsuarioCommand from(String id, MultipartFile foto){
        this.id = id;
        this.foto = foto;
        return this;
    }
}
