package br.senai.sc.capiplayusuario.utils.validacao.senha;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SenhaForteValidador implements ConstraintValidator<SenhaForte, String> {
    @Override
    public void initialize(SenhaForte constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        boolean containsDigit = value.matches(".*\\d.*");

        boolean containsSpecialChar = value.matches(".*[!@#$%^&*()\\-_=+\\\\|\\[\\]{};:'\",<.>/?].*");

        boolean containsLowercase = value.matches(".*[a-z].*");

        boolean containsUppercase = value.matches(".*[A-Z].*");

        return containsDigit && containsSpecialChar && containsLowercase && containsUppercase;
    }
}
