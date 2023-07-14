package br.senai.sc.capiplayusuario.service;

import br.senai.sc.capiplayusuario.exceptions.UsuarioInexistente;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public void salvar(Usuario usuario){
        usuarioRepository.save(usuario);
    }

    public Usuario buscarUm(String id){
        return usuarioRepository
                .findById(id)
                .orElseThrow(UsuarioInexistente::new);
    }

    public List<Usuario> buscarTodos(){
        return usuarioRepository.findAll();
    }

    public void deletar(String id){
        usuarioRepository.deleteById(id);
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario buscarPorPerfil(String perfil) {
        return usuarioRepository.findByPerfil(perfil);
    }
}
