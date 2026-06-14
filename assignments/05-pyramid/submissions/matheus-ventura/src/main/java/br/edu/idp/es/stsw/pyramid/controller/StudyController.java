package br.edu.idp.es.stsw.pyramid.controller;

import br.edu.idp.es.stsw.pyramid.service.StudyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudyController {

    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok(studyService.status());
    }

    @GetMapping("/topics/{name}")
    public ResponseEntity<String> topicByName(@PathVariable String name) {
        return studyService.findTopicSummary(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tip")
    public ResponseEntity<String> dailyTip() {
        return ResponseEntity.ok(studyService.dailyTip());
    }
}
