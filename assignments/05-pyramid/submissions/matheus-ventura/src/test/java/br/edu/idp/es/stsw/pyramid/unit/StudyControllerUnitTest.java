package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.controller.StudyController;
import br.edu.idp.es.stsw.pyramid.service.StudyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyControllerUnitTest {

    @Mock
    private StudyService studyService;

    @InjectMocks
    private StudyController studyController;

    @Test
    void shouldReturnStatusMessage() {
        when(studyService.status()).thenReturn("Assignment 05 online");

        ResponseEntity<String> response = studyController.status();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Assignment 05 online");
    }

    @Test
    void shouldReturnTopicSummaryWhenTopicExists() {
        when(studyService.findTopicSummary("JUnit"))
                .thenReturn(Optional.of("Topic JUnit: Framework for automated tests"));

        ResponseEntity<String> response = studyController.topicByName("JUnit");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Topic JUnit: Framework for automated tests");
    }

    @Test
    void shouldReturnNotFoundWhenTopicDoesNotExist() {
        when(studyService.findTopicSummary("Unknown")).thenReturn(Optional.empty());

        ResponseEntity<String> response = studyController.topicByName("Unknown");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldReturnDailyTip() {
        when(studyService.dailyTip()).thenReturn("Daily tip: Keep your tests small and focused.");

        ResponseEntity<String> response = studyController.dailyTip();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Daily tip: Keep your tests small and focused.");
    }
}
