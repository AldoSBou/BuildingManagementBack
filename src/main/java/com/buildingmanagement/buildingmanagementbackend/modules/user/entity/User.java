package com.buildingmanagement.buildingmanagementbackend.modules.user.entity;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UserRole;
import com.buildingmanagement.buildingmanagementbackend.shared.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}