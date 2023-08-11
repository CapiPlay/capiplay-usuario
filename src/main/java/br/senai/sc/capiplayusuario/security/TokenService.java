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
import java.util.UUID;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

@Service
public class TokenService {

    @Value("${secret.key}")
    private String secret;

    public String generateToken(String id) {
        return generateToken(id, false);
    }

    public String generateToken(String id, boolean anonimo) {

        try {
            Algorithm algorithm = HMAC256(secret);

            var jwt = JWT.create().withIssuer("capiplay")
                    .withExpiresAt(genExpirationDate())
                    .withClaim("usuarioId", id);
            if (anonimo)
                jwt.withClaim("anonimo", true);

            return jwt.sign(algorithm);
        } catch (JWTCreationException a) {
            throw new RuntimeException("Erro ao gerar token");
        }
    }

    private Instant genExpirationDate() {
        return LocalDateTime.now().plusDays(7).toInstant(ZoneOffset.of("-03:00"));
    }
}
