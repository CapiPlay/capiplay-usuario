package br.senai.sc.capiplayusuario.infra.messaging;

import br.senai.sc.capiplayusuario.usuario.events.UsuarioSalvoEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    private final AmqpAdmin amqpAdmin;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("usuario-service");
    }

    private static final Queue USUARIO_SALVO_ENGAJAMENTO = new Queue("usuarios.v1.usuario-salvo.engajamento");
    private static final Queue USUARIO_SALVO_VIDEO = new Queue("usuarios.v1.usuario-salvo.video");

    private static final HashMap<String, Queue> map = new HashMap<>(){
        {
            put(USUARIO_SALVO_VIDEO.getName(), USUARIO_SALVO_VIDEO);
            put(USUARIO_SALVO_ENGAJAMENTO.getName(), USUARIO_SALVO_ENGAJAMENTO);
        }
    };

    @PostConstruct
    public void postConstruct() {
        var queues = map.values();
        queues.forEach(amqpAdmin::declareQueue);
        amqpAdmin.declareBinding(bind(USUARIO_SALVO_ENGAJAMENTO, UsuarioSalvoEvent.class));
        amqpAdmin.declareBinding(bind(USUARIO_SALVO_VIDEO, UsuarioSalvoEvent.class));
    }

    private Binding bind(Queue queue, Class clazz) {
        return BindingBuilder.bind(queue).to(exchange()).with(clazz.getSimpleName());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}