package com.zrio.pos.auth;

import com.zrio.pos.user.User;
import com.zrio.pos.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SessionAuthenticationStrategy
            sessionAuthenticationStrategy;
    private final UserRepository userRepository;

    private final SecurityContextHolderStrategy
            securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    public AuthController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            SessionAuthenticationStrategy sessionAuthenticationStrategy,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.sessionAuthenticationStrategy =
                sessionAuthenticationStrategy;
        this.userRepository = userRepository;
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken csrfToken) {

        return new CsrfResponse(
                csrfToken.getHeaderName(),
                csrfToken.getToken()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        try {
            Authentication authenticationRequest =
                    UsernamePasswordAuthenticationToken
                            .unauthenticated(
                                    loginRequest.username().trim(),
                                    loginRequest.password()
                            );

            Authentication authentication =
                    authenticationManager.authenticate(
                            authenticationRequest
                    );

            sessionAuthenticationStrategy.onAuthentication(
                    authentication,
                    request,
                    response
            );

            SecurityContext context =
                    securityContextHolderStrategy
                            .createEmptyContext();

            context.setAuthentication(authentication);

            securityContextHolderStrategy.setContext(context);

            securityContextRepository.saveContext(
                    context,
                    request,
                    response
            );

            User user = findUser(authentication.getName());

            return ResponseEntity.ok(toResponse(user));

        } catch (AuthenticationException exception) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            new AuthErrorResponse(
                                    "Invalid username or password"
                            )
                    );
        }
    }

    @GetMapping("/me")
    public AuthUserResponse currentUser(
            Authentication authentication
    ) {

        User user = findUser(authentication.getName());

        return toResponse(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        Authentication authentication =
                securityContextHolderStrategy
                        .getContext()
                        .getAuthentication();

        SecurityContextLogoutHandler logoutHandler =
                new SecurityContextLogoutHandler();

        logoutHandler.logout(
                request,
                response,
                authentication
        );

        return ResponseEntity.noContent().build();
    }

    private User findUser(String username) {

        return userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow();
    }

    private AuthUserResponse toResponse(User user) {

        return new AuthUserResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name()
        );
    }
}