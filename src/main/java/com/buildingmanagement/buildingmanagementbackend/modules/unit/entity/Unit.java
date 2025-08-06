package com.buildingmanagement.buildingmanagementbackend.modules.unit.entity;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UnitType;
import com.buildingmanagement.buildingmanagementbackend.shared.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "units", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"building_id", "unit_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Unit extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    @Column(name = "unit_number", nullable = false, length = 50)
    private String unitNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type", nullable = false)
    private UnitType unitType;

    @Column(precision = 8, scale = 2)
    private Double area;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public void prePersist() {
        if (this.isActive == null) {
            this.isActive = true;
        }
    }
}