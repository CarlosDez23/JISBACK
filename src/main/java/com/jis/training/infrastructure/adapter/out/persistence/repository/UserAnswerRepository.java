package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.UserAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswerEntity, Long> {
    List<UserAnswerEntity> findByUsuarioId(Long usuarioId);
    List<UserAnswerEntity> findByQuestionId(Long questionId);

    @Modifying
    @Query("DELETE FROM UserAnswerEntity ua WHERE ua.question.id IN :questionIds")
    void deleteByQuestionIdIn(@Param("questionIds") List<Long> questionIds);
}
