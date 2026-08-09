package org.example.besmarthelpdesk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO kết quả gợi ý Priority từ AI")
public class LlmPriorityResponse {

    @Schema(description = "Mức độ ưu tiên gợi ý (HIGH, MEDIUM, LOW)", example = "HIGH")
    private String priority;

    @Schema(description = "Mức độ tin cậy (0.0 - 1.0)", example = "0.98")
    private Double confidence;

    @Schema(description = "Lý do AI đề xuất độ ưu tiên", example = "Sự cố ảnh hưởng toàn bộ người dùng không đăng nhập được hệ thống.")
    private String reasoning;
}
