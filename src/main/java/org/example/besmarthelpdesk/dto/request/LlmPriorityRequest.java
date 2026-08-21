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
@Schema(description = "Request DTO cho gợi ý Priority từ mô tả")
public class LlmPriorityRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Schema(description = "Tiêu đề của ticket", example = "Khách hàng không thể đăng nhập được tài khoản")
    private String title;

    @NotBlank(message = "Mô tả không được để trống")
    @Schema(description = "Mô tả chi tiết ticket", example = "Toàn bộ người dùng đăng nhập đều nhận báo lỗi 401 Unauthorized")
    private String description;
}
