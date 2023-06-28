package br.senai.sc.capiplayusuario.model.enumerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
public enum Categoria {
    ARTESECULTURA("Artes e Cultura"),
    CIENCIAETECNOLOGIA("Ciência e Tecnologia"),
    CULINARIA("Culinária"),
    EDUCACAO("Educação"),
    ENTRETERIMENTO("Entreterimento"),
    ESPORTES("Esportes"),
    DOCUMENTARIO("Documentário"),
    JOGOS("Jogos"),
    LIFESTYLE("Life Style"),
    MODAEBELEZA("Moda e Beleza"),
    MUSICA("Música"),
    VIAGEMETURISMO("Viagem e Turismo");

    private final String nomeCategoria;

    Categoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }
}
