package com.buildingmanagement.buildingmanagementbackend.modules.building.mapper;

import com.buildingmanagement.buildingmanagementbackend.modules.building.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.building.entity.Building;
import org.springframework.stereotype.Component;

@Component
public class BuildingMapper {

    public Building toEntity(BuildingCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Building.builder()
                .name(request.getName())
                .address(request.getAddress())
                .description(request.getDescription())
                .totalUnits(request.getTotalUnits() != null ? request.getTotalUnits() : 0)
                .adminUserId(request.getAdminUserId())
                .build();
    }

    public BuildingResponse toResponse(Building building) {
        if (building == null) {
            return null;
        }

        return BuildingResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .address(building.getAddress())
                .description(building.getDescription())
                .totalUnits(building.getTotalUnits())
                .adminUserId(building.getAdminUserId())
                .adminName(null) // Se asignará en el service
                .createdAt(building.getCreatedAt())
                .updatedAt(building.getUpdatedAt())
                .build();
    }

    public BuildingListResponse toListResponse(Building building) {
        if (building == null) {
            return null;
        }

        return BuildingListResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .address(building.getAddress())
                .totalUnits(building.getTotalUnits())
                .adminName(null) // Se asignará en el service
                .createdAt(building.getCreatedAt())
                .build();
    }

    public void updateEntityFromRequest(BuildingUpdateRequest request, Building building) {
        if (request == null || building == null) {
            return;
        }

        building.setName(request.getName());
        building.setAddress(request.getAddress());
        building.setDescription(request.getDescription());
        building.setTotalUnits(request.getTotalUnits() != null ? request.getTotalUnits() : 0);
        building.setAdminUserId(request.getAdminUserId());
    }
}