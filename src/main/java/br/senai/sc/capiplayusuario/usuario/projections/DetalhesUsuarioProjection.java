package br.senai.sc.capiplayusuario.usuario.projections;

import java.sql.Date;

public interface DetalhesUsuarioProjection {
    String getNome();
    String getEmail();
    String getPerfil();
    String getFoto();
    Date getDataNascimento();
}
