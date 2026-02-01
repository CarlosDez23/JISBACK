package com.jis.training.domain.port.out;

import com.jis.training.domain.model.Topic;

import java.util.List;

public interface TopicRepositoryPort {
    List<Topic> findByMateriaId(Long materiaId);
}
