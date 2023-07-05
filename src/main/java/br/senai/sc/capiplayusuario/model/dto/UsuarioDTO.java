package br.senai.sc.capiplayusuario.model.dto;

import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.model.enumerator.Categoria;
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
    private List<Usuario> canaisInscritos;
    private List<Categoria> categorias;
}
