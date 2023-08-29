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
    Queue anonimoSalvoEngajamentoQueue() {
        return new Queue("usuarios.v1.anonimo-salvo.engajamento");
    }

    @Bean
    Queue anonimoSalvoVideoQueue() {
        return new Queue("usuarios.v1.anonimo-salvo.video");
    }

    @Bean
    Queue usuarioSalvoVideoQueue() {
        return new Queue("usuarios.v1.usuario-salvo.video");
    }

    @Bean
    Binding bindingUsuarioSalvoEngajamento() {
        return BindingBuilder.bind(usuarioSalvoEngajamentoQueue()).to(exchange()).with("UsuarioSalvoEvent");
    }

    @Bean
    Binding bindingUsuarioSalvoVideo() {
        return BindingBuilder.bind(usuarioSalvoVideoQueue()).to(exchange()).with("UsuarioSalvoEvent");
    }

    @Bean
    Binding bindingAnonimoSalvoVideo() {
        return BindingBuilder.bind(anonimoSalvoVideoQueue()).to(exchange()).with("AnonimoEvent");
    }

    @Bean
    Binding bindingAnonimoSalvoEngajamento() {
        return BindingBuilder.bind(anonimoSalvoEngajamentoQueue()).to(exchange()).with("AnonimoEvent");
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
