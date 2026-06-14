package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.controller.GreetingController;
import br.edu.idp.es.stsw.pyramid.service.GreetingService;
import br.edu.idp.es.stsw.pyramid.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GreetingControllerUnitTest {

    @Mock
    private GreetingService greetingService;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private GreetingController greetingController;

    @Test
    void shouldReturnHelloWorld() {
        when(greetingService.helloWorld()).thenReturn("Hello World!");

        ResponseEntity<String> response = greetingController.helloWorld();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello World!");
    }

    @Test
    void shouldReturnHelloByLastNameWhenFound() {
        when(greetingService.helloByLastName("Vocke")).thenReturn(Optional.of("Hello Ham Vocke!"));

        ResponseEntity<String> response = greetingController.helloByLastName("Vocke");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello Ham Vocke!");
    }

    @Test
    void shouldReturnNotFoundWhenLastNameIsUnknown() {
        when(greetingService.helloByLastName("Unknown")).thenReturn(Optional.empty());

        ResponseEntity<String> response = greetingController.helloByLastName("Unknown");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void shouldReturnWeather() {
        when(weatherService.currentWeatherInHamburg())
                .thenReturn("Current weather in Hamburg: 20.0°C, clear sky.");

        ResponseEntity<String> response = greetingController.weather();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Current weather in Hamburg: 20.0°C, clear sky.");
    }
}
