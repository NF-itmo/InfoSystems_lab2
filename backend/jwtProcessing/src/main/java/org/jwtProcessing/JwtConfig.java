package org.jwtProcessing;

import com.auth0.jwt.algorithms.Algorithm;


public final class JwtConfig {
    private static final String SECRET = System.getenv().getOrDefault(
            "JWT_SECRET",
            "your-super-secret-key-change-in-production"
    );
    public static final String ISSUER = System.getenv().getOrDefault(
            "JWT_ISSUER",
            "your-app-name"
    );

    public static final Algorithm ALGORITHM;

    public static final int JWT_EXPIRATION_SECONDS = Integer.parseInt(
            System.getenv().getOrDefault("JWT_EXPIRATION_SECONDS", "3600")
    );

    static {
        try {
            ALGORITHM = Algorithm.HMAC256(SECRET);
        } catch (IllegalArgumentException e) {
            throw new ExceptionInInitializerError("Failed to initialize JWT algorithm: " + e.getMessage());
        }
    }

    private JwtConfig() {}
}
