package org.example.besmarthelpdesk.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.besmarthelpdesk.dto.request.LoginRequest;
import org.example.besmarthelpdesk.dto.request.RegisterRequest;
import org.example.besmarthelpdesk.dto.response.MemberResponse;
import org.example.besmarthelpdesk.dto.response.TokenResponse;
import org.example.besmarthelpdesk.facade.AuthFacade;
import org.example.besmarthelpdesk.security.JwtTokenProvider;
import org.example.besmarthelpdesk.service.MemberService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFacadeImpl implements AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final MemberService memberService;

    @Override
    public TokenResponse login(LoginRequest request) {
        log.info("(login) request: {}", request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);

        log.info("(login) token generated successfully for: {}", request.getEmail());
        return new TokenResponse(token);
    }

    @Override
    public void logout() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("(logout) username: {}", username);
        } else {
            log.info("(logout)");
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    public MemberResponse register(RegisterRequest request) {
        log.info("(register) request: {}", request);
        return memberService.createMember(request);
    }
}
