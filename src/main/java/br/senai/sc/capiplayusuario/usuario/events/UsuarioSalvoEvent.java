package br.senai.sc.capiplayusuario.usuario.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSalvoEvent {

    private String id;

    private String nome;
}