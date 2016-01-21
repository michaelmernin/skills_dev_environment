package com.perficient.etm.authorize;

import java.util.Optional;
import java.util.function.Predicate;

import com.perficient.etm.domain.User;
import com.perficient.etm.security.SecurityUtils;

public class Authorizer {

    protected static final String TYPE_START = "T(com.perficient.etm.authorize.";

    protected static final String TYPE_END = ")";

    protected static final String AUTHORIZE_METHOD = ".authorize(returnObject)";

    protected static final String FILTER_METHOD = ".filter(filterObject)";

    private static final String SYSTEM_USERNAME = "system";

    protected static Optional<String> getLogin() {
        return Optional.ofNullable(SecurityUtils.getCurrentLogin());
    }
    
    protected static boolean loginIs(Optional<User> user, String username) {
        return user.map(User::getLogin).filter(loginIs(username)).isPresent();
    }

    protected static Predicate<String> loginIs(String username) {
        return login -> login.equals(username);
    }
    
    protected static boolean isSystem(String login) {
        return loginIs(SYSTEM_USERNAME).test(login);
    }
}
