package com.buildingmanagement.buildingmanagementbackend.modules.building.entity;

import com.buildingmanagement.buildingmanagementbackend.shared.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buildings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Building extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_units")
    private Integer totalUnits = 0;

    @Column(name = "admin_user_id")
    private Long adminUserId;

    public void prePersist() {
        if (this.totalUnits == null) {
            this.totalUnits = 0;
        }
    }
}