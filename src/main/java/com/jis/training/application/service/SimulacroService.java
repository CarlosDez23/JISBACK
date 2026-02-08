package com.jis.training.application.service;

import com.jis.training.domain.model.Question;
import com.jis.training.domain.model.Simulacro;
import com.jis.training.domain.model.SimulacroPregunta;
import com.jis.training.domain.port.out.PersistencePort;
import com.jis.training.infrastructure.adapter.in.rest.dto.SimulacroResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SimulacroService extends GenericCrudService<Simulacro, Long> {

    private final SimulacroPreguntaService simulacroPreguntaService;
    private final QuestionService questionService;

    public SimulacroService(
            @Qualifier("simulacroPersistenceAdapter") PersistencePort<Simulacro, Long> persistencePort,
            SimulacroPreguntaService simulacroPreguntaService,
            @Lazy QuestionService questionService) {
        super(persistencePort);
        this.simulacroPreguntaService = simulacroPreguntaService;
        this.questionService = questionService;
    }

    public List<SimulacroResponse> getAllWithPreguntaCount() {
        return getAll().stream()
                .map(simulacro -> new SimulacroResponse(
                        simulacro.getId(),
                        simulacro.getNombreSimulacro(),
                        simulacro.getComunidad(),
                        simulacro.getMateria(),
                        simulacroPreguntaService.countBySimulacroId(simulacro.getId())
                ))
                .toList();
    }

    @Transactional
    public Simulacro createWithPreguntas(Simulacro simulacro, List<Long> preguntaIds) {
        Simulacro savedSimulacro = create(simulacro);

        for (Long preguntaId : preguntaIds) {
            SimulacroPregunta sp = new SimulacroPregunta();
            sp.setSimulacro(savedSimulacro);

            Question pregunta = new Question();
            pregunta.setId(preguntaId);
            sp.setPregunta(pregunta);

            simulacroPreguntaService.create(sp);
        }

        return savedSimulacro;
    }

    @Transactional
    public void deleteWithPreguntas(Long id) {
        simulacroPreguntaService.deleteBySimulacroId(id);
        delete(id);
    }

    @Transactional
    public Simulacro generateSimulacro(Simulacro simulacro, Map<Long, Integer> preguntasPorTema) {
        List<Long> preguntaIds = questionService.selectRandomQuestionIds(preguntasPorTema);
        return createWithPreguntas(simulacro, preguntaIds);
    }
}
