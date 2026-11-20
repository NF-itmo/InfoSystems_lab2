package org.controllers;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.controllers.DTO.auth.LoginRequest;
import org.controllers.DTO.auth.RegisterRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jwtProcessing.filter.JwtSecured;
import org.services.AuthService;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthController {
    @Inject
    private AuthService authService;

    @Inject @ConfigProperty(name = "auth.jwt-cookie.name")
    private String jwtCookieName;

    @Inject @ConfigProperty(name = "auth.jwt-cookie.max-age")
    private int jwtCookieMaxAge;

    @Inject @ConfigProperty(name = "auth.jwt-cookie.isSecure")
    private boolean jwtCookieIsSecure;

    @POST
    @Path("/register")
    public Response register(
            @Valid RegisterRequest request
    ) {
        String token = authService.register(
                request.login(),
                request.password(),
                request.inviteCode()
        );

        return Response.noContent()
                .cookie(createCookie(token))
                .build();
    }

    @POST
    @Path("/login")
    public Response login(
            @Valid LoginRequest request
    ) {
        String token = authService.login(request.login(), request.password());

        return Response.noContent()
                .cookie(createCookie(token))
                .build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        return Response.noContent()
                .cookie(removeCookie())
                .build();
    }

    @POST
    @Path("/validate")
    @JwtSecured
    public Response validate() {
        return Response.noContent().build();
    }

    private NewCookie createCookie(String token) {
        return new NewCookie.Builder(jwtCookieName)
                .value("Bearer " + token)
                .maxAge(jwtCookieMaxAge)
                .httpOnly(true)
                .secure(jwtCookieIsSecure)
                .sameSite(NewCookie.SameSite.STRICT)
                .path("/")
                .build();
    }

    public NewCookie removeCookie() {
        return new NewCookie.Builder(jwtCookieName)
                .value("")
                .maxAge(0)
                .httpOnly(true)
                .secure(jwtCookieIsSecure)
                .sameSite(NewCookie.SameSite.STRICT)
                .path("/")
                .build();
    }
}
