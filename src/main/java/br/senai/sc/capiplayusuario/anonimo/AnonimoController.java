package br.senai.sc.capiplayusuario.anonimo;

import br.senai.sc.capiplayusuario.security.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class AnonimoController {

    private final TokenService tokenService;

    @PostMapping("/anonimo")
    public ResponseEntity<String> anonimo() {
        return ResponseEntity.status(HttpStatus.OK).body(
                tokenService.generateToken(
                        UUID.randomUUID().toString(), true
                )
        );
    }
}
