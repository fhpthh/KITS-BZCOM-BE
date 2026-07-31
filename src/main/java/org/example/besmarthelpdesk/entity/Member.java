package org.example.besmarthelpdesk.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    @JsonIgnore
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
}
