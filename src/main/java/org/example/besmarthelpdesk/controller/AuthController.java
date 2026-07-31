package org.example.besmarthelpdesk.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.besmarthelpdesk.constant.MessageConstants;
import org.example.besmarthelpdesk.dto.ResponseGeneral;
import org.example.besmarthelpdesk.dto.request.LoginRequest;
import org.example.besmarthelpdesk.dto.request.RegisterRequest;
import org.example.besmarthelpdesk.dto.response.MemberResponse;
import org.example.besmarthelpdesk.dto.response.TokenResponse;
import org.example.besmarthelpdesk.facade.AuthFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/login")
    public ResponseEntity<ResponseGeneral<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("(login) request: {}", request);
        TokenResponse tokenResponse = authFacade.login(request);
        ResponseGeneral<TokenResponse> body = ResponseGeneral.success(MessageConstants.LOGIN_SUCCESS, tokenResponse);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseGeneral<Void>> logout() {
        log.info("(logout)");
        authFacade.logout();
        ResponseGeneral<Void> body = ResponseGeneral.success(MessageConstants.LOGOUT_SUCCESS, null);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseGeneral<MemberResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("(register) request: {}", request);
        MemberResponse response = authFacade.register(request);
        ResponseGeneral<MemberResponse> body = ResponseGeneral.success(MessageConstants.REGISTER_SUCCESS, response);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }
}
