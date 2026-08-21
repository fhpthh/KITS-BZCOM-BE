package org.example.besmarthelpdesk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO kết quả phân loại Category từ AI")
public class LlmClassifyResponse {

    @Schema(description = "Category phân loại được (BUG, FEATURE, INQUIRY)", example = "BUG")
    private String category;

    @Schema(description = "Danh sách tags gợi ý cho Smart Assignment", example = "[\"payment\", \"vnpay\", \"backend\"]")
    private List<String> tags;

    @Schema(description = "Mức độ tin cậy (0.0 - 1.0)", example = "0.95")
    private Double confidence;

    @Schema(description = "Lý do AI phân loại", example = "Mô tả đề cập đến lỗi 500 server error khi thanh toán.")
    private String reasoning;
}
