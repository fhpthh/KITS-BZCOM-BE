package org.example.besmarthelpdesk.facade;

import org.example.besmarthelpdesk.dto.request.LoginRequest;
import org.example.besmarthelpdesk.dto.request.RegisterRequest;
import org.example.besmarthelpdesk.dto.response.MemberResponse;
import org.example.besmarthelpdesk.dto.response.TokenResponse;

public interface AuthFacade {
    TokenResponse login(LoginRequest request);
    void logout();
    MemberResponse register(RegisterRequest request);
}
