package com.buildingmanagement.buildingmanagementbackend.modules.building.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildingBasicInfoResponse {
    private Long id;
    private String name;
    private String address;
    private Integer totalUnits;
}