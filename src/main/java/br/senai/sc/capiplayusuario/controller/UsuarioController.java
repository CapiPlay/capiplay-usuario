package br.senai.sc.capiplayusuario.controller;

import br.senai.sc.capiplayusuario.infra.messaging.Publisher;
import br.senai.sc.capiplayusuario.model.dto.EditarUsuarioCommand;
import br.senai.sc.capiplayusuario.model.dto.LoginDTO;
import br.senai.sc.capiplayusuario.model.dto.UsuarioDTO;
import br.senai.sc.capiplayusuario.model.entity.Usuario;
import br.senai.sc.capiplayusuario.security.TokenService;
import br.senai.sc.capiplayusuario.service.EmailSenderService;
import br.senai.sc.capiplayusuario.service.UsuarioService;
import br.senai.sc.capiplayusuario.usuario.events.AnonimoEvent;
import br.senai.sc.capiplayusuario.usuario.events.UsuarioSalvoEvent;
import br.senai.sc.capiplayusuario.usuario.projections.DetalhesUsuarioProjection;
import com.rabbitmq.client.Return;
import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
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
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

//    private final EmailSenderService emailSenderService;

//    private final ResourceLoader resourceLoader;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    private final Publisher publisher;

//    private ResourceLoader resourceLoader;

    @PostMapping("/salvar")
    public ResponseEntity salvar() {
        var id = UUID.randomUUID().toString();
        return created(URI.create(id)).build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Boolean> criar(@ModelAttribute @Valid UsuarioDTO usuarioDTO,
                                         @RequestParam(value = "foto1", required = false) MultipartFile multipartFile) throws IOException {
        service.salvar(usuarioDTO, Objects.nonNull(multipartFile) ? multipartFile.getBytes() : null);
        return ResponseEntity.status(HttpStatus.CREATED).body(true);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginDTO loginDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken
                (loginDTO.email(), loginDTO.senha());

        Authentication auth = authenticationManager.authenticate(usernamePassword);
        Usuario usuario = (Usuario) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(tokenService.generateToken(usuario.getUuid()));
    }

    @GetMapping
    public ResponseEntity<DetalhesUsuarioProjection> detalhe(@RequestHeader String usuarioId) {
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
                                          @RequestParam(name = "foto1", required = false) MultipartFile foto) throws IOException {
        service.handle(cmd.from(usuarioId, foto));
    }

    @DeleteMapping
    public ResponseEntity<?> deletar(@RequestHeader String usuarioId) {
        service.deletar(usuarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/anonimo")
    public ResponseEntity<String> anonimo() {

        log.debug("anonimo");
        String uuid = UUID.randomUUID().toString();
        String token = tokenService.generateToken(
                uuid, true
        );
        this.publisher.publish(new AnonimoEvent(uuid));

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
