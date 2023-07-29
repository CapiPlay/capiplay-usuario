package br.senai.sc.capiplayusuario.infra.messaging;

import br.senai.sc.capiplayusuario.usuario.events.UsuarioSalvoEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {

    private final ConnectionFactory connectionFactory;

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("usuario-service");
    }

    @Bean
    Queue usuarioSalvoEngajamentoQueue() {
        return new Queue("usuarios.v1.usuario-salvo.engajamento");
    }

    @Bean
    Queue usuarioSalvoVideoQueue() {
        return new Queue("usuarios.v1.usuario-salvo.video");
    }

    @Bean
    Binding bindingUsuarioSalvoEngajamento(Queue usuarioSalvoEngajamentoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(usuarioSalvoEngajamentoQueue).to(exchange).with(UsuarioSalvoEvent.class.getSimpleName());
    }

    @Bean
    Binding bindingUsuarioSalvoVideo(Queue usuarioSalvoVideoQueue, TopicExchange exchange) {
        return BindingBuilder.bind(usuarioSalvoVideoQueue).to(exchange).with("UsuarioSalvoEvent");
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
