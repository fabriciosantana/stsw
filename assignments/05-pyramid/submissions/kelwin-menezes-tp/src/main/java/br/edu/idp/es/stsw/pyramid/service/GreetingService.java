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
        return personRepository.findByLastNameIgnoreCase(lastName.trim())
                .map(p -> "Hello %s %s!".formatted(p.getFirstName(), p.getLastName()));
    }
}