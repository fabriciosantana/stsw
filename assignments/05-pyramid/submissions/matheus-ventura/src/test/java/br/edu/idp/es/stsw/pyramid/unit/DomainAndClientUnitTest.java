package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.domain.StudyTopic;
import br.edu.idp.es.stsw.pyramid.domain.TipResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class DomainAndClientUnitTest {

    @Test
    void shouldCreateStudyTopicWithConstructorData() {
        StudyTopic studyTopic = new StudyTopic("Spring", "Framework used in the project");

        assertThat(studyTopic.getId()).isNull();
        assertThat(studyTopic.getName()).isEqualTo("Spring");
        assertThat(studyTopic.getDescription()).isEqualTo("Framework used in the project");
    }

    @Test
    void shouldInstantiateProtectedNoArgsConstructor() throws Exception {
        Constructor<StudyTopic> constructor = StudyTopic.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        StudyTopic studyTopic = constructor.newInstance();

        assertThat(studyTopic.getId()).isNull();
        assertThat(studyTopic.getName()).isNull();
        assertThat(studyTopic.getDescription()).isNull();
    }

    @Test
    void shouldExposeTipResponseMessage() {
        TipResponse response = new TipResponse("Write tests before delivery.");

        assertThat(response.message()).isEqualTo("Write tests before delivery.");
    }
}
