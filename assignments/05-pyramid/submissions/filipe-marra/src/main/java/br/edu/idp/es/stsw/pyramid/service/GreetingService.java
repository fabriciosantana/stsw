package br.edu.idp.es.stsw.pyramid.service;

import br.edu.idp.es.stsw.pyramid.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GreetingService {

    private final PersonRepository personRepository;

    public GreetingService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public String helloWorld() {
        return "Hello World!";
    }

    public Optional<String> helloByLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            return Optional.empty();
        }

        String normalizedLastName = lastName.trim();

        return personRepository.findByLastNameIgnoreCase(normalizedLastName)
                .map(person -> "Hello %s %s!".formatted(person.getFirstName(), person.getLastName()));
    }
}
