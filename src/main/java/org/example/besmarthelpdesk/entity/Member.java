package org.example.besmarthelpdesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.besmarthelpdesk.enums.Role;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(exclude = "password")
public class Member extends AuditEntity {

    @Column(name = "member_id", length = 20, unique = true)
    private String memberId;

    @Column(name = "company_id", length = 20)
    private String companyId;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    @JsonIgnore
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(length = 20)
    @Builder.Default
    private String status = "active";

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}

