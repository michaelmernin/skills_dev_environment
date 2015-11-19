package com.perficient.etm.authorize;

import java.util.Optional;

import com.perficient.etm.security.SecurityUtils;

public class Authorizer {

    protected static final String TYPE_START = "T(com.perficient.etm.authorize.";

    protected static final String TYPE_END = ")";

    protected static final String AUTHORIZE_METHOD = ".authorize(returnObject)";

    protected static final String FILTER_METHOD = ".filter(filterObject)";

    protected static Optional<String> getLogin() {
        return Optional.ofNullable(SecurityUtils.getCurrentLogin());
    }

}
