package br.edu.idp.es.stsw.pyramid.service;

import br.edu.idp.es.stsw.pyramid.client.WeatherClient;
import br.edu.idp.es.stsw.pyramid.domain.WeatherDetails;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class WeatherService {

    private static final String DEFAULT_CITY = "Hamburg,de";
    private final WeatherClient weatherClient;

    public WeatherService(WeatherClient weatherClient) {
        this.weatherClient = weatherClient;
    }

    public String currentWeatherInHamburg() {
        WeatherDetails details = weatherClient.fetchForCity(DEFAULT_CITY);
        return String.format(Locale.US, "Current weather in Hamburg: %.1f°C, %s.",
                details.temperatureCelsius(),
                details.description()
        );
    }
}
