package com.buildingmanagement.buildingmanagementbackend.modules.user.repository;

import com.buildingmanagement.buildingmanagementbackend.modules.user.entity.User;
import com.buildingmanagement.buildingmanagementbackend.common.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    boolean existsByEmail(String email);

    List<User> findByBuildingIdAndIsActiveTrue(Long buildingId);

    List<User> findByRoleAndIsActiveTrue(UserRole role);

    @Query("SELECT u FROM User u WHERE u.buildingId = :buildingId AND u.role = :role AND u.isActive = true")
    List<User> findByBuildingIdAndRoleAndIsActiveTrue(@Param("buildingId") Long buildingId,
                                                      @Param("role") UserRole role);

    long countByBuildingIdAndIsActiveTrue(Long buildingId);
}
