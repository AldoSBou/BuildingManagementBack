package com.buildingmanagement.buildingmanagementbackend.modules.unit.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UnitSummaryResponse {

    private Long buildingId;
    private String buildingName;
    private Long totalUnits;
    private Long occupiedUnits;
    private Long vacantUnits;
    private Long apartmentUnits;
    private Long parkingUnits;
    private Long storageUnits;
    private Double totalArea;
    private Double occupancyRate; // Porcentaje de ocupación
}