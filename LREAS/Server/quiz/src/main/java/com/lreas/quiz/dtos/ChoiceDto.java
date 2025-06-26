package com.lreas.quiz.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.lreas.quiz.models.Answer;

@JsonTypeName("d7207d27-36c8-432d-b6f6-28c175ff944e")
public class ChoiceDto extends AbstractChoiceDto {
    public ChoiceDto() {}

    public ChoiceDto(Answer answer) {
        this.choiceId = answer.getId();
        this.answer = answer.getText();
        this.correct = answer.getIsCorrect();
        this.createdTime = answer.getDateCreated();
        this.updatedTime = answer.getDateUpdated();
    }
}
