package com.buildingmanagement.buildingmanagementbackend.modules.building.repository;

import com.buildingmanagement.buildingmanagementbackend.modules.building.entity.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    // Buscar edificio por nombre
    Optional<Building> findByName(String name);

    // Verificar si existe un edificio con ese nombre
    boolean existsByName(String name);

    // Verificar si existe un edificio con ese nombre excluyendo un ID específico
    boolean existsByNameAndIdNot(String name, Long id);

    // Buscar edificios por administrador
    List<Building> findByAdminUserId(Long adminUserId);

    // Buscar edificios con paginación
    Page<Building> findAll(Pageable pageable);

    // Buscar edificios por nombre que contenga un texto (para búsqueda)
    @Query("SELECT b FROM Building b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Building> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    // Buscar edificios por dirección que contenga un texto
    @Query("SELECT b FROM Building b WHERE LOWER(b.address) LIKE LOWER(CONCAT('%', :address, '%'))")
    Page<Building> findByAddressContainingIgnoreCase(@Param("address") String address, Pageable pageable);

    // Contar total de edificios
    long count();

    // Buscar edificios con más de X unidades
    @Query("SELECT b FROM Building b WHERE b.totalUnits >= :minUnits")
    List<Building> findBuildingsWithMinUnits(@Param("minUnits") Integer minUnits);
}