package org.example.besmarthelpdesk;

import tools.jackson.databind.ObjectMapper;
import org.example.besmarthelpdesk.dto.request.LoginRequest;
import org.example.besmarthelpdesk.dto.request.RegisterRequest;
import org.example.besmarthelpdesk.enums.Role;
import org.example.besmarthelpdesk.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        memberRepository.deleteAll();
    }

    @Test
    public void testRegisterMember_Success() throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test User", Role.CLIENT);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Member registered successfully")))
                .andExpect(jsonPath("$.data.email", is("test@example.com")))
                .andExpect(jsonPath("$.data.name", is("Test User")))
                .andExpect(jsonPath("$.data.role", is("CLIENT")))
                .andExpect(jsonPath("$.data.id", notNullValue()));
    }

    @Test
    public void testRegisterMember_EmailCollision() throws Exception {
        RegisterRequest request1 = new RegisterRequest("collision@example.com", "password123", "User One", Role.CLIENT);
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        RegisterRequest request2 = new RegisterRequest("collision@example.com", "password456", "User Two", Role.DEVELOPER);
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Email is already registered")));
    }

    @Test
    public void testRegisterMember_InvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest("invalid-email", "password123", "Test User", Role.CLIENT);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Email must be a valid email format")));
    }

    @Test
    public void testLogin_Success() throws Exception {
        RegisterRequest register = new RegisterRequest("login@example.com", "password123", "Login User", Role.DEVELOPER);
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("login@example.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("Login successful")))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.user.email", is("login@example.com")))
                .andExpect(jsonPath("$.data.user.name", is("Login User")));
    }

    @Test
    public void testLogin_InvalidCredentials() throws Exception {
        LoginRequest login = new LoginRequest("nonexistent@example.com", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    public void testGetMembers_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    public void testGetMembers_ForbiddenForClient() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetMembers_AllowedForAdmin() throws Exception {
        mockMvc.perform(get("/api/members"))
                .andExpect(status().isOk());
    }

    @Test
    public void testRefreshToken_Success() throws Exception {
        RegisterRequest register = new RegisterRequest("refresh_test@example.com", "password123", "Refresh User", Role.DEVELOPER);
        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login = new LoginRequest("refresh_test@example.com", "password123");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String refreshToken = com.jayway.jsonpath.JsonPath.read(loginResponse, "$.data.refreshToken");

        org.example.besmarthelpdesk.dto.request.TokenRefreshRequest refreshRequest =
                new org.example.besmarthelpdesk.dto.request.TokenRefreshRequest(refreshToken);

        String refreshResponse = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.accessToken", notNullValue()))
                .andExpect(jsonPath("$.data.refreshToken", notNullValue()))
                .andExpect(jsonPath("$.data.accessTokenTtl", notNullValue()))
                .andExpect(jsonPath("$.data.refreshTokenTtl", notNullValue()))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());
    }
}
