package br.senai.sc.capiplayusuario.controller;

import br.senai.sc.capiplayusuario.infra.messaging.Publisher;
import br.senai.sc.capiplayusuario.model.dto.LoginDTO;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.security.TokenService;
import br.senai.sc.capiplayusuario.service.UsuarioService;
import br.senai.sc.capiplayusuario.usuario.events.UsuarioSalvoEvent;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UsuarioController {

    private UsuarioService usuarioService;

    private AuthenticationManager authenticationManager;

    private TokenService tokenService;

    private Publisher publisher;

    @PostMapping("/salvar")
    public ResponseEntity salvar() {
        var id = UUID.randomUUID().toString();
        publisher.publish(new UsuarioSalvoEvent(id, "Teste " + new Random().nextInt()));
        return created(URI.create(id)).build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Boolean> criar(@ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                         @RequestParam("foto1") MultipartFile multipartFile) {

        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }

        if (usuarioDTO.getPerfil().isEmpty()) {
            usuarioDTO.setPerfil(usuarioService.nomePadrao(usuarioDTO.getEmail()));
        }

        usuarioDTO.setFoto(usuarioService.salvarFoto(multipartFile, usuarioDTO.getPerfil()));

        if (usuarioService.salvar(usuarioDTO)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
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

    @GetMapping
    public ResponseEntity<Usuario> detalhe(@RequestHeader String usuarioId) {
        usuarioId = usuarioId.replace("\"", "");
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUm(usuarioId));
    }

//    @GetMapping
//    public ResponseEntity<List<Usuario>> buscarTodos() {
//        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarTodos());
//    }


    @PutMapping
    public ResponseEntity<Boolean> editar(@RequestHeader String usuarioId,
                                          @ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                          @RequestParam("foto1") MultipartFile multipartFile) {
        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        usuarioId = usuarioId.replace("\"", "");

        if (usuarioDTO.getPerfil().isEmpty()) {
            usuarioDTO.setPerfil(usuarioService.nomePadrao(usuarioDTO.getEmail()));
        }

        usuarioDTO.setFoto(usuarioService.salvarFoto(multipartFile, usuarioDTO.getPerfil()));
        if (usuarioService.editar(usuarioDTO, usuarioId)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(@RequestHeader String usuarioId) {
        usuarioId = usuarioId.replace("\"", "");
        usuarioService.deletar(usuarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
