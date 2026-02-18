package com.jis.training.infrastructure.adapter.out.persistence.repository;

import com.jis.training.infrastructure.adapter.out.persistence.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByTopicIdIn(List<Long> topicIds);
    List<QuestionEntity> findByTopicId(Long topicId);
    long countByTopicId(Long topicId);

    @Modifying
    @Query("DELETE FROM QuestionEntity q WHERE q.topic.id = :topicId")
    void deleteByTopicId(@Param("topicId") Long topicId);
}
