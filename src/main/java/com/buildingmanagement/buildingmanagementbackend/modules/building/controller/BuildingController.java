package com.buildingmanagement.buildingmanagementbackend.modules.building.controller;

import com.buildingmanagement.buildingmanagementbackend.modules.building.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.building.service.BuildingService;
import com.buildingmanagement.buildingmanagementbackend.security.UserPrincipal;
import com.buildingmanagement.buildingmanagementbackend.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    /**
     * Crear nuevo edificio
     * Solo administradores pueden crear edificios
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BuildingResponse>> createBuilding(
            @Valid @RequestBody BuildingCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Creating building request from user: {} for building: {}",
                currentUser.getEmail(), request.getName());

        BuildingResponse response = buildingService.createBuilding(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Building created successfully"));
    }

    /**
     * Obtener edificio por ID
     * Usuarios autenticados pueden ver edificios
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER') or hasRole('OWNER') or hasRole('TENANT')")
    public ResponseEntity<ApiResponse<BuildingResponse>> getBuildingById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting building {} requested by user: {}", id, currentUser.getEmail());

        BuildingResponse response = buildingService.getBuildingById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Listar todos los edificios con filtros y paginación
     * Solo administradores pueden ver todos los edificios
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<BuildingListResponse>>> getAllBuildings(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String address,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting all buildings requested by admin: {}", currentUser.getEmail());

        BuildingSearchRequest searchRequest = new BuildingSearchRequest();
        searchRequest.setName(name);
        searchRequest.setAddress(address);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        Page<BuildingListResponse> response = buildingService.getAllBuildings(searchRequest);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Actualizar edificio
     * Solo administradores y board members pueden actualizar
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<BuildingResponse>> updateBuilding(
            @PathVariable Long id,
            @Valid @RequestBody BuildingUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Updating building {} requested by user: {}", id, currentUser.getEmail());

        BuildingResponse response = buildingService.updateBuilding(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Building updated successfully"));
    }

    /**
     * Eliminar edificio
     * Solo administradores pueden eliminar edificios
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteBuilding(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Deleting building {} requested by admin: {}", id, currentUser.getEmail());

        buildingService.deleteBuilding(id);

        return ResponseEntity.ok(ApiResponse.success("Building deleted successfully"));
    }

    /**
     * Verificar si existe un edificio con un nombre específico
     * Útil para validaciones en el frontend
     */
    @GetMapping("/check-name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> checkBuildingNameExists(
            @RequestParam String name,
            @RequestParam(required = false) Long excludeId) {

        boolean exists;
        if (excludeId != null) {
            exists = buildingService.existsByNameAndIdNot(name, excludeId);
        } else {
            exists = buildingService.existsByName(name);
        }

        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    /**
     * Obtener edificios donde el usuario actual es administrador
     * Para board members que gestionan edificios específicos
     */
    @GetMapping("/my-buildings")
    @PreAuthorize("hasRole('BOARD_MEMBER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<BuildingListResponse>>> getMyBuildings(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting buildings managed by user: {}", currentUser.getEmail());

        // Para admin, mostrar todos los edificios
        // Para board member, filtrar por adminUserId
        BuildingSearchRequest searchRequest = new BuildingSearchRequest();
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        Page<BuildingListResponse> response = buildingService.getAllBuildings(searchRequest);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}