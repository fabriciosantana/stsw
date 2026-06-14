package br.edu.idp.es.stsw.pyramid.service;

import br.edu.idp.es.stsw.pyramid.client.ExternalClient;
import br.edu.idp.es.stsw.pyramid.domain.TipResponse;
import br.edu.idp.es.stsw.pyramid.repository.StudyTopicRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudyService {

    private final StudyTopicRepository studyTopicRepository;
    private final ExternalClient externalClient;

    public StudyService(StudyTopicRepository studyTopicRepository, ExternalClient externalClient) {
        this.studyTopicRepository = studyTopicRepository;
        this.externalClient = externalClient;
    }

    public String status() {
        return "Assignment 05 online";
    }

    public Optional<String> findTopicSummary(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        String normalizedName = name.trim();

        return studyTopicRepository.findByNameIgnoreCase(normalizedName)
                .map(topic -> "Topic %s: %s".formatted(topic.getName(), topic.getDescription()));
    }

    public String dailyTip() {
        TipResponse tipResponse = externalClient.fetchTip();
        return "Daily tip: %s".formatted(tipResponse.message());
    }
}
