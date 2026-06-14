package br.edu.idp.es.stsw.pyramid.client;

import br.edu.idp.es.stsw.pyramid.domain.TipResponse;

public interface ExternalClient {

    TipResponse fetchTip();
}
