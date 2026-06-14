package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.client.ExternalClient;
import br.edu.idp.es.stsw.pyramid.domain.StudyTopic;
import br.edu.idp.es.stsw.pyramid.domain.TipResponse;
import br.edu.idp.es.stsw.pyramid.repository.StudyTopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudyApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudyTopicRepository studyTopicRepository;

    @MockBean
    private ExternalClient externalClient;

    @BeforeEach
    void setUp() {
        studyTopicRepository.deleteAll();
        studyTopicRepository.save(new StudyTopic("JUnit", "Framework for automated tests"));
    }

    @Test
    void shouldReturnStatusFromHttpEndpoint() throws Exception {
        mockMvc.perform(get("/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Assignment 05 online"));
    }

    @Test
    void shouldReturnTopicSummaryFromHttpEndpoint() throws Exception {
        mockMvc.perform(get("/topics/JUnit"))
                .andExpect(status().isOk())
                .andExpect(content().string("Topic JUnit: Framework for automated tests"));
    }

    @Test
    void shouldReturnNotFoundWhenTopicDoesNotExist() throws Exception {
        mockMvc.perform(get("/topics/Unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnDailyTipFromHttpEndpoint() throws Exception {
        when(externalClient.fetchTip()).thenReturn(new TipResponse("Review both happy path and error path."));

        mockMvc.perform(get("/tip"))
                .andExpect(status().isOk())
                .andExpect(content().string("Daily tip: Review both happy path and error path."));
    }
}
