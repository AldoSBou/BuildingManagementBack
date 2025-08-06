package com.buildingmanagement.buildingmanagementbackend.modules.unit.service;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UnitType;
import com.buildingmanagement.buildingmanagementbackend.common.enums.UserRole;
import com.buildingmanagement.buildingmanagementbackend.common.exceptions.BusinessException;
import com.buildingmanagement.buildingmanagementbackend.common.exceptions.ResourceNotFoundException;
import com.buildingmanagement.buildingmanagementbackend.modules.building.entity.Building;
import com.buildingmanagement.buildingmanagementbackend.modules.building.repository.BuildingRepository;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.entity.Unit;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.mapper.UnitMapper;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.repository.UnitRepository;
import com.buildingmanagement.buildingmanagementbackend.modules.user.entity.User;
import com.buildingmanagement.buildingmanagementbackend.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final UnitMapper unitMapper;

    @Override
    @Transactional
    public UnitResponse createUnit(Long buildingId, UnitCreateRequest request) {
        log.info("Creating unit {} in building {}", request.getUnitNumber(), buildingId);

        // Verificar que el edificio exista
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + buildingId));

        // Verificar que el número de unidad sea único en el edificio
        if (unitRepository.existsByBuildingIdAndUnitNumber(buildingId, request.getUnitNumber())) {
            throw new BusinessException("Unit with number '" + request.getUnitNumber() + "' already exists in this building");
        }

        // Validar propietario si se proporciona
        if (request.getOwnerId() != null) {
            validateUserAsOwnerOrTenant(request.getOwnerId(), "owner");
        }

        // Validar inquilino si se proporciona
        if (request.getTenantId() != null) {
            validateUserAsOwnerOrTenant(request.getTenantId(), "tenant");
        }

        // Crear y guardar la unidad
        Unit unit = unitMapper.toEntity(request, buildingId);
        Unit savedUnit = unitRepository.save(unit);

        log.info("Unit created successfully with id: {}", savedUnit.getId());

        return enrichUnitResponse(unitMapper.toResponse(savedUnit));
    }

    @Override
    @Transactional(readOnly = true)
    public UnitResponse getUnitById(Long id) {
        log.debug("Getting unit by id: {}", id);

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));

        return enrichUnitResponse(unitMapper.toResponse(unit));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UnitListResponse> getUnitsByBuilding(Long buildingId, UnitSearchRequest searchRequest) {
        log.debug("Getting units for building {} with search: {}", buildingId, searchRequest);

        // Verificar que el edificio exista
        if (!buildingRepository.existsById(buildingId)) {
            throw new ResourceNotFoundException("Building not found with id: " + buildingId);
        }

        // Configurar paginación y ordenamiento
        Sort sort = Sort.by(
                searchRequest.getSortDirection().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                searchRequest.getSortBy()
        );

        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                sort
        );

        Page<Unit> unitsPage;

        // Aplicar filtros
        if (StringUtils.hasText(searchRequest.getUnitNumber())) {
            unitsPage = unitRepository.findByBuildingIdAndUnitNumberContainingIgnoreCase(
                    buildingId, searchRequest.getUnitNumber(), pageable);
        } else {
            unitsPage = unitRepository.findUnitsByFilters(
                    buildingId,
                    searchRequest.getUnitType(),
                    searchRequest.getHasOwner(),
                    pageable);
        }

        // Convertir a DTOs y enriquecer con nombres
        return unitsPage.map(unit -> {
            UnitListResponse response = unitMapper.toListResponse(unit);
            enrichUnitListResponse(response, unit);
            return response;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitListResponse> getUnitsByOwner(Long ownerId) {
        log.debug("Getting units for owner: {}", ownerId);

        List<Unit> units = unitRepository.findByOwnerIdAndIsActiveTrue(ownerId);

        return units.stream()
                .map(unit -> {
                    UnitListResponse response = unitMapper.toListResponse(unit);
                    enrichUnitListResponse(response, unit);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnitListResponse> getUnitsByTenant(Long tenantId) {
        log.debug("Getting units for tenant: {}", tenantId);

        List<Unit> units = unitRepository.findByTenantIdAndIsActiveTrue(tenantId);

        return units.stream()
                .map(unit -> {
                    UnitListResponse response = unitMapper.toListResponse(unit);
                    enrichUnitListResponse(response, unit);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UnitResponse updateUnit(Long id, UnitUpdateRequest request) {
        log.info("Updating unit with id: {}", id);

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));

        // Verificar que el número de unidad sea único (excluyendo la unidad actual)
        if (unitRepository.existsByBuildingIdAndUnitNumberAndIdNot(
                unit.getBuildingId(), request.getUnitNumber(), id)) {
            throw new BusinessException("Unit with number '" + request.getUnitNumber() + "' already exists in this building");
        }

        // Validar propietario si se proporciona
        if (request.getOwnerId() != null) {
            validateUserAsOwnerOrTenant(request.getOwnerId(), "owner");
        }

        // Validar inquilino si se proporciona
        if (request.getTenantId() != null) {
            validateUserAsOwnerOrTenant(request.getTenantId(), "tenant");
        }

        // Actualizar campos
        unitMapper.updateEntityFromRequest(request, unit);
        Unit updatedUnit = unitRepository.save(unit);

        log.info("Unit updated successfully with id: {}", updatedUnit.getId());

        return enrichUnitResponse(unitMapper.toResponse(updatedUnit));
    }

    @Override
    @Transactional
    public void deleteUnit(Long id) {
        log.info("Deleting unit with id: {}", id);

        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + id));

        // TODO: Verificar que no tenga cuotas o pagos pendientes antes de eliminar

        unitRepository.delete(unit);
        log.info("Unit deleted successfully with id: {}", id);
    }

    @Override
    @Transactional
    public UnitResponse assignOwner(Long unitId, UnitAssignmentRequest request) {
        log.info("Assigning owner {} to unit {}", request.getUserId(), unitId);

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));

        validateUserAsOwnerOrTenant(request.getUserId(), "owner");

        unit.setOwnerId(request.getUserId());
        Unit updatedUnit = unitRepository.save(unit);

        log.info("Owner assigned successfully to unit {}", unitId);

        return enrichUnitResponse(unitMapper.toResponse(updatedUnit));
    }

    @Override
    @Transactional
    public UnitResponse assignTenant(Long unitId, UnitAssignmentRequest request) {
        log.info("Assigning tenant {} to unit {}", request.getUserId(), unitId);

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));

        validateUserAsOwnerOrTenant(request.getUserId(), "tenant");

        unit.setTenantId(request.getUserId());
        Unit updatedUnit = unitRepository.save(unit);

        log.info("Tenant assigned successfully to unit {}", unitId);

        return enrichUnitResponse(unitMapper.toResponse(updatedUnit));
    }

    @Override
    @Transactional
    public UnitResponse removeOwner(Long unitId) {
        log.info("Removing owner from unit {}", unitId);

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));

        unit.setOwnerId(null);
        Unit updatedUnit = unitRepository.save(unit);

        log.info("Owner removed successfully from unit {}", unitId);

        return enrichUnitResponse(unitMapper.toResponse(updatedUnit));
    }

    @Override
    @Transactional
    public UnitResponse removeTenant(Long unitId) {
        log.info("Removing tenant from unit {}", unitId);

        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found with id: " + unitId));

        unit.setTenantId(null);
        Unit updatedUnit = unitRepository.save(unit);

        log.info("Tenant removed successfully from unit {}", unitId);

        return enrichUnitResponse(unitMapper.toResponse(updatedUnit));
    }

    @Override
    @Transactional(readOnly = true)
    public UnitSummaryResponse getBuildingSummary(Long buildingId) {
        log.debug("Getting summary for building: {}", buildingId);

        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + buildingId));

        Long totalUnits = unitRepository.countByBuildingIdAndIsActiveTrue(buildingId);
        Long occupiedUnits = unitRepository.countOccupiedUnitsByBuilding(buildingId);
        Long apartmentUnits = unitRepository.countByBuildingIdAndUnitTypeAndIsActiveTrue(buildingId, UnitType.APARTMENT);
        Long parkingUnits = unitRepository.countByBuildingIdAndUnitTypeAndIsActiveTrue(buildingId, UnitType.PARKING);
        Long storageUnits = unitRepository.countByBuildingIdAndUnitTypeAndIsActiveTrue(buildingId, UnitType.STORAGE);
        Double totalArea = unitRepository.getTotalAreaByBuilding(buildingId);

        return unitMapper.toSummaryResponse(
                buildingId,
                building.getName(),
                totalUnits,
                occupiedUnits,
                apartmentUnits,
                parkingUnits,
                storageUnits,
                totalArea != null ? totalArea : 0.0
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBuildingIdAndUnitNumber(Long buildingId, String unitNumber) {
        return unitRepository.existsByBuildingIdAndUnitNumber(buildingId, unitNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByBuildingIdAndUnitNumberAndIdNot(Long buildingId, String unitNumber, Long id) {
        return unitRepository.existsByBuildingIdAndUnitNumberAndIdNot(buildingId, unitNumber, id);
    }

    // Métodos privados de utilidad

    private void validateUserAsOwnerOrTenant(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!user.getIsActive()) {
            throw new BusinessException("User is not active");
        }

        // Validar que el usuario puede ser propietario o inquilino
        if (role.equals("owner") &&
                !user.getRole().equals(UserRole.OWNER) &&
                !user.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException("User must be OWNER or ADMIN to be assigned as owner");
        }

        if (role.equals("tenant") &&
                !user.getRole().equals(UserRole.TENANT) &&
                !user.getRole().equals(UserRole.OWNER) &&
                !user.getRole().equals(UserRole.ADMIN)) {
            throw new BusinessException("User must be TENANT, OWNER or ADMIN to be assigned as tenant");
        }
    }

    private UnitResponse enrichUnitResponse(UnitResponse response) {
        // Agregar nombre del edificio
        if (response.getBuildingId() != null) {
            buildingRepository.findById(response.getBuildingId())
                    .ifPresent(building -> response.setBuildingName(building.getName()));
        }

        // Agregar nombre del propietario
        if (response.getOwnerId() != null) {
            userRepository.findById(response.getOwnerId())
                    .ifPresent(owner -> response.setOwnerName(owner.getName()));
        }

        // Agregar nombre del inquilino
        if (response.getTenantId() != null) {
            userRepository.findById(response.getTenantId())
                    .ifPresent(tenant -> response.setTenantName(tenant.getName()));
        }

        return response;
    }

    private void enrichUnitListResponse(UnitListResponse response, Unit unit) {
        // Agregar nombre del propietario
        if (unit.getOwnerId() != null) {
            userRepository.findById(unit.getOwnerId())
                    .ifPresent(owner -> response.setOwnerName(owner.getName()));
        }

        // Agregar nombre del inquilino
        if (unit.getTenantId() != null) {
            userRepository.findById(unit.getTenantId())
                    .ifPresent(tenant -> response.setTenantName(tenant.getName()));
        }
    }
}