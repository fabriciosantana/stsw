package br.edu.idp.es.stsw.pyramid.integration;

import br.edu.idp.es.stsw.pyramid.domain.Person;
import br.edu.idp.es.stsw.pyramid.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PersonRepositoryIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void shouldFindByLastNameIgnoringCase() {
        Person saved = personRepository.save(new Person("Ham", "Vocke"));

        Optional<Person> result = personRepository.findByLastNameIgnoreCase("vocke");

        assertThat(saved.getId()).isNotNull();
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Ham");
        assertThat(result.get().getLastName()).isEqualTo("Vocke");
    }

    @Test
    void shouldReturnEmptyWhenLastNameDoesNotExist() {
        Optional<Person> result = personRepository.findByLastNameIgnoreCase("Nobody");

        assertThat(result).isEmpty();
    }
}
