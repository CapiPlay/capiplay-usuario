package br.senai.sc.capiplayusuario.usuario.events;

import br.senai.sc.capiplayusuario.model.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSalvoEvent {

    private String id;

    private String nome;

    public static UsuarioSalvoEvent from(Usuario usuario) {
        return new UsuarioSalvoEvent(usuario.getUuid(), usuario.getNome());
    }
}