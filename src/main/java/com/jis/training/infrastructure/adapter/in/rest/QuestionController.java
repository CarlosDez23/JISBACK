package com.jis.training.infrastructure.adapter.in.rest;

import com.jis.training.application.service.QuestionService;
import com.jis.training.domain.model.Answer;
import com.jis.training.domain.model.Question;
import com.jis.training.domain.model.QuestionWithAnswers;
import com.jis.training.domain.model.Topic;
import com.jis.training.infrastructure.adapter.in.rest.dto.CreateQuestionRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.GenerateQuizRequest;
import com.jis.training.infrastructure.adapter.in.rest.dto.GenerateQuizResponse;
import com.jis.training.infrastructure.adapter.in.rest.mapper.QuestionDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@Tag(name = "Question", description = "API de Preguntas")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService service;
    private final QuestionDtoMapper questionDtoMapper;

    @GetMapping
    @Operation(summary = "Listar todas las preguntas")
    public List<Question> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pregunta por ID con sus respuestas")
    public ResponseEntity<QuestionWithAnswers> getById(@PathVariable Long id) {
        return service.getByIdWithAnswers(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear pregunta con respuestas")
    public Question create(@Valid @RequestBody CreateQuestionRequest request) {
        Question question = new Question();
        question.setQuestionText(request.questionText());

        Topic topic = new Topic();
        topic.setId(request.topic().id());
        question.setTopic(topic);

        List<Answer> answers = request.answers().stream()
                .map(answerRequest -> {
                    Answer answer = new Answer();
                    answer.setAnswerText(answerRequest.answerText());
                    answer.setCorrect(answerRequest.isCorrect());
                    answer.setExplanation(answerRequest.explanation());
                    return answer;
                })
                .toList();

        return service.createWithAnswers(question, answers);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pregunta")
    public Question update(@PathVariable Long id, @RequestBody Question question) {
        return service.update(id, question);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pregunta")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/generate-quiz")
    @Operation(summary = "Generar quiz de preguntas", description = "Genera un quiz con preguntas distribuidas equitativamente entre los topics especificados")
    public ResponseEntity<GenerateQuizResponse> generateQuiz(@Valid @RequestBody GenerateQuizRequest request) {
        List<QuestionWithAnswers> questions = service.generateQuiz(
                request.topicIds(),
                request.numberOfQuestions()
        );

        GenerateQuizResponse response = new GenerateQuizResponse(
                questions.size(),
                request.topicIds().size(),
                questionDtoMapper.toQuestionWithAnswersResponseList(questions)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-topic/{topicId}")
    @Operation(summary = "Obtener preguntas por tema", description = "Devuelve todas las preguntas que pertenecen a un topic")
    public List<Question> getByTopicId(@PathVariable Long topicId) {
        return service.findByTopicId(topicId);
    }

    @GetMapping("/by-materia/{materiaId}")
    @Operation(summary = "Obtener preguntas por materia", description = "Devuelve todas las preguntas de todos los topics de una materia")
    public List<Question> getByMateriaId(@PathVariable Long materiaId) {
        return service.findByMateriaId(materiaId);
    }
}
