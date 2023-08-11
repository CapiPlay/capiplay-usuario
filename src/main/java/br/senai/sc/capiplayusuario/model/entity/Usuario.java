package br.senai.sc.capiplayusuario.model.entity;

import br.senai.sc.capiplayusuario.usuario.projections.DetalhesUsuarioProjection;
import br.senai.sc.capiplayusuario.usuario.projections.UsuarioComentarioProjection;
import br.senai.sc.capiplayusuario.utils.GeradorUuidUtils;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.util.Collection;

@Entity
@Data
public class Usuario implements UsuarioComentarioProjection, DetalhesUsuarioProjection, UserDetails {

    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(length = 50, nullable = false)
    private String nome;

    private String perfil; // Apelido

    @Column(nullable = false)
    private String senha;

    @Column(length = 60, nullable = false)
    private String email;

    private String foto;

    @Column(nullable = false)
    private Date dataNascimento;
    private boolean isEnabled;

    public Usuario() {
        this.uuid = GeradorUuidUtils.gerarUuid();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.perfil;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
