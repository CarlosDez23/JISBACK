package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    List<AnswerEntity> findByQuestionIdIn(List<Long> questionIds);
    List<AnswerEntity> findByQuestionId(Long questionId);

    @Modifying
    @Query("DELETE FROM AnswerEntity a WHERE a.question.id IN :questionIds")
    void deleteByQuestionIdIn(@Param("questionIds") List<Long> questionIds);
}
