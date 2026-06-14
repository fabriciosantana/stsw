package br.edu.idp.es.stsw.pyramid.client;

import br.edu.idp.es.stsw.pyramid.domain.WeatherDetails;

public interface WeatherClient {
    WeatherDetails fetchForCity(String city);
}
