package br.edu.idp.es.stsw.pyramid.repository;

import br.edu.idp.es.stsw.pyramid.domain.StudyTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyTopicRepository extends JpaRepository<StudyTopic, Long> {

    Optional<StudyTopic> findByNameIgnoreCase(String name);
}
