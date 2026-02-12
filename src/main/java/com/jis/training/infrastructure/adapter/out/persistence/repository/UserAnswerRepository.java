package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.UserAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAnswerRepository extends JpaRepository<UserAnswerEntity, Long> {
    List<UserAnswerEntity> findByUsuarioId(Long usuarioId);
    List<UserAnswerEntity> findByQuestionId(Long questionId);
}
