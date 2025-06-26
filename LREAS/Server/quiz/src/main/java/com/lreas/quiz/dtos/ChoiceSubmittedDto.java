package com.lreas.quiz.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Objects;

@JsonTypeName("4d82355d-210b-484f-bd4f-7d8ecdb827af")
public class ChoiceSubmittedDto extends AbstractChoiceDto {
    public Boolean submittedCorrect;

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        else {
            ChoiceSubmittedDto other = (ChoiceSubmittedDto) o;
            return Objects.equals(this.submittedCorrect, other.submittedCorrect);
        }
    }
}
