package br.senai.sc.capiplayusuario.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static java.util.Objects.nonNull;

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

    private byte[] foto;

    public EditarUsuarioCommand from(String id, MultipartFile foto) throws IOException {
        this.id = id;
        if(nonNull(foto))
            this.foto = foto.getBytes();
        return this;
    }
}
