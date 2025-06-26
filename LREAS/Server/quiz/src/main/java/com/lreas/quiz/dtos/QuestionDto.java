package com.lreas.quiz.dtos;

import com.lreas.quiz.models.Answer;
import com.lreas.quiz.models.Question;
import com.lreas.quiz.utils.QuizUtils;

import java.util.*;

public class QuestionDto {
    public String questionId;
    public String question;
    public Double time;
    public Double points;
    public Integer position;
    public String imageObjectName;
    public String imageUrl;
    public Date createdTime;
    public Date updatedTime;
    public Map<String, String> imageMetadata;
    public List<AbstractChoiceDto> choices;

    public QuestionDto() {}

    public QuestionDto(
            Question question,
            QuizUtils quizUtils,
            Boolean isShuffle
    ) {
        this.init(question, quizUtils, isShuffle);
    }

    private void init(
            Question question,
            QuizUtils quizUtils,
            Boolean isShuffle
    ) {
        if (isShuffle == null) {
            isShuffle = false;
        }

        this.questionId = question.getId();
        this.question = question.getTitle();
        this.time = question.getTimeLimit();
        this.points = question.getScore();
        this.position = question.getPosition();
        this.imageObjectName = question.getImage();
        this.createdTime = question.getDateCreated();
        this.updatedTime = question.getDateUpdated();

        if (question.getImage() != null) {
            try {
                this.imageUrl = quizUtils.getMinioClientUtils().getUrl(question.getImage());
            }
            catch (Exception e) {
                this.imageUrl = null;
            }

            try {
                this.imageMetadata = quizUtils.getMinioClientUtils().getMetadata(question.getImage());
            }
            catch (Exception e) {
                this.imageMetadata = Collections.emptyMap();
            }
        }

        List<Answer> answersLst = quizUtils.getAnswers(question);
        this.choices = new LinkedList<>();
        for (Answer answer : answersLst) {
            this.choices.add(new ChoiceDto(answer));
        }

        // shuffle the answers
        if (isShuffle) {
            Collections.shuffle(this.choices);
        }
        else {
            // sort by created date
            this.choices.sort(Comparator.comparing(c -> c.createdTime));
        }
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

        QuestionDto other = (QuestionDto) o;

        // check choice list
        boolean isChoicesEqual = false;
        if (this.choices != null) {
            if (other.choices != null) {
                if (this.choices.size() == other.choices.size()) {
                    this.choices.sort(Comparator.comparing(q -> q.choiceId));
                    other.choices.sort(Comparator.comparing(q -> q.choiceId));
                    isChoicesEqual = this.choices.equals(other.choices);
                }
            }
        }
        else {
            isChoicesEqual = (other.choices == null);
        }

        return (
            Objects.equals(this.questionId, other.questionId) &&
            Objects.equals(this.question, other.question) &&
            Objects.equals(this.time, other.time) &&
            Objects.equals(this.points, other.points) &&
            Objects.equals(this.position, other.position) &&
            Objects.equals(this.imageObjectName, other.imageObjectName) &&
            this.compareDates(this.createdTime, other.createdTime) &&
            this.compareDates(this.updatedTime, other.updatedTime) &&
            isChoicesEqual
        );
    }
}
