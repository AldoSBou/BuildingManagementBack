package com.buildingmanagement.buildingmanagementbackend.modules.building.controller;

import com.buildingmanagement.buildingmanagementbackend.modules.building.dto.BuildingBasicInfoResponse;
import com.buildingmanagement.buildingmanagementbackend.modules.building.dto.BuildingResponse;
import com.buildingmanagement.buildingmanagementbackend.modules.building.service.BuildingService;
import com.buildingmanagement.buildingmanagementbackend.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public/buildings")
@RequiredArgsConstructor
public class PublicBuildingController {

    private final BuildingService buildingService;

    /**
     * Obtener información básica de un edificio (para usuarios no autenticados)
     * Útil para páginas de información pública
     */
    @GetMapping("/{id}/basic-info")
    public ResponseEntity<ApiResponse<BuildingBasicInfoResponse>> getBuildingBasicInfo(
            @PathVariable Long id) {

        log.debug("Getting basic info for building: {}", id);

        BuildingResponse building = buildingService.getBuildingById(id);

        // Crear respuesta con información limitada (sin datos sensibles)
        BuildingBasicInfoResponse response = BuildingBasicInfoResponse.builder()
                .id(building.getId())
                .name(building.getName())
                .address(building.getAddress())
                .totalUnits(building.getTotalUnits())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}