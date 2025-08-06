package com.buildingmanagement.buildingmanagementbackend.modules.unit.dto;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UnitType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitListResponse {

    private Long id;
    private String unitNumber;
    private UnitType unitType;
    private Double area;
    private String ownerName;
    private String tenantName;
    private Boolean isActive;
}
