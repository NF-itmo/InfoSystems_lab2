package org.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.exceptions.BadRequestException;
import org.exceptions.ConflictException;
import org.exceptions.UnauthorizedException;
import org.jwtProcessing.StrictJwtBuilderFabric;
import org.models.User;
import org.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;

@ApplicationScoped
public class AuthService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private StrictJwtBuilderFabric jwtBuilderFabric;
    @Inject @ConfigProperty(name = "auth.invite-code")
    private String inviteCode;

    @Transactional
    public String login(final String login, final String password) throws UnauthorizedException {
        final User user = userRepository.findByUsername(login)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return jwtBuilderFabric.newTokenBuilder(user.getUsername())
                .addClaim("uid", user.getId())
                .build();
    }

    @Transactional
    public String register(
            final String login,
            final String password,
            final String inviteCode
    ) throws BadRequestException, ConflictException {
        if (!this.inviteCode.equals(inviteCode)) {
            throw new BadRequestException("Invalid invite code");
        }
        if (userRepository.findByUsername(login).isPresent()) {
            throw new ConflictException("User already exists");
        }

        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        final User user = userRepository.save(
                User.builder()
                        .username(login)
                        .password(hashedPassword)
                        .build()
        );

        return jwtBuilderFabric.newTokenBuilder(user.getUsername())
                .addClaim("uid", user.getId())
                .build();
    }
}
