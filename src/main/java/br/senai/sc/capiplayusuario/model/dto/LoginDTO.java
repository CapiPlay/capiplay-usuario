package br.senai.sc.capiplayusuario.model.dto;

import br.senai.sc.capiplayusuario.utils.validacao.senha.SenhaForte;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDTO(@Email String email,@NotBlank String senha) {
}
