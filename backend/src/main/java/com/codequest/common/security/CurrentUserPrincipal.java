package com.codequest.common.security;

import java.security.Principal;
import java.util.UUID;

public record CurrentUserPrincipal(UUID userId, String email, String role) implements Principal {

    @Override
    public String getName() {
        return userId.toString();
    }
}
