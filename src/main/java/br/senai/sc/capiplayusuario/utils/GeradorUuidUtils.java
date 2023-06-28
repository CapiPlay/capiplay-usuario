package br.senai.sc.capiplayusuario.utils;

import java.util.UUID;

public class GeradorUuidUtils {

    private GeradorUuidUtils(){}

    public static String gerarUuid(){
        return UUID.randomUUID().toString();
    }
}
