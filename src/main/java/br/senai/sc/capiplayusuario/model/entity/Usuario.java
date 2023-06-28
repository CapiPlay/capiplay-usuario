package br.senai.sc.capiplayusuario.model.entity;

import br.senai.sc.capiplayusuario.model.enumerator.Categoria;
import br.senai.sc.capiplayusuario.usuario.projections.UsuarioDetalhesProjection;
import br.senai.sc.capiplayusuario.utils.GeradorUuidUtils;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Entity
@Data
public class Usuario implements UsuarioDetalhesProjection {

    @Id
    @Column(length = 36)
    private String uuid;
    private String nome;
    private String senha;
    private String email;
    private String foto;
    private Date dataNascimento;

    @ManyToMany
    private List<Usuario> canaisInscritos;

    @Enumerated(EnumType.STRING)
    private List<Categoria> categorias;

    public Usuario() {
        this.uuid = GeradorUuidUtils.gerarUuid();
    }

}
