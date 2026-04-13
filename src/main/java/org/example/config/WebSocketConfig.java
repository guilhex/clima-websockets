package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Define o endpoint que o cliente usa para abrir a conexão WebSocket.
     * withSockJS() habilita o fallback para navegadores que não suportam WS nativo.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-clima")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * Configura o message broker interno do Spring:
     * - /topic  → destinos de broadcast (servidor → todos os clientes inscritos)
     * - /app    → prefixo para mensagens roteadas a @MessageMapping (cliente → servidor)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}