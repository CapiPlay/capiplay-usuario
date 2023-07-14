package br.senai.sc.capiplayusuario.model.entity;

import br.senai.sc.capiplayusuario.usuario.projections.UsuarioComentarioProjection;
import br.senai.sc.capiplayusuario.utils.GeradorUuidUtils;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Entity
@Data
public class Usuario implements UsuarioComentarioProjection {

    @Id
    @Column(length = 36)
    private String uuid;
    private String nome;
    private String perfil; // Apelido
    private String senha;
    private String email;
    private String foto;
    private Date dataNascimento;

    public Usuario() {
        this.uuid = GeradorUuidUtils.gerarUuid();
    }

}
