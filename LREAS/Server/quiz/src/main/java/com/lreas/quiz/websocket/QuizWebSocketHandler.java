package com.lreas.quiz.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.lreas.quiz.dtos.CollaboratorDto;
import com.lreas.quiz.dtos.QuizResourcesDto;
import com.lreas.quiz.dtos.UserInfoDto;
import com.lreas.quiz.services.GrpcProfileServiceGrpcClient;
import com.lreas.quiz.services.QuizService;

import com.lreas.quiz.utils.JwtUtils;
import org.jetbrains.annotations.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.*;

@Component
public class QuizWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(QuizWebSocketHandler.class);

    private static final Long TIMEOUT = 60000L;

    private final ObjectMapper objectMapper;
    private final QuizService quizService;
    private final GrpcProfileServiceGrpcClient grpcProfileServiceGrpcClient;
    private final JwtUtils jwtUtils;

    private static final Map<String, Set<WebSocketSession>> resourceIdToSessionMap = new ConcurrentHashMap<>();
    private static final Map<String, String> sessionIdToResourceIdMap = new ConcurrentHashMap<>();
    private static final Map<WebSocketSession, Long> lastPongTimeMap = new ConcurrentHashMap<>();
    private static final Set<CollaboratorDto> collaboratorDtoSet = Collections.synchronizedSet(new HashSet<>());
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static final Map<String, StringBuilder> messageBuffer = new ConcurrentHashMap<>();

    public QuizWebSocketHandler(
            QuizService quizService,
            GrpcProfileServiceGrpcClient grpcProfileServiceGrpcClient,
            JwtUtils jwtUtils
    ) {
        this.objectMapper = new ObjectMapper();
        this.quizService = quizService;
        this.grpcProfileServiceGrpcClient = grpcProfileServiceGrpcClient;
        this.jwtUtils = jwtUtils;

        try {
            executor.scheduleAtFixedRate(() -> {
                long now = System.currentTimeMillis();
                List<WebSocketSession> removingSessions = new LinkedList<>();

                for (Map.Entry<WebSocketSession, Long> entry : lastPongTimeMap.entrySet()) {
                    Long lastSeen = entry.getValue();
                    WebSocketSession session = entry.getKey();

                    if (now - lastSeen > TIMEOUT) {
                        try {
                            logger.debug("No Response From {}. Closing...", session.getId());

                            this.removeSession(session.getId());
                            this.removeCollaborator(session.getId());
                            removingSessions.add(session);
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                    else {
                        try {
                            if (session.isOpen()) {
                                session.sendMessage(new TextMessage(
                                    objectMapper.writeValueAsString(
                                        new QuizUpdatedMessage(QuizUpdatedMessage.Type.PING, null)
                                    )
                                ));
                            }
                        }
                        catch (Exception e) {
                            logger.error(e.getMessage());
                        }
                    }
                }

                // remove sessions
                for (WebSocketSession removingSession : removingSessions) {
                    lastPongTimeMap.remove(removingSession);
                }
            }, 10, 30, TimeUnit.SECONDS);

            Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
        }
        catch (Exception e) {
            logger.error("{}: {}", Thread.currentThread().getName(), e.getMessage());
        }
    }

    private void removeSession(String sessionId) {
        if (sessionIdToResourceIdMap.containsKey(sessionId)) {
            Set<WebSocketSession> sessions = resourceIdToSessionMap.get(
                    sessionIdToResourceIdMap.get(sessionId)
            );
            WebSocketSession removingSession = null;

            for (WebSocketSession s : sessions) {
                if (s.getId().equals(sessionId)) {
                    try {
                        logger.debug("Removing Session: {}", sessionId);
                        s.close();
                        removingSession = s;
                        break;
                    }
                    catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            }

            // remove session
            if (removingSession != null) {
                sessions.remove(removingSession);
            }

            sessionIdToResourceIdMap.remove(sessionId);
        }
    }

    private void removeCollaborator(String sessionId) {
        CollaboratorDto removingCollaboratorDto = null;

        for (CollaboratorDto collaboratorDto : collaboratorDtoSet) {
            if (collaboratorDto.sessionId.equals(sessionId)) {
                logger.debug("Removing Collaborator: {}. Old Size: {}", collaboratorDto.userId, collaboratorDtoSet.size());
                removingCollaboratorDto = collaboratorDto;
                break;
            }
        }

        // remove collaborator dto
        if (removingCollaboratorDto != null) {
            collaboratorDtoSet.remove(removingCollaboratorDto);
        }
    }

    private void removeLastPongTime(String sessionId) {
        WebSocketSession removingLastPongTime = null;
        for (WebSocketSession s : lastPongTimeMap.keySet()) {
            if (s.getId().equals(sessionId)) {
                removingLastPongTime = s;
                break;
            }
        }

        // remove item
        if (removingLastPongTime != null) {
            lastPongTimeMap.remove(removingLastPongTime);
        }
    }

    private void setActiveTimeForCollab(
            String sessionId, Date newActiveTime
    ) {
        for (CollaboratorDto collaboratorDto : collaboratorDtoSet) {
            if (collaboratorDto.sessionId.equals(sessionId)) {
                synchronized (collaboratorDto.mutex) {
                    if (collaboratorDto.lastActive.before(newActiveTime)) {
                        collaboratorDto.lastActive = newActiveTime;
                    }
                }
                break;
            }
        }
    }

    private String getCurrentSessionByUser(
            String userId, String sessionId
    ) {
        for (CollaboratorDto collaboratorDto : collaboratorDtoSet) {
            if (collaboratorDto.userId.equals(userId) && !collaboratorDto.sessionId.equals(sessionId)) {
                return collaboratorDto.sessionId;
            }
        }
        return null;
    }

    @Override
    public void afterConnectionClosed(
            @NotNull WebSocketSession session,
            @NotNull CloseStatus status
    ) {
        this.removeCollaborator(session.getId());
        this.removeSession(session.getId());
        logger.debug(status.toString());
    }

    @Override
    public void afterConnectionEstablished(
            @NotNull WebSocketSession session
    ) {
        // get user id
        String userId = jwtUtils.extractUserId(
            (String) session.getAttributes().get("token")
        );

        // check previous attached session of user
        String removingSessionId = this.getCurrentSessionByUser(userId, session.getId());
        if (removingSessionId != null) {
            this.removeLastPongTime(removingSessionId);
            this.removeCollaborator(removingSessionId);
            this.removeSession(removingSessionId);
        }
        lastPongTimeMap.put(session, System.currentTimeMillis());

        // get collaborator info
        UserInfoDto userInfoDto = this.grpcProfileServiceGrpcClient.getUserInfo(userId);

        logger.debug("User {} Connected", userId);
        collaboratorDtoSet.add(new CollaboratorDto(userId, session.getId(), userInfoDto));
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    @Override
    public void handleTextMessage(
            @NotNull WebSocketSession session,
            @NotNull TextMessage message
    ) {
        try {
            StringBuilder builder = messageBuffer.computeIfAbsent(
                    session.getId(), s -> new StringBuilder()
            );
            builder.append(message.getPayload());

            // check if this message is last or not
            if (!message.isLast()) {
                return;
            }

            // get user id
            String userId = jwtUtils.extractUserId(
                    (String) session.getAttributes().get("token")
            );

            // update pong time
            lastPongTimeMap.put(session, System.currentTimeMillis());

            // update active time
            this.setActiveTimeForCollab(session.getId(), new Date());

            String data = builder.toString();

            // map from json string to object
            QuizUpdatedMessage updatedMessage = objectMapper.readValue(data, QuizUpdatedMessage.class);
            if (updatedMessage == null) {
                return;
            }
            QuizResourcesDto updatedQuiz = updatedMessage.updatedQuiz;

            if (updatedMessage.type.equals(QuizUpdatedMessage.Type.REGISTER)) {
                // check previous attached session of user
                String removingSessionId = this.getCurrentSessionByUser(userId, session.getId());
                if (removingSessionId != null) {
                    this.removeLastPongTime(removingSessionId);
                    this.removeCollaborator(removingSessionId);
                    this.removeSession(removingSessionId);
                }

                // add to existed session
                if (resourceIdToSessionMap.containsKey(updatedQuiz.resourceId)) {
                    resourceIdToSessionMap.get(updatedQuiz.resourceId).add(session);
                }
                else {
                    Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>() {{
                        add(session);
                    }});

                    resourceIdToSessionMap.put(updatedQuiz.resourceId, sessions);
                    sessionIdToResourceIdMap.put(session.getId(), updatedQuiz.resourceId);
                }

                // send client some information
                session.sendMessage(new TextMessage(
                    objectMapper.writeValueAsString(
                        new QuizUpdatedMessage(
                            QuizUpdatedMessage.Type.INFO,
                            collaboratorDtoSet,
                            null
                        )
                    )
                ));

                logger.debug("Registered Session: {}. Resource Id: {}", session.getId(), updatedQuiz.resourceId);
            }

            // update quiz
            if (updatedMessage.type.equals(QuizUpdatedMessage.Type.UPDATED)) {
                // save updated quiz
                QuizResourcesDto updatedQuizSaved = this.quizService.updateQuiz(updatedQuiz);

                // broadcast all
                this.broadCast(session, updatedQuizSaved);
            }

            // remove buffer
            messageBuffer.remove(session.getId());
        }
        catch (Exception e) {
            logger.error("{}: {}", "Quiz WebSocket Handler", e.getMessage());
        }
    }

    private void broadCast(
            WebSocketSession currentSession,
            QuizResourcesDto updatedQuiz
    ) {
        try {
            // get all sessions relating to this quiz editing session
            Set<WebSocketSession> sessions = resourceIdToSessionMap.get(updatedQuiz.resourceId);
            if (sessions != null) {
                TextMessage data = new TextMessage(
                        objectMapper.writeValueAsString(
                                new QuizUpdatedMessage(QuizUpdatedMessage.Type.UPDATED, collaboratorDtoSet, updatedQuiz)
                        )
                );

                synchronized (sessions) {
                    logger.debug("Session {} Is BroadCasting... Size: {}", currentSession.getId(), sessions.size());
                    for (WebSocketSession session : sessions) {
                        logger.debug("Checking Session {}", session.getId());

                        if (session.isOpen()) {
                            logger.debug("Sending Message To Session {}", session.getId());
                            session.sendMessage(data);
                        }
                        else {
                            this.removeCollaborator(session.getId());
                            this.removeSession(session.getId());
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error("{}: {}", "Quiz WebSocket Broadcast", e.getMessage());
        }
    }

    private static class QuizUpdatedMessage {
        public Type type;
        public Set<CollaboratorDto> collaborators;
        public QuizResourcesDto updatedQuiz;

        public QuizUpdatedMessage() {}

        public QuizUpdatedMessage(
                Type type,
                QuizResourcesDto updatedQuiz
        ) {
            this.type = type;
            this.collaborators = Collections.emptySet();
            this.updatedQuiz = updatedQuiz;
        }

        public QuizUpdatedMessage(
                Type type,
                Set<CollaboratorDto> collaborators,
                QuizResourcesDto updatedQuiz
        ) {
            this.type = type;
            this.collaborators = collaborators;
            this.updatedQuiz = updatedQuiz;
        }

        public enum Type {
            UPDATED, REGISTER, PING, PONG, INFO
        }
    }
}
