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
import org.models.UserRole;
import org.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;

@ApplicationScoped
public class AuthService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private StrictJwtBuilderFabric jwtBuilderFabric;
    @Inject @ConfigProperty(name = "auth.user-invite-code")
    private String userInviteCode;
    @Inject @ConfigProperty(name = "auth.admin-invite-code")
    private String adminInviteCode;

    @Transactional
    public String login(final String login, final String password) throws UnauthorizedException {
        final User user = userRepository.findByUsername(login)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return jwtBuilderFabric.newTokenBuilder(user.getUsername())
                .addClaim("uid", user.getId())
                .addClaim("role", user.getRole().name())
                .build();
    }

    @Transactional
    public String register(
            final String login,
            final String password,
            final String inviteCode
    ) throws BadRequestException, ConflictException {
        UserRole role = resolveRole(inviteCode);
        if (userRepository.findByUsername(login).isPresent()) {
            throw new ConflictException("User already exists");
        }

        final String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        final User user = userRepository.save(
                User.builder()
                        .username(login)
                        .password(hashedPassword)
                        .role(role)
                        .build()
        );

        return jwtBuilderFabric.newTokenBuilder(user.getUsername())
                .addClaim("uid", user.getId())
                .addClaim("role", user.getRole().name())
                .build();
    }

    private UserRole resolveRole(String inviteCode) {
        if (adminInviteCode.equals(inviteCode)) {
            return UserRole.ADMIN;
        }
        if (userInviteCode.equals(inviteCode)) {
            return UserRole.USER;
        }

        throw new BadRequestException("Invalid invite code");
    }
}
