package br.senai.sc.capiplayusuario.repository;

import br.senai.sc.capiplayusuario.model.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Usuario findByEmail(String email);
    Usuario findByPerfil(String perfil);

    @Query(value = "select perfil from db_capiplay_usuario.usuario where lower(perfil) like lower(concat(:perfil,'%'))", nativeQuery = true)
    Set<String> findAllByPerfil(String perfil);

}
