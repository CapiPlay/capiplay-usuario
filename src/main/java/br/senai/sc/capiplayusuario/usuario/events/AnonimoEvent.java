package br.senai.sc.capiplayusuario.usuario.events;

import lombok.Data;

@Data
public class AnonimoEvent {
     String id;

    public AnonimoEvent(String id) {
        this.id = id;
    }
}
