package br.senai.sc.capiplayusuario.controller;

import br.senai.sc.capiplayusuario.infra.messaging.Publisher;
import br.senai.sc.capiplayusuario.model.dto.EditarUsuarioCommand;
import br.senai.sc.capiplayusuario.model.dto.LoginDTO;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.security.TokenService;
import br.senai.sc.capiplayusuario.service.UsuarioService;
import br.senai.sc.capiplayusuario.usuario.events.UsuarioSalvoEvent;
import br.senai.sc.capiplayusuario.usuario.projections.DetalhesUsuarioProjection;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.created;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    private final EmailSenderService emailSenderService;

    private final ResourceLoader resourceLoader;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final Publisher publisher;


//    private ResourceLoader resourceLoader;
    @PostMapping("/salvar")
    public ResponseEntity salvar() {
        var id = UUID.randomUUID().toString();
        publisher.publish(new UsuarioSalvoEvent(id, "Teste " + new Random().nextInt()));
        return created(URI.create(id)).build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Boolean> criar(@ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                         @RequestParam(value = "foto1", required = false) MultipartFile multipartFile) throws IOException {
        if (usuarioDTO.getPerfil().isEmpty()) {
            usuarioDTO.setPerfil(service.nomePadrao(usuarioDTO.getEmail()));
        }

        if (service.salvar(usuarioDTO, Objects.nonNull(multipartFile) ? multipartFile.getBytes():null) != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(true);
        } else {
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
    public ResponseEntity<DetalhesUsuarioProjection> detalhe(@RequestHeader String usuarioId) {
        usuarioId = usuarioId.replace("\"", "");
        return ResponseEntity.status(HttpStatus.OK).body(service.buscarUm(usuarioId));
    }

//    @GetMapping("/verifyEmail/{uuid}")
//    public ResponseEntity<Resource> verifyEmail(@PathVariable String uuid){
//        try {
//            System.out.println(uuid);
//            for (Usuario u:service.buscarTodos()) {
//                if (u.getUuid().equals(uuid)){
//                    u.setEnabled(true);
//                    service.alterarCampos(u);
//                } else {
//                    return ResponseEntity.notFound().build();
//                }
//            }
//            Resource resource = resourceLoader.getResource("classpath:static/verify.html");
//            if (resource.exists()) {
//                return ResponseEntity.ok(resource);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(500).build();
//        }
//    }


//    @PutMapping
//    public ResponseEntity<Boolean> editar(@RequestHeader String usuarioId,
//                                          @ModelAttribute @Valid UsuarioEditDTO usuarioDTO,
//                                          @RequestParam(name="foto1", required = false) MultipartFile multipartFile) throws IOException {
//
//
//        usuarioDTO.setFoto(service.salvarFoto(multipartFile.getBytes(), usuarioDTO.getPerfil()));
//        if (service.editar(usuarioDTO, usuarioId)) {
//            return ResponseEntity.status(HttpStatus.OK).body(true);
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
//    }

    @PutMapping
    public void editar(@RequestHeader("usuarioId") String usuarioId,
                       @ModelAttribute EditarUsuarioCommand cmd,
                       @RequestParam(name="foto1", required = false) MultipartFile foto) throws IOException {
        service.handle(cmd.from(usuarioId, foto));
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(@RequestHeader String usuarioId) {
        service.deletar(usuarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
