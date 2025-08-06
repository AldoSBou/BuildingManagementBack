package com.buildingmanagement.buildingmanagementbackend.modules.unit.repository;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UnitType;
import com.buildingmanagement.buildingmanagementbackend.modules.unit.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    // Buscar unidades por edificio
    List<Unit> findByBuildingIdAndIsActiveTrue(Long buildingId);

    Page<Unit> findByBuildingIdAndIsActiveTrue(Long buildingId, Pageable pageable);

    // Buscar unidad específica por edificio y número
    Optional<Unit> findByBuildingIdAndUnitNumberAndIsActiveTrue(Long buildingId, String unitNumber);

    // Verificar si existe una unidad con ese número en el edificio
    boolean existsByBuildingIdAndUnitNumber(Long buildingId, String unitNumber);

    // Verificar excluyendo un ID específico (para updates)
    boolean existsByBuildingIdAndUnitNumberAndIdNot(Long buildingId, String unitNumber, Long id);

    // Buscar unidades por propietario
    List<Unit> findByOwnerIdAndIsActiveTrue(Long ownerId);

    Page<Unit> findByOwnerIdAndIsActiveTrue(Long ownerId, Pageable pageable);

    // Buscar unidades por inquilino
    List<Unit> findByTenantIdAndIsActiveTrue(Long tenantId);

    // Buscar unidades por tipo
    List<Unit> findByBuildingIdAndUnitTypeAndIsActiveTrue(Long buildingId, UnitType unitType);

    Page<Unit> findByBuildingIdAndUnitTypeAndIsActiveTrue(Long buildingId, UnitType unitType, Pageable pageable);

    // Buscar unidades disponibles (sin propietario)
    @Query("SELECT u FROM Unit u WHERE u.buildingId = :buildingId AND u.ownerId IS NULL AND u.isActive = true")
    List<Unit> findAvailableUnitsByBuilding(@Param("buildingId") Long buildingId);

    // Buscar unidades por número que contenga texto
    @Query("SELECT u FROM Unit u WHERE u.buildingId = :buildingId AND LOWER(u.unitNumber) LIKE LOWER(CONCAT('%', :unitNumber, '%')) AND u.isActive = true")
    Page<Unit> findByBuildingIdAndUnitNumberContainingIgnoreCase(@Param("buildingId") Long buildingId,
                                                                 @Param("unitNumber") String unitNumber,
                                                                 Pageable pageable);

    // Contar unidades por edificio
    long countByBuildingIdAndIsActiveTrue(Long buildingId);

    // Contar unidades por tipo en un edificio
    long countByBuildingIdAndUnitTypeAndIsActiveTrue(Long buildingId, UnitType unitType);

    // Contar unidades con propietario en un edificio
    @Query("SELECT COUNT(u) FROM Unit u WHERE u.buildingId = :buildingId AND u.ownerId IS NOT NULL AND u.isActive = true")
    long countOccupiedUnitsByBuilding(@Param("buildingId") Long buildingId);

    // Obtener área total por edificio
    @Query("SELECT COALESCE(SUM(u.area), 0) FROM Unit u WHERE u.buildingId = :buildingId AND u.isActive = true")
    Double getTotalAreaByBuilding(@Param("buildingId") Long buildingId);

    // Buscar unidades por múltiples filtros
    @Query("SELECT u FROM Unit u WHERE u.buildingId = :buildingId " +
            "AND (:unitType IS NULL OR u.unitType = :unitType) " +
            "AND (:hasOwner IS NULL OR (:hasOwner = true AND u.ownerId IS NOT NULL) OR (:hasOwner = false AND u.ownerId IS NULL)) " +
            "AND u.isActive = true")
    Page<Unit> findUnitsByFilters(@Param("buildingId") Long buildingId,
                                  @Param("unitType") UnitType unitType,
                                  @Param("hasOwner") Boolean hasOwner,
                                  Pageable pageable);
}