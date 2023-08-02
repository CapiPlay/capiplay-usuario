package br.senai.sc.capiplayusuario.security;

import br.senai.sc.capiplayusuario.model.entity.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

@Service
public class TokenService {

    @Value("${secret.key}")
    private String secret;

    public String generateToken(Usuario usuario){
        try {
            Algorithm algorithm = HMAC256(secret);
            return JWT.create().withIssuer("capiplay")
                    .withSubject(usuario.getNome())
                    .withExpiresAt(genExpirationDate())
                    .withClaim("usuarioId", usuario.getUuid())
                    .sign(algorithm);
        } catch (JWTCreationException a) {
            throw new RuntimeException("Erro ao gerar token");
        }

    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
