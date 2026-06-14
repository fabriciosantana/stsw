package br.edu.idp.es.stsw.pyramid.repository;

import br.edu.idp.es.stsw.pyramid.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByLastNameIgnoreCase(String lastName);
}
