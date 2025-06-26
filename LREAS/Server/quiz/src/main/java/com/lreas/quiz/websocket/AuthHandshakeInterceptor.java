package com.lreas.quiz.websocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes
    ) {
        String uri = request.getURI().toString();

        if (uri.contains("token=")) {
            String token = uri.substring(uri.indexOf("token=") + 6);
            attributes.put("token", token);
            return true;
        }

        // reject connection
        return false;
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @Nullable Exception exception
    ) {}
}
