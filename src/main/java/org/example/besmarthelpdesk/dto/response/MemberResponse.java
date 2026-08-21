package org.example.besmarthelpdesk.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.besmarthelpdesk.enums.Role;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {
    private UUID id;
    private String memberId;
    private String companyId;
    private String email;
    private String name;
    private String phone;
    private Role role;
    private String status;
    private Boolean isDeleted;
    private Instant createdAt;
    private Instant updatedAt;
}

