package com.jis.training.infrastructure.adapter.in.rest.mapper;

import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.QuestionWithAnswers;
import com.jis.training.infrastructure.adapter.in.rest.dto.AnswerResponse;
import com.jis.training.infrastructure.adapter.in.rest.dto.QuestionWithAnswersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionDtoMapper {

    @Mapping(target = "isCorrect", source = "correct")
    AnswerResponse toAnswerResponse(Answer answer);

    List<AnswerResponse> toAnswerResponseList(List<Answer> answers);

    QuestionWithAnswersResponse toQuestionWithAnswersResponse(QuestionWithAnswers question);

    List<QuestionWithAnswersResponse> toQuestionWithAnswersResponseList(List<QuestionWithAnswers> questions);
}
