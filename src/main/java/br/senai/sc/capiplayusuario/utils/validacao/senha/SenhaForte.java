package br.senai.sc.capiplayusuario.utils.validacao.senha;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SenhaForteValidador.class)
@Documented
public @interface SenhaForte {

    String message() default "Senha fraca";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
