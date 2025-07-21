package com.buildingmanagement.buildingmanagementbackend.modules.building.service;

import com.buildingmanagement.buildingmanagementbackend.modules.building.dto.*;
import org.springframework.data.domain.Page;

public interface BuildingService {

    BuildingResponse createBuilding(BuildingCreateRequest request);

    BuildingResponse getBuildingById(Long id);

    Page<BuildingListResponse> getAllBuildings(BuildingSearchRequest searchRequest);

    BuildingResponse updateBuilding(Long id, BuildingUpdateRequest request);

    void deleteBuilding(Long id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
