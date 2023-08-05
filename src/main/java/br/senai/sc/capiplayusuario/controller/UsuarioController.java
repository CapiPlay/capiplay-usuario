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
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/api/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private Publisher publisher;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("/salvar")
    public ResponseEntity salvar() {
        var id = UUID.randomUUID().toString();
        publisher.publish(new UsuarioSalvoEvent(id, "Teste " + new Random().nextInt()));
        return created(URI.create(id)).build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Boolean> criar(@ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                         @RequestParam(value = "foto1", required = false)  MultipartFile multipartFile) {
        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        System.out.println(usuarioDTO.getSenha().equals(""));
        //usuarioDTO.setFoto(usuarioService.salvarFoto(multipartFile, usuarioDTO.getPerfil()));
        Usuario usuario = usuarioService.salvar(usuarioDTO);
        emailSenderService.validEmail(usuario.getEmail(), "Valiação de Email", usuario.getUuid());
        usuario.setEnabled(false);
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

    @GetMapping("/detalhe")
    public ResponseEntity<Usuario> detalhe(@RequestHeader String usuarioId) {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarUm(usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> buscarTodos() {
        return ResponseEntity.status(HttpStatus.OK).body(usuarioService.buscarTodos());
    }

    @GetMapping("/verifyEmail/{uuid}")
    public ResponseEntity<Resource> verifyEmail(@PathVariable String uuid){
        try {
            System.out.println(uuid);
            for (Usuario u:usuarioService.buscarTodos()) {
                if (u.getUuid().equals(uuid)){
                    u.setEnabled(true);
                    usuarioService.alterarCampos(u);
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


    @PutMapping("/{id}")
    public ResponseEntity<Boolean> editar(@PathVariable String id,
                                          @ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                          @RequestParam("foto1") MultipartFile multipartFile) {
        if (usuarioService.buscarPorPerfil(usuarioDTO.getPerfil()) != null ||
                usuarioService.buscarPorEmail(usuarioDTO.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
        usuarioDTO.setFoto(usuarioService.salvarFoto(multipartFile, usuarioDTO.getPerfil()));
        usuarioService.editar(usuarioDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        usuarioService.deletar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
