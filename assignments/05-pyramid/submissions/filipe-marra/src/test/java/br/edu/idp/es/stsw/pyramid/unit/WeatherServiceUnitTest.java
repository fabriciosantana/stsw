package br.edu.idp.es.stsw.pyramid.unit;

import br.edu.idp.es.stsw.pyramid.client.WeatherClient;
import br.edu.idp.es.stsw.pyramid.domain.WeatherDetails;
import br.edu.idp.es.stsw.pyramid.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceUnitTest {

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void shouldFormatWeatherSummary() {
        when(weatherClient.fetchForCity("Hamburg,de"))
                .thenReturn(new WeatherDetails(18.39, "clear sky"));

        String result = weatherService.currentWeatherInHamburg();

        assertThat(result).isEqualTo("Current weather in Hamburg: 18.4°C, clear sky.");
        verify(weatherClient).fetchForCity("Hamburg,de");
    }
}
