package com.buildingmanagement.buildingmanagementbackend.modules.building.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BuildingUpdateRequest {

    @NotBlank(message = "Building name is required")
    @Size(min = 2, max = 255, message = "Building name must be between 2 and 255 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 500, message = "Address must be between 5 and 500 characters")
    private String address;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Integer totalUnits;

    private Long adminUserId;
}
