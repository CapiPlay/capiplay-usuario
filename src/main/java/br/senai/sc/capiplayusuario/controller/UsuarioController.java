package br.senai.sc.capiplayusuario.controller;

import br.senai.sc.capiplayusuario.infra.messaging.Publisher;
import br.senai.sc.capiplayusuario.model.dto.LoginDTO;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.security.TokenService;
import br.senai.sc.capiplayusuario.service.EmailSenderService;
import br.senai.sc.capiplayusuario.service.UsuarioService;
import br.senai.sc.capiplayusuario.usuario.events.UsuarioSalvoEvent;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Random;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UsuarioController {

    private UsuarioService service;

    private AuthenticationManager authenticationManager;

    private TokenService tokenService;

    private Publisher publisher;

    private EmailSenderService emailSenderService;

    private ResourceLoader resourceLoader;

    @PostMapping("/salvar")
    public ResponseEntity salvar() {
        var id = UUID.randomUUID().toString();
        publisher.publish(new UsuarioSalvoEvent(id, "Teste " + new Random().nextInt()));
        return created(URI.create(id)).build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Boolean> criar(@ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                         @RequestParam(value = "foto1", required = false) MultipartFile multipartFile) {

        if (usuarioDTO.getPerfil().isEmpty()) {
            usuarioDTO.setPerfil(service.nomePadrao(usuarioDTO.getEmail()));
        }

        usuarioDTO.setFoto(service.salvarFoto(multipartFile, usuarioDTO.getPerfil()));

        if (service.salvar(usuarioDTO) != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        } else {
            System.out.println("else");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
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
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarUm(usuarioId));
    }

    @GetMapping("/verifyEmail/{uuid}")
    public ResponseEntity<Resource> verifyEmail(@PathVariable String uuid){
        try {
            System.out.println(uuid);
            for (Usuario u:service.buscarTodos()) {
                if (u.getUuid().equals(uuid)){
                    u.setEnabled(true);
                    service.alterarCampos(u);
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
            Resource resource = resourceLoader.getResource("classpath:static/verify.html");
            if (resource.exists()) {
                return ResponseEntity.ok(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    @PutMapping
    public ResponseEntity<Boolean> editar(@RequestHeader String usuarioId,
                                          @ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                          @RequestParam(name="foto1", required = false) MultipartFile multipartFile) {
        if (service.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                service.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        usuarioId = usuarioId.replace("\"", "");

        if (usuarioDTO.getPerfil().isEmpty()) {
            usuarioDTO.setPerfil(service.nomePadrao(usuarioDTO.getEmail()));
        }

        usuarioDTO.setFoto(service.salvarFoto(multipartFile, usuarioDTO.getPerfil()));
        if (service.editar(usuarioDTO, usuarioId)) {
            return ResponseEntity.status(HttpStatus.OK).body(true);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(@RequestHeader String usuarioId) {
        usuarioId = usuarioId.replace("\"", "");
        service.deletar(usuarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
