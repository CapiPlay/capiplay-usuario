package br.senai.sc.capiplayusuario.controller;

import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.service.UsuarioService;
import br.senai.sc.capiplayusuario.usuario.projections.UsuarioComentarioProjection;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/usuario")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioComentarioProjection> criar(@RequestBody UsuarioDTO usuarioDTO){
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, usuario);
        System.out.println("DTO: " + usuarioDTO);
        System.out.println("Obj: " + usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.salvar(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUm(@PathVariable String id){
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUm(id));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos(){
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> editar(@PathVariable String id, @RequestBody UsuarioDTO usuarioDTO){
        Usuario usuario = buscarUm(id).getBody();
        assert usuario != null;
        BeanUtils.copyProperties(usuarioDTO, usuario);
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.salvar(usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id){
        usuarioService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
