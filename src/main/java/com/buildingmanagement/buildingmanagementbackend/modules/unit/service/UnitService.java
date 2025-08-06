package com.buildingmanagement.buildingmanagementbackend.modules.unit.service;

import com.buildingmanagement.buildingmanagementbackend.modules.unit.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UnitService {

    UnitResponse createUnit(Long buildingId, UnitCreateRequest request);

    UnitResponse getUnitById(Long id);

    Page<UnitListResponse> getUnitsByBuilding(Long buildingId, UnitSearchRequest searchRequest);

    List<UnitListResponse> getUnitsByOwner(Long ownerId);

    List<UnitListResponse> getUnitsByTenant(Long tenantId);

    UnitResponse updateUnit(Long id, UnitUpdateRequest request);

    void deleteUnit(Long id);

    UnitResponse assignOwner(Long unitId, UnitAssignmentRequest request);

    UnitResponse assignTenant(Long unitId, UnitAssignmentRequest request);

    UnitResponse removeOwner(Long unitId);

    UnitResponse removeTenant(Long unitId);

    UnitSummaryResponse getBuildingSummary(Long buildingId);

    boolean existsByBuildingIdAndUnitNumber(Long buildingId, String unitNumber);

    boolean existsByBuildingIdAndUnitNumberAndIdNot(Long buildingId, String unitNumber, Long id);
}
