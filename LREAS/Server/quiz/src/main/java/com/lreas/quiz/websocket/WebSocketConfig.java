package com.lreas.quiz.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final QuizWebSocketHandler quizWebSocketHandler;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    @Autowired
    public WebSocketConfig(
            QuizWebSocketHandler quizWebSocketHandler,
            AuthHandshakeInterceptor authHandshakeInterceptor
    ) {
        this.quizWebSocketHandler = quizWebSocketHandler;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(
            WebSocketHandlerRegistry registry
    ) {
        registry.addHandler(
                quizWebSocketHandler,
                "/quiz-edit-collab"
        ).addInterceptors(
                authHandshakeInterceptor
        ).setAllowedOrigins("*").withSockJS();

        registry.addHandler(
                quizWebSocketHandler,
                "/quiz-edit-collab"
        ).addInterceptors(
                authHandshakeInterceptor
        ).setAllowedOrigins("*");
    }
}
