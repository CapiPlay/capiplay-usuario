package br.senai.sc.capiplayusuario.controller;

import br.senai.sc.capiplayusuario.model.dto.LoginDTO;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.security.TokenService;
import br.senai.sc.capiplayusuario.service.UsuarioService;
import br.senai.sc.capiplayusuario.usuario.projections.UsuarioComentarioProjection;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/cadastro")
    public ResponseEntity<Boolean> criar(@RequestBody UsuarioDTO usuarioDTO) {
        System.out.println("Cadastrando");
        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(usuarioDTO, usuario);
        usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
        usuarioService.salvar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO){
        var usernamePassword = new UsernamePasswordAuthenticationToken
                (loginDTO.email(), loginDTO.senha());
        System.out.println(usernamePassword);
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        System.out.println("Auth");
        return ResponseEntity.status(HttpStatus.OK).body(tokenService.generateToken((Usuario) auth.getPrincipal()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUm(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUm(id));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarTodos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> editar(@PathVariable String id, @RequestBody UsuarioDTO usuarioDTO) {
        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        Usuario usuario = buscarUm(id).getBody();
        assert usuario != null;
        BeanUtils.copyProperties(usuarioDTO, usuario);
        usuarioService.salvar(usuario);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        usuarioService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
