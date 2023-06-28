package br.senai.sc.capiplayusuario.model.dto;

import br.senai.sc.capiplayusuario.model.enumerator.Categoria;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class UsuarioDTO {

    private String nome;
    private String senha;
    private String email;
    private String foto;
    private Date dataNascimento;
    private List<Categoria> categorias;

}
