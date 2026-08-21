package org.example.besmarthelpdesk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO cho phân loại Category từ mô tả")
public class LlmClassifyRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Schema(description = "Tiêu đề của ticket", example = "Sập hệ thống thanh toán khi thanh toán đơn hàng")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    @Schema(description = "Mô tả chi tiết ticket", example = "Khách hàng bấm vào nút thanh toán VNPay bị bắn ra lỗi 500 server error")
    private String description;
}
