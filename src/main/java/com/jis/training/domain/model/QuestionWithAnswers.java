package com.jis.training.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionWithAnswers {
    private Long id;
    private String questionText;
    private Long topicId;
    private String topicName;
    private List<Answer> answers;
}
