package com.perficient.etm.domain.util;

import com.perficient.etm.web.view.View;

/**
 * Custom Jackson serializer for serializing nested objects with only the id field.
 * Ignores fields annotated with @JsonView that is not View.Identity
 * 
 * @see View
 */
public class IdentitySerializer extends AbstractViewSerializer {

    @Override
    protected Class<?> getView() {
        return View.Identity.class;
    }
}
