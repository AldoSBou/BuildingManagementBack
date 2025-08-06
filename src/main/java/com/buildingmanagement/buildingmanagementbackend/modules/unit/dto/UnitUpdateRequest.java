package com.buildingmanagement.buildingmanagementbackend.modules.unit.dto;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UnitType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UnitUpdateRequest {

    @NotBlank(message = "Unit number is required")
    @Size(min = 1, max = 50, message = "Unit number must be between 1 and 50 characters")
    private String unitNumber;

    @NotNull(message = "Unit type is required")
    private UnitType unitType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Area must be greater than 0")
    @DecimalMax(value = "99999.99", message = "Area must be less than 99999.99")
    private Double area;

    private Long ownerId;

    private Long tenantId;

    private Boolean isActive;
}
