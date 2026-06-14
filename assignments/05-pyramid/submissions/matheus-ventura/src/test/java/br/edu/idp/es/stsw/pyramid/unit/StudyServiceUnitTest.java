package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.client.ExternalClient;
import br.edu.idp.es.stsw.pyramid.domain.StudyTopic;
import br.edu.idp.es.stsw.pyramid.domain.TipResponse;
import br.edu.idp.es.stsw.pyramid.repository.StudyTopicRepository;
import br.edu.idp.es.stsw.pyramid.service.StudyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyServiceUnitTest {

    @Mock
    private StudyTopicRepository studyTopicRepository;

    @Mock
    private ExternalClient externalClient;

    @InjectMocks
    private StudyService studyService;

    @Test
    void shouldReturnApplicationStatus() {
        assertThat(studyService.status()).isEqualTo("Assignment 05 online");
    }

    @Test
    void shouldReturnTopicSummaryWhenTopicExists() {
        when(studyTopicRepository.findByNameIgnoreCase("JUnit"))
                .thenReturn(Optional.of(new StudyTopic("JUnit", "Framework for automated tests")));

        Optional<String> result = studyService.findTopicSummary("JUnit");

        assertThat(result).contains("Topic JUnit: Framework for automated tests");
    }

    @Test
    void shouldTrimTopicNameBeforeLookup() {
        when(studyTopicRepository.findByNameIgnoreCase("JUnit"))
                .thenReturn(Optional.of(new StudyTopic("JUnit", "Framework for automated tests")));

        studyService.findTopicSummary("  JUnit ");

        verify(studyTopicRepository).findByNameIgnoreCase("JUnit");
    }

    @Test
    void shouldReturnEmptyWhenTopicDoesNotExist() {
        when(studyTopicRepository.findByNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

        Optional<String> result = studyService.findTopicSummary("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenTopicNameIsBlank() {
        Optional<String> result = studyService.findTopicSummary("   ");

        assertThat(result).isEmpty();
        verifyNoInteractions(studyTopicRepository);
    }

    @Test
    void shouldReturnEmptyWhenTopicNameIsNull() {
        Optional<String> result = studyService.findTopicSummary(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(studyTopicRepository);
    }

    @Test
    void shouldReturnFormattedDailyTip() {
        when(externalClient.fetchTip()).thenReturn(new TipResponse("Keep your tests small and focused."));

        String result = studyService.dailyTip();

        assertThat(result).isEqualTo("Daily tip: Keep your tests small and focused.");
        verify(externalClient).fetchTip();
    }
}
