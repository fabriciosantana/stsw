package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.domain.Person;
import br.edu.idp.es.stsw.pyramid.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GreetingApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
        personRepository.save(new Person("Ham", "Vocke"));
    }

    @Test
    void shouldReturnHelloWorldFromHttpEndpoint() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello World!"));
    }

    @Test
    void shouldReturnGreetingByLastNameFromHttpEndpoint() throws Exception {
        mockMvc.perform(get("/hello/Vocke"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello Ham Vocke!"));
    }

    @Test
    void shouldReturnNotFoundWhenPersonDoesNotExist() throws Exception {
        mockMvc.perform(get("/hello/Unknown"))
                .andExpect(status().isNotFound());
    }
}
