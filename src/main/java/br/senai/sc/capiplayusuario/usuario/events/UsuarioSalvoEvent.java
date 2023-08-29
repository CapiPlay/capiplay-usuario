package br.senai.sc.capiplayusuario.usuario.events;

import br.senai.sc.capiplayusuario.model.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class UsuarioSalvoEvent {

    private String id;

    private String nome;

    private String foto;

    private String perfil;

    private String descricao;

    public UsuarioSalvoEvent(Usuario usuario) {
        this.id = usuario.getUuid();
        this.nome = usuario.getNome();
        this.foto = usuario.getFoto();
        this.perfil = usuario.getPerfil();
        this.descricao = usuario.getDescricao();
    }

    public UsuarioSalvoEvent(String id) {
        this.id = id;
    }

}