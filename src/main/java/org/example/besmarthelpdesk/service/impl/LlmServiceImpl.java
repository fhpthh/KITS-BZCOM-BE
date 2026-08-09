package org.example.besmarthelpdesk.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.besmarthelpdesk.dto.request.LlmClassifyRequest;
import org.example.besmarthelpdesk.dto.request.LlmPriorityRequest;
import org.example.besmarthelpdesk.dto.response.LlmClassifyResponse;
import org.example.besmarthelpdesk.dto.response.LlmPriorityResponse;
import org.example.besmarthelpdesk.dto.response.LlmSummaryResponse;
import org.example.besmarthelpdesk.dto.response.SmartTriageResponse;
import org.example.besmarthelpdesk.service.LlmService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LlmServiceImpl implements LlmService {

    @Value("${app.ai.provider:openai}")
    private String aiProvider;

    @Value("${app.ai.api-key:mock-key}")
    private String apiKey;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String model;

    @Value("${app.ai.timeout-ms:3000}")
    private long timeoutMs;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LlmClassifyResponse classifyCategory(LlmClassifyRequest request) {
        log.info("(classifyCategory) title: {}", request.getTitle());
        SmartTriageResponse triage = autoTriage(request.getTitle(), request.getDescription());
        return LlmClassifyResponse.builder()
                .category(triage.getCategory())
                .tags(triage.getTags())
                .confidence(0.95)
                .reasoning(triage.getReasoning())
                .build();
    }

    @Override
    public LlmPriorityResponse suggestPriority(LlmPriorityRequest request) {
        log.info("(suggestPriority) title: {}", request.getTitle());
        SmartTriageResponse triage = autoTriage(request.getTitle(), request.getDescription());
        return LlmPriorityResponse.builder()
                .priority(triage.getPriority())
                .confidence(0.95)
                .reasoning(triage.getReasoning())
                .build();
    }

    @Override
    public LlmSummaryResponse generateSummaryByRequestId(Long id) {
        log.info("(generateSummaryByRequestId) requestId: {}", id);
        // Sau này khi có RequestRepository có thể findById. Hiện tại mock theo ID.
        String mockTitle = "Request #" + id + " - Hỗ trợ sự cố hệ thống";
        String mockDesc = "Khách hàng báo cáo lỗi phát sinh khi thao tác trên ứng dụng CRM Smart Helpdesk.";
        SmartTriageResponse triage = autoTriage(mockTitle, mockDesc);
        
        return LlmSummaryResponse.builder()
                .requestId(id)
                .summary(triage.getSummary())
                .build();
    }

    @Override
    public SmartTriageResponse autoTriage(String title, String description) {
        log.info("(autoTriage) Processing LLM triage for title: {}", title);

        try {
            // Chạy LLM Call với Timeout (chặn đứng quá 3 giây)
            return CompletableFuture.supplyAsync(() -> callLlmApi(title, description))
                    .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                    .exceptionally(ex -> {
                        log.warn("(autoTriage) LLM Call timed out or failed: {}. Triggering Graceful Fallback.", ex.getMessage());
                        return executeFallbackTriage(title, description);
                    })
                    .get();
        } catch (Exception e) {
            log.warn("(autoTriage) Exception during execution, using fallback: {}", e.getMessage());
            return executeFallbackTriage(title, description);
        }
    }

    private SmartTriageResponse callLlmApi(String title, String description) {
        // Kiểm tra nếu là mock key thì chuyển thẳng qua Fallback Heuristic
        if (apiKey == null || apiKey.equalsIgnoreCase("mock-key") || apiKey.equalsIgnoreCase("mock") || apiKey.isBlank()) {
            log.info("(callLlmApi) No valid API key provided. Using Smart Fallback Heuristic Engine.");
            return executeFallbackTriage(title, description);
        }

        try {
            String systemPrompt = """
                    You are an AI Triage Assistant for a CRM Helpdesk System.
                    Analyze the request title and description provided by the client.
                    You MUST reply strictly in valid JSON format matching this schema:
                    {
                      "category": "BUG" | "FEATURE" | "INQUIRY",
                      "priority": "HIGH" | "MEDIUM" | "LOW",
                      "tags": ["string"],
                      "summary": "string",
                      "reasoning": "string"
                    }
                    Rules:
                    - If system crash, payment failure, or 500 error -> priority = HIGH, category = BUG.
                    - If feature request or UI addition -> category = FEATURE.
                    - Otherwise category = INQUIRY.
                    """;

            String userPrompt = "Title: " + title + "\nDescription: " + description;

            Map<String, Object> body = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userPrompt)
                    ),
                    "temperature", 0.2
            );

            RestClient restClient = RestClient.builder().build();
            String responseStr = restClient.post()
                    .uri("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            if (responseStr != null) {
                var jsonNode = objectMapper.readTree(responseStr);
                String content = jsonNode.path("choices").get(0).path("message").path("content").asText();
                return objectMapper.readValue(content, SmartTriageResponse.class);
            }
        } catch (Exception e) {
            log.error("(callLlmApi) OpenAI API Error: {}. Fallback activated.", e.getMessage());
        }

        return executeFallbackTriage(title, description);
    }

    private SmartTriageResponse executeFallbackTriage(String title, String description) {
        String text = ((title != null ? title : "") + " " + (description != null ? description : "")).toLowerCase();

        String category = "INQUIRY";
        String priority = "MEDIUM";
        List<String> tags = new ArrayList<>();

        if (text.contains("crash") || text.contains("sập") || text.contains("500") || text.contains("vnpay") 
                || text.contains("thanh toán") || text.contains("lỗi") || text.contains("bug") || text.contains("không thể đăng nhập")) {
            category = "BUG";
            priority = "HIGH";
            tags.add("critical");
            tags.add("bug");
            if (text.contains("thanh toán") || text.contains("vnpay")) {
                tags.add("payment");
            }
        } else if (text.contains("tính năng") || text.contains("giao diện") || text.contains("nút") || text.contains("thêm") || text.contains("feature")) {
            category = "FEATURE";
            priority = "LOW";
            tags.add("feature");
            tags.add("ui");
        } else {
            tags.add("inquiry");
            tags.add("general");
        }

        String summary = title != null && !title.isBlank() ? title : "Yêu cầu hỗ trợ từ khách hàng";
        if (description != null && description.length() > 60) {
            summary += " - " + description.substring(0, 57) + "...";
        } else if (description != null && !description.isBlank()) {
            summary += " - " + description;
        }

        return SmartTriageResponse.builder()
                .category(category)
                .priority(priority)
                .tags(tags)
                .summary(summary)
                .reasoning("Tự động phân tích theo Smart Triage Engine (Rule-based Fallback)")
                .build();
    }
}
