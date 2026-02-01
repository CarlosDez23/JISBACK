package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByTopicIdIn(List<Long> topicIds);
    List<QuestionEntity> findByTopicId(Long topicId);
    long countByTopicId(Long topicId);
}
