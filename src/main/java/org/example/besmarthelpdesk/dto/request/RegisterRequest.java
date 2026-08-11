package org.example.besmarthelpdesk.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.besmarthelpdesk.constant.MessageConstants;
import org.example.besmarthelpdesk.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @Size(max = 20, message = "Member ID cannot exceed 20 characters")
    private String memberId;

    @Size(max = 20, message = "Company ID cannot exceed 20 characters")
    private String companyId;

    @NotBlank(message = MessageConstants.EMAIL_BLANK)
    @Email(message = MessageConstants.EMAIL_INVALID)
    private String email;

    @NotBlank(message = MessageConstants.PASSWORD_BLANK)
    @ToString.Exclude
    private String password;

    @NotBlank(message = MessageConstants.NAME_BLANK)
    private String name;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @NotNull(message = MessageConstants.ROLE_NULL)
    private Role role;

    private String status;

    public RegisterRequest(String email, String password, String name, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
    }
}

