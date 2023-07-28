package br.senai.sc.capiplayusuario.controller;

import br.senai.sc.capiplayusuario.infra.messaging.Publisher;
import br.senai.sc.capiplayusuario.model.dto.LoginDTO;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.security.TokenService;
import br.senai.sc.capiplayusuario.service.UsuarioService;
import br.senai.sc.capiplayusuario.usuario.events.UsuarioSalvoEvent;
import br.senai.sc.capiplayusuario.usuario.projections.UsuarioComentarioProjection;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final Publisher publisher;

    @PostMapping("/salvar")
    public ResponseEntity salvar() {
        var id = UUID.randomUUID().toString();
        publisher.publish(new UsuarioSalvoEvent(id, id));
        return created(URI.create(id)).build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Boolean> criar(@ModelAttribute UsuarioDTO usuarioDTO,
                                         @RequestParam("foto1") MultipartFile multipartFile) {
        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        usuarioDTO.setFoto(usuarioService.salvarFoto(multipartFile));
        usuarioService.salvar(usuarioDTO);
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
    public ResponseEntity<Boolean> editar(@PathVariable String id,
                                          @ModelAttribute UsuarioDTO usuarioDTO,
                                          @RequestParam("foto1") MultipartFile multipartFile) {
        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        usuarioDTO.setFoto(usuarioService.salvarFoto(multipartFile));
        usuarioService.editar(usuarioDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        usuarioService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}