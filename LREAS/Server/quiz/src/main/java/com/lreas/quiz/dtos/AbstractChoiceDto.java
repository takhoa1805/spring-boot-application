package com.lreas.quiz.dtos;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = ChoiceDto.class)
public abstract class AbstractChoiceDto {
    public String choiceId;
    public String answer;
    public Boolean correct;
    public Date createdTime;
    public Date updatedTime;

    public void copyFrom(AbstractChoiceDto dto) {
        this.choiceId = dto.choiceId;
        this.answer = dto.answer;
        this.correct = dto.correct;
        this.createdTime = dto.createdTime;
        this.updatedTime = dto.updatedTime;
    }

    private boolean compareDates(
            Date d1, Date d2
    ) {
        if (d1 == null && d2 == null) {
            return true;
        }
        else if (d1 == null || d2 == null) {
            return false;
        }
        return d1.getTime() == d2.getTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractChoiceDto other = (AbstractChoiceDto) o;
        return (
            Objects.equals(this.choiceId, other.choiceId) &&
            Objects.equals(this.answer, other.answer) &&
            Objects.equals(this.correct, other.correct) &&
            this.compareDates(this.createdTime, other.createdTime) &&
            this.compareDates(this.updatedTime, other.updatedTime)
        );
    }
}
