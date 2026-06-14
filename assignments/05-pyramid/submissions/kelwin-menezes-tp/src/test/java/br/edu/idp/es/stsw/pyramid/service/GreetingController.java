package br.edu.idp.es.stsw.pyramid.controller;

import br.edu.idp.es.stsw.pyramid.service.GreetingService;
import br.edu.idp.es.stsw.pyramid.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class GreetingController {
    private final GreetingService greetingService;
    private final WeatherService weatherService;

    public GreetingController(GreetingService greetingService, WeatherService weatherService) {
        this.greetingService = greetingService;
        this.weatherService = weatherService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok(greetingService.helloWorld());
    }

    @GetMapping("/hello/{lastname}")
    public ResponseEntity<String> helloByLastName(@PathVariable String lastname) {
        return greetingService.helloByLastName(lastname)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/weather")
    public ResponseEntity<String> weather() {
        return ResponseEntity.ok(weatherService.currentWeatherInHamburg());
    }
}