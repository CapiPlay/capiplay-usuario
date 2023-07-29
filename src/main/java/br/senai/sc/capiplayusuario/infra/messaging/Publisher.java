package br.senai.sc.capiplayusuario.infra.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Publisher {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "usuario-service";

    public void publish(Object event) {
        var routingKey = event.getClass().getSimpleName(); // UsuarioSalvoEvent
        rabbitTemplate.convertAndSend(EXCHANGE, routingKey, event);
    }
}