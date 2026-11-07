package org.jwtProcessing.security;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.security.Principal;

public record JwtPrincipal(DecodedJWT decodedToken) implements Principal {
    @Override
    public String getName() {
        return decodedToken.getSubject();
    }
    public int getUid() {
        return decodedToken.getClaim("uid").asInt();
    }
}