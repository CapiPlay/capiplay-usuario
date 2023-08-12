package br.senai.sc.capiplayusuario.usuario.events;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioExcluidoEvent {

    private String identificador;

}