package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.domain.StudyTopic;
import br.edu.idp.es.stsw.pyramid.repository.StudyTopicRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudyTopicRepositoryIntegrationTest {

    @Autowired
    private StudyTopicRepository studyTopicRepository;

    @Test
    void shouldFindTopicByNameIgnoringCase() {
        StudyTopic saved = studyTopicRepository.save(new StudyTopic("Mockito", "Library used to mock dependencies"));

        Optional<StudyTopic> result = studyTopicRepository.findByNameIgnoreCase("mockito");

        assertThat(saved.getId()).isNotNull();
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Mockito");
        assertThat(result.get().getDescription()).isEqualTo("Library used to mock dependencies");
    }

    @Test
    void shouldReturnEmptyWhenTopicDoesNotExist() {
        Optional<StudyTopic> result = studyTopicRepository.findByNameIgnoreCase("Unknown");

        assertThat(result).isEmpty();
    }
}
