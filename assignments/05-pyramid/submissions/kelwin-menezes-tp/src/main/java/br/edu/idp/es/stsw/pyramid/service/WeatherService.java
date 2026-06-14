package br.edu.idp.es.stsw.pyramid.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {
    private final RestTemplate restTemplate = new RestTemplate();

    public String currentWeatherInHamburg() {
        return restTemplate.getForObject("http://localhost:8081/hamburg-weather", String.class);
    }
}