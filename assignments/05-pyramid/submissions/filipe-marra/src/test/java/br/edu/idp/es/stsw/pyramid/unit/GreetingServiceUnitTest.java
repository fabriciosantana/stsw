package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.domain.Person;
import br.edu.idp.es.stsw.pyramid.repository.PersonRepository;
import br.edu.idp.es.stsw.pyramid.service.GreetingService;
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
class GreetingServiceUnitTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private GreetingService greetingService;

    @Test
    void shouldReturnHelloWorld() {
        assertThat(greetingService.helloWorld()).isEqualTo("Hello World!");
    }

    @Test
    void shouldReturnGreetingWhenPersonExists() {
        when(personRepository.findByLastNameIgnoreCase("Vocke"))
                .thenReturn(Optional.of(new Person("Ham", "Vocke")));

        Optional<String> result = greetingService.helloByLastName("Vocke");

        assertThat(result).contains("Hello Ham Vocke!");
    }

    @Test
    void shouldTrimInputBeforeLookup() {
        when(personRepository.findByLastNameIgnoreCase("Vocke"))
                .thenReturn(Optional.of(new Person("Ham", "Vocke")));

        greetingService.helloByLastName("  Vocke ");

        verify(personRepository).findByLastNameIgnoreCase("Vocke");
    }

    @Test
    void shouldReturnEmptyWhenPersonDoesNotExist() {
        when(personRepository.findByLastNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

        Optional<String> result = greetingService.helloByLastName("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenInputIsBlank() {
        Optional<String> result = greetingService.helloByLastName("   ");

        assertThat(result).isEmpty();
        verifyNoInteractions(personRepository);
    }

    @Test
    void shouldReturnEmptyWhenInputIsNull() {
        Optional<String> result = greetingService.helloByLastName(null);

        assertThat(result).isEmpty();
        verifyNoInteractions(personRepository);
    }
}
