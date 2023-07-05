package br.senai.sc.capiplayusuario.repository;

import br.senai.sc.capiplayusuario.model.entity.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
