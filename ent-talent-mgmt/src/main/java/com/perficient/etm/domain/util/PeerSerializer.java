package com.perficient.etm.domain.util;

import com.perficient.etm.web.view.View;

/**
 * Custom Jackson serializer for serializing nested objects with only the publicly available info.
 * Ignores fields annotated with @JsonView that is not View.Public
 * 
 * @see View
 */
public class PeerSerializer extends AbstractViewSerializer {

    @Override
    protected Class<?> getView() {
        return View.Peer.class;
    }
}
