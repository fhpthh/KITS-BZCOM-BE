package org.example.besmarthelpdesk.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.besmarthelpdesk.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberRequest {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Size(max = 20, message = "Company ID cannot exceed 20 characters")
    private String companyId;

    private Role role;

    @Size(max = 20, message = "Status cannot exceed 20 characters")
    private String status;
}
