package org.example.besmarthelpdesk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.besmarthelpdesk.dto.request.LlmClassifyRequest;
import org.example.besmarthelpdesk.dto.request.LlmPriorityRequest;
import org.example.besmarthelpdesk.dto.response.SmartTriageResponse;
import org.example.besmarthelpdesk.service.LlmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class LlmFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private LlmService llmService;

    @Test
    @DisplayName("Test API Classify Category (POST /api/requests/classify)")
    void testClassify_BugCategory() throws Exception {
        LlmClassifyRequest request = LlmClassifyRequest.builder()
                .title("Sập hệ thống thanh toán VNPay")
                .description("Khách hàng báo lỗi 500 khi bấm nút mua hàng và không thể hoàn tất giao dịch")
                .build();

        mockMvc.perform(post("/api/requests/classify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", is("success")))
                .andExpect(jsonPath("$.data.category", is("BUG")))
                .andExpect(jsonPath("$.data.tags", hasItem("payment")));
    }

    @Test
    @DisplayName("Test API Suggest Priority (POST /api/requests/suggest-priority)")
    void testSuggestPriority_HighPriority() throws Exception {
        LlmPriorityRequest request = LlmPriorityRequest.builder()
                .title("Không thể đăng nhập tài khoản hệ thống")
                .description("Toàn bộ nhân viên công ty nhận thông báo 401 Unauthorized khi truy cập")
                .build();

        mockMvc.perform(post("/api/requests/suggest-priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.priority", is("HIGH")));
    }

    @Test
    @DisplayName("Test API Get Summary by ID (GET /api/requests/{id}/summary)")
    void testGetSummary_Success() throws Exception {
        mockMvc.perform(get("/api/requests/101/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.data.requestId", is(101)))
                .andExpect(jsonPath("$.data.summary", notNullValue()));
    }

    @Test
    @DisplayName("Test Internal Service Call autoTriage() cho Member B")
    void testAutoTriage_InternalCall() {
        SmartTriageResponse triage = llmService.autoTriage("Thêm nút xuất file Excel", "Giao diện báo cáo cần thêm tính năng xuất dữ liệu ra file excel");

        assertNotNull(triage);
        assertEquals("FEATURE", triage.getCategory());
        assertEquals("LOW", triage.getPriority());
        assertTrue(triage.getTags().contains("feature"));
        assertNotNull(triage.getSummary());
    }

    @Test
    @DisplayName("Test Graceful Fallback Engine (Không gây lỗi 500)")
    void testAutoTriage_FallbackBehavior() {
        SmartTriageResponse triage = llmService.autoTriage("Hỏi về thời gian bảo trì", "Công ty có lịch bảo trì hệ thống tuần này không?");

        assertNotNull(triage);
        assertEquals("INQUIRY", triage.getCategory());
        assertEquals("MEDIUM", triage.getPriority());
        assertNotNull(triage.getReasoning());
    }
}
