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
@Schema(description = "Response DTO kết quả tóm tắt Ticket từ AI")
public class LlmSummaryResponse {

    @Schema(description = "ID của Ticket", example = "101")
    private Long requestId;

    @Schema(description = "Nội dung tóm tắt 1-2 câu từ AI", example = "Sập hệ thống thanh toán VNPay do lỗi server 500 khi mua hàng.")
    private String summary;
}
