package com.lreas.generator.services;

import com.lreas.generator.dtos.*;
import com.lreas.generator.dtos.QuizDtos.*;

import com.lreas.grpc.GrpcQuizServiceOuterClass.QuizResourcesGrpc;
import com.lreas.grpc.GrpcQuizServiceOuterClass.QuestionGrpc;
import com.lreas.grpc.GrpcQuizServiceOuterClass.ChoiceGrpc;
import com.lreas.grpc.GrpcQuizServiceOuterClass.CreateQuizGrpc;
import com.lreas.grpc.GrpcQuizServiceOuterClass.Collaborator;

import lombok.Setter;
import org.springframework.stereotype.Service;

import com.lreas.grpc.GrpcQuizServiceGrpc;

import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.google.protobuf.Empty;

@Setter
@Service
public class GrpcQuizServiceGrpcClient {
    @GrpcClient("grpc_quiz_service")
    private GrpcQuizServiceGrpc.GrpcQuizServiceBlockingStub grpcQuizServiceStub;

    public QuizResourcesDto createQuiz(
            GenerateFromFileDto generateFromFileDto
    ) {
        CreateQuizGrpc.Builder createQuizGrpcBuilder = CreateQuizGrpc.newBuilder();
        Optional.ofNullable(generateFromFileDto.newResourceName).ifPresent(createQuizGrpcBuilder::setContentName);

        for (GenerateFromFileDto.Collaborator collaborator : generateFromFileDto.collaborators) {
            Collaborator.Builder collabBuilder = Collaborator.newBuilder();

            Optional.ofNullable(collaborator.id).ifPresent(collabBuilder::setId);
            Optional.ofNullable(collaborator.role).ifPresent(r -> collabBuilder.setRole(collaborator.role.toString()));

            createQuizGrpcBuilder.addCollaborators(collabBuilder.build());
        }

        Optional.ofNullable(generateFromFileDto.outputFolderId).ifPresent(createQuizGrpcBuilder::setParentResourceId);
        Optional.ofNullable(generateFromFileDto.userId).ifPresent(createQuizGrpcBuilder::setUserId);

        QuizResourcesGrpc response = this.grpcQuizServiceStub.createQuiz(createQuizGrpcBuilder.build());

        return this.convertToQuizResourcesDto(response);
    }

    public void updateQuiz(
            QuizResourcesDto quizResourcesDto
    ) {
        // convert data
        QuizResourcesGrpc quizResourcesGrpc = this.convertToQuizResourcesGrpc(quizResourcesDto);

        // pass to service
        Empty response = this.grpcQuizServiceStub.updateQuiz(quizResourcesGrpc);
    }

    private QuizResourcesDto convertToQuizResourcesDto(
            QuizResourcesGrpc quizResourcesGrpc
    ) {
        QuizResourcesDto quizResourcesDto = new QuizResourcesDto();
        quizResourcesDto.resourceId = quizResourcesGrpc.getResourceId().isEmpty() ? null : quizResourcesGrpc.getResourceId();
        quizResourcesDto.quizId = quizResourcesGrpc.getQuizId().isEmpty() ? null : quizResourcesGrpc.getQuizId();
        quizResourcesDto.isGame = quizResourcesGrpc.getIsGame();
        quizResourcesDto.startTime = quizResourcesGrpc.getStartTime().isEmpty() ? null : new Date(Long.parseLong(quizResourcesGrpc.getStartTime()));
        quizResourcesDto.endTime = quizResourcesGrpc.getEndTime().isEmpty() ? null : new Date(Long.parseLong(quizResourcesGrpc.getEndTime()));
        quizResourcesDto.maxPlayers = quizResourcesGrpc.getMaxPlayer();
        quizResourcesDto.description = quizResourcesGrpc.getDescription().isEmpty() ? null : quizResourcesGrpc.getDescription();
        quizResourcesDto.userId = quizResourcesGrpc.getUserId().isEmpty() ? null : quizResourcesGrpc.getUserId();
        quizResourcesDto.parentResourceId = quizResourcesGrpc.getParentResourceId().isEmpty() ? null : quizResourcesGrpc.getParentResourceId();
        quizResourcesDto.name = quizResourcesGrpc.getName();
        quizResourcesDto.showCorrectAnswer = quizResourcesGrpc.getShowCorrectAnswer();
        quizResourcesDto.allowedAttempts = quizResourcesGrpc.getAllowedAttempts();
        quizResourcesDto.shuffleAnswers = quizResourcesGrpc.getShuffleAnswers();
        quizResourcesDto.totalTime = quizResourcesGrpc.getTotalTime();
        quizResourcesDto.totalPoints = quizResourcesGrpc.getTotalPoints();
        quizResourcesDto.createdTime = quizResourcesGrpc.getCreatedTime().isEmpty() ? null : new Date(Long.parseLong(quizResourcesGrpc.getCreatedTime()));
        quizResourcesDto.updatedTime = quizResourcesGrpc.getUpdatedTime().isEmpty() ? null : new Date(Long.parseLong(quizResourcesGrpc.getUpdatedTime()));
        quizResourcesDto.questions = new LinkedList<>();

        // loop through each question
        for (QuestionGrpc questionGrpc : quizResourcesGrpc.getQuestionsList()) {
            QuestionDto questionDto = new QuestionDto();
            questionDto.questionId = questionGrpc.getQuestionId().isEmpty() ? null : questionGrpc.getQuestionId();
            questionDto.question = questionGrpc.getQuestion().isEmpty() ? null : questionGrpc.getQuestion();
            questionDto.time = questionGrpc.getTime();
            questionDto.points = questionGrpc.getPoints();
            questionDto.position = questionGrpc.getPosition();
            questionDto.imageObjectName = questionGrpc.getImageObjectName().isEmpty() ? null : questionGrpc.getImageObjectName();
            questionDto.imageUrl = questionGrpc.getImageUrl().isEmpty() ? null : questionGrpc.getImageUrl();
            questionDto.createdTime = questionGrpc.getCreatedTime().isEmpty() ? null : new Date(Long.parseLong(questionGrpc.getCreatedTime()));
            questionDto.updatedTime = questionGrpc.getUpdatedTime().isEmpty() ? null : new Date(Long.parseLong(questionGrpc.getUpdatedTime()));
            questionDto.imageMetadata = questionGrpc.getImageMetadataMap();
            questionDto.choices = new LinkedList<>();

            // loop through each answer
            for (ChoiceGrpc choiceGrpc : questionGrpc.getChoicesList()) {
                ChoiceDto choiceDto = new ChoiceDto();
                choiceDto.choiceId = choiceGrpc.getChoiceId().isEmpty() ? null : choiceGrpc.getChoiceId();
                choiceDto.answer = choiceGrpc.getAnswer().isEmpty() ? null : choiceGrpc.getAnswer();
                choiceDto.correct = choiceGrpc.getCorrect();
                choiceDto.createdTime = choiceGrpc.getCreatedTime().isEmpty() ? null : new Date(Long.parseLong(choiceGrpc.getCreatedTime()));
                choiceDto.updatedTime = choiceGrpc.getUpdatedTime().isEmpty() ? null : new Date(Long.parseLong(choiceGrpc.getUpdatedTime()));

                questionDto.choices.add(choiceDto);
            }

            quizResourcesDto.questions.add(questionDto);
        }

        return quizResourcesDto;
    }

    private QuizResourcesGrpc convertToQuizResourcesGrpc(
            QuizResourcesDto quizResourcesDto
    ) {
        List<QuestionGrpc> questionGrpcList = new LinkedList<>();
        for (QuestionDto question : quizResourcesDto.questions) {
            // loop through each choice
            List<ChoiceGrpc> choiceGrpcList = new LinkedList<>();
            for (ChoiceDto choice : question.choices) {
                ChoiceGrpc.Builder choiceGrpcBuilder = ChoiceGrpc.newBuilder();
                Optional.ofNullable(choice.choiceId).ifPresent(choiceGrpcBuilder::setChoiceId);
                Optional.ofNullable(choice.answer).ifPresent(choiceGrpcBuilder::setAnswer);
                Optional.ofNullable(choice.correct).ifPresent(choiceGrpcBuilder::setCorrect);
                Optional.ofNullable(choice.createdTime).ifPresent(t -> choiceGrpcBuilder.setCreatedTime(String.valueOf(choice.createdTime.getTime())));
                Optional.ofNullable(choice.updatedTime).ifPresent(t -> choiceGrpcBuilder.setUpdatedTime(String.valueOf(choice.updatedTime.getTime())));

                ChoiceGrpc choiceGrpc = choiceGrpcBuilder.build();
                choiceGrpcList.add(choiceGrpc);
            }

            QuestionGrpc.Builder questionGrpcBuilder = QuestionGrpc.newBuilder();
            Optional.ofNullable(question.questionId).ifPresent(questionGrpcBuilder::setQuestionId);
            Optional.ofNullable(question.question).ifPresent(questionGrpcBuilder::setQuestion);
            Optional.ofNullable(question.time).ifPresent(questionGrpcBuilder::setTime);
            Optional.ofNullable(question.points).ifPresent(questionGrpcBuilder::setPoints);
            Optional.ofNullable(question.position).ifPresent(questionGrpcBuilder::setPosition);
            Optional.ofNullable(question.imageObjectName).ifPresent(questionGrpcBuilder::setImageObjectName);
            Optional.ofNullable(question.imageUrl).ifPresent(questionGrpcBuilder::setImageUrl);
            Optional.ofNullable(question.createdTime).ifPresent(t -> questionGrpcBuilder.setCreatedTime(String.valueOf(question.createdTime.getTime())));
            Optional.ofNullable(question.updatedTime).ifPresent(t -> questionGrpcBuilder.setUpdatedTime(String.valueOf(question.updatedTime.getTime())));
            Optional.ofNullable(question.imageMetadata).ifPresent(questionGrpcBuilder::putAllImageMetadata);
            Optional.of(choiceGrpcList).ifPresent(questionGrpcBuilder::addAllChoices);

            QuestionGrpc questionGrpc = questionGrpcBuilder.build();
            questionGrpcList.add(questionGrpc);
        }

        QuizResourcesGrpc.Builder quizResourcesGrpcBuilder = QuizResourcesGrpc.newBuilder();
        Optional.ofNullable(quizResourcesDto.resourceId).ifPresent(quizResourcesGrpcBuilder::setResourceId);
        Optional.ofNullable(quizResourcesDto.quizId).ifPresent(quizResourcesGrpcBuilder::setQuizId);
        Optional.ofNullable(quizResourcesDto.isGame).ifPresent(quizResourcesGrpcBuilder::setIsGame);
        Optional.ofNullable(quizResourcesDto.startTime).ifPresent(t -> quizResourcesGrpcBuilder.setStartTime(String.valueOf(quizResourcesDto.startTime.getTime())));
        Optional.ofNullable(quizResourcesDto.endTime).ifPresent(t -> quizResourcesGrpcBuilder.setEndTime(String.valueOf(quizResourcesDto.endTime.getTime())));
        Optional.ofNullable(quizResourcesDto.maxPlayers).ifPresent(quizResourcesGrpcBuilder::setMaxPlayer);
        Optional.ofNullable(quizResourcesDto.description).ifPresent(quizResourcesGrpcBuilder::setDescription);
        Optional.ofNullable(quizResourcesDto.userId).ifPresent(quizResourcesGrpcBuilder::setUserId);
        Optional.ofNullable(quizResourcesDto.parentResourceId).ifPresent(quizResourcesGrpcBuilder::setParentResourceId);
        Optional.ofNullable(quizResourcesDto.name).ifPresent(quizResourcesGrpcBuilder::setName);
        Optional.ofNullable(quizResourcesDto.showCorrectAnswer).ifPresent(quizResourcesGrpcBuilder::setShowCorrectAnswer);
        Optional.ofNullable(quizResourcesDto.allowedAttempts).ifPresent(quizResourcesGrpcBuilder::setAllowedAttempts);
        Optional.ofNullable(quizResourcesDto.shuffleAnswers).ifPresent(quizResourcesGrpcBuilder::setShuffleAnswers);
        Optional.ofNullable(quizResourcesDto.totalTime).ifPresent(quizResourcesGrpcBuilder::setTotalTime);
        Optional.ofNullable(quizResourcesDto.totalPoints).ifPresent(quizResourcesGrpcBuilder::setTotalPoints);
        Optional.ofNullable(quizResourcesDto.createdTime).ifPresent(t -> quizResourcesGrpcBuilder.setCreatedTime(String.valueOf(quizResourcesDto.createdTime.getTime())));
        Optional.ofNullable(quizResourcesDto.updatedTime).ifPresent(t -> quizResourcesGrpcBuilder.setUpdatedTime(String.valueOf(quizResourcesDto.updatedTime.getTime())));
        Optional.of(questionGrpcList).ifPresent(quizResourcesGrpcBuilder::addAllQuestions);

        return quizResourcesGrpcBuilder.build();
    }
}
