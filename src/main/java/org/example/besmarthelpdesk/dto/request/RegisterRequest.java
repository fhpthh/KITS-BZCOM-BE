package org.example.besmarthelpdesk.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.besmarthelpdesk.constant.MessageConstants;
import org.example.besmarthelpdesk.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = MessageConstants.EMAIL_BLANK)
    @Email(message = MessageConstants.EMAIL_INVALID)
    private String email;

    @NotBlank(message = MessageConstants.PASSWORD_BLANK)
    private String password;

    @NotBlank(message = MessageConstants.NAME_BLANK)
    private String name;

    @NotNull(message = MessageConstants.ROLE_NULL)
    private Role role;
}
