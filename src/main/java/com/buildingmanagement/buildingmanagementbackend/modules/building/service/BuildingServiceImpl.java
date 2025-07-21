package com.buildingmanagement.buildingmanagementbackend.modules.building.service;

import com.buildingmanagement.buildingmanagementbackend.common.exceptions.BusinessException;
import com.buildingmanagement.buildingmanagementbackend.common.exceptions.ResourceNotFoundException;
import com.buildingmanagement.buildingmanagementbackend.modules.building.dto.*;
import com.buildingmanagement.buildingmanagementbackend.modules.building.entity.Building;
import com.buildingmanagement.buildingmanagementbackend.modules.building.mapper.BuildingMapper;
import com.buildingmanagement.buildingmanagementbackend.modules.building.repository.BuildingRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BuildingServiceImpl implements BuildingService {

    private final BuildingRepository buildingRepository;
    private final UserRepository userRepository;
    private final BuildingMapper buildingMapper;

    @Override
    @Transactional
    public BuildingResponse createBuilding(BuildingCreateRequest request) {
        log.info("Creating building with name: {}", request.getName());

        // Validar que el nombre no exista
        if (buildingRepository.existsByName(request.getName())) {
            throw new BusinessException("Building with name '" + request.getName() + "' already exists");
        }

        // Validar administrador si se proporciona
        if (request.getAdminUserId() != null) {
            User admin = userRepository.findById(request.getAdminUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + request.getAdminUserId()));

            // Verificar que el usuario puede ser administrador
            if (!admin.getRole().name().equals("ADMIN") && !admin.getRole().name().equals("BOARD_MEMBER")) {
                throw new BusinessException("User must be ADMIN or BOARD_MEMBER to manage a building");
            }
        }

        // Crear y guardar el edificio
        Building building = buildingMapper.toEntity(request);
        Building savedBuilding = buildingRepository.save(building);

        log.info("Building created successfully with id: {}", savedBuilding.getId());

        // Convertir a response y agregar nombre del admin
        BuildingResponse response = buildingMapper.toResponse(savedBuilding);
        if (savedBuilding.getAdminUserId() != null) {
            userRepository.findById(savedBuilding.getAdminUserId())
                    .ifPresent(admin -> response.setAdminName(admin.getName()));
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BuildingResponse getBuildingById(Long id) {
        log.debug("Getting building by id: {}", id);

        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        BuildingResponse response = buildingMapper.toResponse(building);

        // Agregar nombre del admin
        if (building.getAdminUserId() != null) {
            userRepository.findById(building.getAdminUserId())
                    .ifPresent(admin -> response.setAdminName(admin.getName()));
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BuildingListResponse> getAllBuildings(BuildingSearchRequest searchRequest) {
        log.debug("Getting all buildings with search: {}", searchRequest);

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

        Page<Building> buildingsPage;

        // Aplicar filtros de búsqueda
        if (StringUtils.hasText(searchRequest.getName())) {
            buildingsPage = buildingRepository.findByNameContainingIgnoreCase(searchRequest.getName(), pageable);
        } else if (StringUtils.hasText(searchRequest.getAddress())) {
            buildingsPage = buildingRepository.findByAddressContainingIgnoreCase(searchRequest.getAddress(), pageable);
        } else {
            buildingsPage = buildingRepository.findAll(pageable);
        }

        // Convertir a DTOs y agregar nombres de admin
        return buildingsPage.map(building -> {
            BuildingListResponse response = buildingMapper.toListResponse(building);
            if (building.getAdminUserId() != null) {
                userRepository.findById(building.getAdminUserId())
                        .ifPresent(admin -> response.setAdminName(admin.getName()));
            }
            return response;
        });
    }

    @Override
    @Transactional
    public BuildingResponse updateBuilding(Long id, BuildingUpdateRequest request) {
        log.info("Updating building with id: {}", id);

        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        // Validar que el nombre no exista en otro edificio
        if (buildingRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BusinessException("Building with name '" + request.getName() + "' already exists");
        }

        // Validar administrador si se proporciona
        if (request.getAdminUserId() != null) {
            User admin = userRepository.findById(request.getAdminUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin user not found with id: " + request.getAdminUserId()));

            if (!admin.getRole().name().equals("ADMIN") && !admin.getRole().name().equals("BOARD_MEMBER")) {
                throw new BusinessException("User must be ADMIN or BOARD_MEMBER to manage a building");
            }
        }

        // Actualizar campos
        buildingMapper.updateEntityFromRequest(request, building);
        Building updatedBuilding = buildingRepository.save(building);

        log.info("Building updated successfully with id: {}", updatedBuilding.getId());

        // Convertir a response
        BuildingResponse response = buildingMapper.toResponse(updatedBuilding);
        if (updatedBuilding.getAdminUserId() != null) {
            userRepository.findById(updatedBuilding.getAdminUserId())
                    .ifPresent(admin -> response.setAdminName(admin.getName()));
        }

        return response;
    }

    @Override
    @Transactional
    public void deleteBuilding(Long id) {
        log.info("Deleting building with id: {}", id);

        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found with id: " + id));

        // TODO: Verificar que no tenga unidades asociadas antes de eliminar
        // TODO: Verificar que no tenga cuotas o pagos pendientes

        buildingRepository.delete(building);
        log.info("Building deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return buildingRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, Long id) {
        return buildingRepository.existsByNameAndIdNot(name, id);
    }
}