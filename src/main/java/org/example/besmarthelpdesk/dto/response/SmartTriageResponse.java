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
@Schema(description = "Response DTO kết quả Smart Triage tổng hợp (cho internal call khi tạo Ticket)")
public class SmartTriageResponse {

    @Schema(description = "Category tự động (BUG, FEATURE, INQUIRY)", example = "BUG")
    private String category;

    @Schema(description = "Priority đề xuất (HIGH, MEDIUM, LOW)", example = "HIGH")
    private String priority;

    @Schema(description = "Tags tự động trích xuất", example = "[\"payment\", \"vnpay\"]")
    private List<String> tags;

    @Schema(description = "Tóm tắt ticket ngắn gọn 1-2 câu", example = "Sập hệ thống thanh toán khi bấm nút mua hàng.")
    private String summary;

    @Schema(description = "Lý do AI phân tích", example = "Lỗi ảnh hưởng trực tiếp thanh toán và giao dịch của khách hàng.")
    private String reasoning;
}
