package com.buildingmanagement.buildingmanagementbackend.modules.unit.controller;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UnitType;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.service.UnitService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    /**
     * Crear nueva unidad en un edificio
     * Solo administradores y board members pueden crear unidades
     */
    @PostMapping("/buildings/{buildingId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<UnitResponse>> createUnit(
            @PathVariable Long buildingId,
            @Valid @RequestBody UnitCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Creating unit {} in building {} requested by user: {}",
                request.getUnitNumber(), buildingId, currentUser.getEmail());

        UnitResponse response = unitService.createUnit(buildingId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Unit created successfully"));
    }

    /**
     * Obtener unidad por ID
     * Usuarios autenticados pueden ver unidades
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER') or hasRole('OWNER') or hasRole('TENANT')")
    public ResponseEntity<ApiResponse<UnitResponse>> getUnitById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting unit {} requested by user: {}", id, currentUser.getEmail());

        UnitResponse response = unitService.getUnitById(id);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Listar unidades de un edificio con filtros y paginación
     */
    @GetMapping("/buildings/{buildingId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER') or hasRole('OWNER') or hasRole('TENANT')")
    public ResponseEntity<ApiResponse<Page<UnitListResponse>>> getUnitsByBuilding(
            @PathVariable Long buildingId,
            @RequestParam(defaultValue = "") String unitNumber,
            @RequestParam(required = false) UnitType unitType,
            @RequestParam(required = false) Boolean hasOwner,
            @RequestParam(required = false) Boolean hasTenant,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "unitNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting units for building {} requested by user: {}", buildingId, currentUser.getEmail());

        UnitSearchRequest searchRequest = new UnitSearchRequest();
        searchRequest.setUnitNumber(unitNumber);
        searchRequest.setUnitType(unitType);
        searchRequest.setHasOwner(hasOwner);
        searchRequest.setHasTenant(hasTenant);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortDirection(sortDirection);

        Page<UnitListResponse> response = unitService.getUnitsByBuilding(buildingId, searchRequest);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Obtener unidades de un propietario específico
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER') or (hasRole('OWNER') and #ownerId == authentication.principal.id)")
    public ResponseEntity<ApiResponse<List<UnitListResponse>>> getUnitsByOwner(
            @PathVariable Long ownerId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting units for owner {} requested by user: {}", ownerId, currentUser.getEmail());

        List<UnitListResponse> response = unitService.getUnitsByOwner(ownerId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Obtener unidades de un inquilino específico
     */
    @GetMapping("/tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER') or (hasRole('TENANT') and #tenantId == authentication.principal.id)")
    public ResponseEntity<ApiResponse<List<UnitListResponse>>> getUnitsByTenant(
            @PathVariable Long tenantId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting units for tenant {} requested by user: {}", tenantId, currentUser.getEmail());

        List<UnitListResponse> response = unitService.getUnitsByTenant(tenantId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Obtener mis unidades (del usuario actual)
     */
    @GetMapping("/my-units")
    @PreAuthorize("hasRole('OWNER') or hasRole('TENANT')")
    public ResponseEntity<ApiResponse<List<UnitListResponse>>> getMyUnits(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting units for current user: {}", currentUser.getEmail());

        // Buscar como propietario e inquilino
        List<UnitListResponse> ownerUnits = unitService.getUnitsByOwner(currentUser.getId());
        List<UnitListResponse> tenantUnits = unitService.getUnitsByTenant(currentUser.getId());

        // Combinar ambas listas
        ownerUnits.addAll(tenantUnits);

        return ResponseEntity.ok(ApiResponse.success(ownerUnits));
    }

    /**
     * Actualizar unidad
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<UnitResponse>> updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UnitUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Updating unit {} requested by user: {}", id, currentUser.getEmail());

        UnitResponse response = unitService.updateUnit(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Unit updated successfully"));
    }

    /**
     * Eliminar unidad
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUnit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Deleting unit {} requested by admin: {}", id, currentUser.getEmail());

        unitService.deleteUnit(id);

        return ResponseEntity.ok(ApiResponse.success("Unit deleted successfully"));
    }

    /**
     * Asignar propietario a una unidad
     */
    @PutMapping("/{id}/assign-owner")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<UnitResponse>> assignOwner(
            @PathVariable Long id,
            @Valid @RequestBody UnitAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Assigning owner {} to unit {} requested by user: {}",
                request.getUserId(), id, currentUser.getEmail());

        UnitResponse response = unitService.assignOwner(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Owner assigned successfully"));
    }

    /**
     * Asignar inquilino a una unidad
     */
    @PutMapping("/{id}/assign-tenant")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<UnitResponse>> assignTenant(
            @PathVariable Long id,
            @Valid @RequestBody UnitAssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Assigning tenant {} to unit {} requested by user: {}",
                request.getUserId(), id, currentUser.getEmail());

        UnitResponse response = unitService.assignTenant(id, request);

        return ResponseEntity.ok(ApiResponse.success(response, "Tenant assigned successfully"));
    }

    /**
     * Remover propietario de una unidad
     */
    @DeleteMapping("/{id}/owner")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<UnitResponse>> removeOwner(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Removing owner from unit {} requested by user: {}", id, currentUser.getEmail());

        UnitResponse response = unitService.removeOwner(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Owner removed successfully"));
    }

    /**
     * Remover inquilino de una unidad
     */
    @DeleteMapping("/{id}/tenant")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<UnitResponse>> removeTenant(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.info("Removing tenant from unit {} requested by user: {}", id, currentUser.getEmail());

        UnitResponse response = unitService.removeTenant(id);

        return ResponseEntity.ok(ApiResponse.success(response, "Tenant removed successfully"));
    }

    /**
     * Obtener resumen estadístico de un edificio
     */
    @GetMapping("/buildings/{buildingId}/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<UnitSummaryResponse>> getBuildingSummary(
            @PathVariable Long buildingId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        log.debug("Getting building summary for {} requested by user: {}", buildingId, currentUser.getEmail());

        UnitSummaryResponse response = unitService.getBuildingSummary(buildingId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Verificar si existe una unidad con un número específico en un edificio
     */
    @GetMapping("/buildings/{buildingId}/check-unit-number")
    @PreAuthorize("hasRole('ADMIN') or hasRole('BOARD_MEMBER')")
    public ResponseEntity<ApiResponse<Boolean>> checkUnitNumberExists(
            @PathVariable Long buildingId,
            @RequestParam String unitNumber,
            @RequestParam(required = false) Long excludeId) {

        boolean exists;
        if (excludeId != null) {
            exists = unitService.existsByBuildingIdAndUnitNumberAndIdNot(buildingId, unitNumber, excludeId);
        } else {
            exists = unitService.existsByBuildingIdAndUnitNumber(buildingId, unitNumber);
        }

        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}
