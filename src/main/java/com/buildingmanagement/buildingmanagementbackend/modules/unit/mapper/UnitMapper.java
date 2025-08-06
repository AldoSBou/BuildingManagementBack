package com.buildingmanagement.buildingmanagementbackend.modules.unit.mapper;

import com.buildingmanagement.buildingmanagementbackend.modules.unit.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.entity.Unit;
import org.springframework.stereotype.Component;

@Component
public class UnitMapper {

    public Unit toEntity(UnitCreateRequest request, Long buildingId) {
        if (request == null) {
            return null;
        }

        return Unit.builder()
                .buildingId(buildingId)
                .unitNumber(request.getUnitNumber())
                .unitType(request.getUnitType())
                .area(request.getArea())
                .ownerId(request.getOwnerId())
                .tenantId(request.getTenantId())
                .isActive(true)
                .build();
    }

    public UnitResponse toResponse(Unit unit) {
        if (unit == null) {
            return null;
        }

        return UnitResponse.builder()
                .id(unit.getId())
                .buildingId(unit.getBuildingId())
                .buildingName(null) // Se asignará en el service
                .unitNumber(unit.getUnitNumber())
                .unitType(unit.getUnitType())
                .area(unit.getArea())
                .ownerId(unit.getOwnerId())
                .ownerName(null) // Se asignará en el service
                .tenantId(unit.getTenantId())
                .tenantName(null) // Se asignará en el service
                .isActive(unit.getIsActive())
                .createdAt(unit.getCreatedAt())
                .updatedAt(unit.getUpdatedAt())
                .build();
    }

    public UnitListResponse toListResponse(Unit unit) {
        if (unit == null) {
            return null;
        }

        return UnitListResponse.builder()
                .id(unit.getId())
                .unitNumber(unit.getUnitNumber())
                .unitType(unit.getUnitType())
                .area(unit.getArea())
                .ownerName(null) // Se asignará en el service
                .tenantName(null) // Se asignará en el service
                .isActive(unit.getIsActive())
                .build();
    }

    public void updateEntityFromRequest(UnitUpdateRequest request, Unit unit) {
        if (request == null || unit == null) {
            return;
        }

        unit.setUnitNumber(request.getUnitNumber());
        unit.setUnitType(request.getUnitType());
        unit.setArea(request.getArea());
        unit.setOwnerId(request.getOwnerId());
        unit.setTenantId(request.getTenantId());

        if (request.getIsActive() != null) {
            unit.setIsActive(request.getIsActive());
        }
    }

    public UnitSummaryResponse toSummaryResponse(Long buildingId,
                                                 String buildingName,
                                                 Long totalUnits,
                                                 Long occupiedUnits,
                                                 Long apartmentUnits,
                                                 Long parkingUnits,
                                                 Long storageUnits,
                                                 Double totalArea) {

        Long vacantUnits = totalUnits - occupiedUnits;
        Double occupancyRate = totalUnits > 0 ? (occupiedUnits.doubleValue() / totalUnits.doubleValue()) * 100 : 0.0;

        return UnitSummaryResponse.builder()
                .buildingId(buildingId)
                .buildingName(buildingName)
                .totalUnits(totalUnits)
                .occupiedUnits(occupiedUnits)
                .vacantUnits(vacantUnits)
                .apartmentUnits(apartmentUnits)
                .parkingUnits(parkingUnits)
                .storageUnits(storageUnits)
                .totalArea(totalArea)
                .occupancyRate(Math.round(occupancyRate * 100.0) / 100.0) // Redondear a 2 decimales
                .build();
    }
}