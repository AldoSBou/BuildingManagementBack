package com.buildingmanagement.buildingmanagementbackend.modules.unit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnitAssignmentRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String notes; // Opcional: notas sobre la asignación
}
