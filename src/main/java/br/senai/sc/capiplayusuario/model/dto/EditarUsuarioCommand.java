package br.senai.sc.capiplayusuario.model.dto;

import br.senai.sc.capiplayusuario.utils.validacao.senha.SenhaForte;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static java.util.Objects.nonNull;

@Data
public class EditarUsuarioCommand {

    private String id;

//    @NotBlank(message = "O nome não pode ser vazio")
    @Size(max = 50, message = "Nome execede o tamanho máximo")
    private String nome;

    @Size(max = 20, message = "Apelido execede o tamanho máximo")
    private String perfil;

//    @NotBlank(message = "A senha não pode ser vazia")
//    @Size(max = 20, min = 6, message = "Senha deve conter entre 6 a 20 caracteres")
//    @SenhaForte
    private String senha;

    @Size(max = 250, message = "Descrição excede o tamanho máximo")
    private String descricao;

    private byte[] foto;

    public EditarUsuarioCommand from(String id,  MultipartFile foto) throws IOException {
        this.id = id;
        if(nonNull(foto))
            this.foto = foto.getBytes();
        return this;
    }
}
