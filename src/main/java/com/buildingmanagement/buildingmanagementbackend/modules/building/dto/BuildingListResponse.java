package com.buildingmanagement.buildingmanagementbackend.modules.building.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BuildingListResponse {

    private Long id;
    private String name;
    private String address;
    private Integer totalUnits;
    private String adminName;
    private LocalDateTime createdAt;
}