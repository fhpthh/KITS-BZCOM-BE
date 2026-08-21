package org.example.besmarthelpdesk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.besmarthelpdesk.dto.ResponseGeneral;
import org.example.besmarthelpdesk.dto.request.LlmClassifyRequest;
import org.example.besmarthelpdesk.dto.request.LlmPriorityRequest;
import org.example.besmarthelpdesk.dto.response.LlmClassifyResponse;
import org.example.besmarthelpdesk.dto.response.LlmPriorityResponse;
import org.example.besmarthelpdesk.dto.response.LlmSummaryResponse;
import org.example.besmarthelpdesk.service.LlmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Tag(name = "LLM API", description = "Các API phân tích AI / LLM tự động cho Request Ticket")
public class LlmController {

    private final LlmService llmService;

    @Operation(summary = "Tự động phân loại Category từ mô tả ticket", description = "Phân tích tiêu đề và mô tả để trả về category (BUG, FEATURE, INQUIRY) và danh sách tags.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phân loại thành công")
    })
    @PostMapping("/classify")
    public ResponseEntity<ResponseGeneral<LlmClassifyResponse>> classify(@Valid @RequestBody LlmClassifyRequest request) {
        log.info("(classify) title: {}", request.getTitle());
        LlmClassifyResponse response = llmService.classifyCategory(request);
        return ResponseEntity.ok(ResponseGeneral.success(response));
    }

    @Operation(summary = "Tự động đề xuất Priority từ mô tả ticket", description = "Phân tích mức độ ảnh hưởng của sự cố để đề xuất độ ưu tiên (HIGH, MEDIUM, LOW).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đề xuất priority thành công")
    })
    @PostMapping("/suggest-priority")
    public ResponseEntity<ResponseGeneral<LlmPriorityResponse>> suggestPriority(@Valid @RequestBody LlmPriorityRequest request) {
        log.info("(suggestPriority) title: {}", request.getTitle());
        LlmPriorityResponse response = llmService.suggestPriority(request);
        return ResponseEntity.ok(ResponseGeneral.success(response));
    }

    @Operation(summary = "Tự động tạo tóm tắt 1-2 dòng cho Ticket theo ID", description = "Truy vấn ticket theo ID và tự động sinh tóm tắt ngắn gọn.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tóm tắt ticket thành công")
    })
    @GetMapping("/{id}/summary")
    public ResponseEntity<ResponseGeneral<LlmSummaryResponse>> getSummary(@PathVariable("id") Long id) {
        log.info("(getSummary) requestId: {}", id);
        LlmSummaryResponse response = llmService.generateSummaryByRequestId(id);
        return ResponseEntity.ok(ResponseGeneral.success(response));
    }
}
