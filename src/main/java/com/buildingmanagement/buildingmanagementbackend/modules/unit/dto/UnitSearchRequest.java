package com.buildingmanagement.buildingmanagementbackend.modules.unit.dto;

import com.buildingmanagement.buildingmanagementbackend.common.enums.UnitType;
import lombok.Data;

@Data
public class UnitSearchRequest {

    private String unitNumber;
    private UnitType unitType;
    private Boolean hasOwner; // true = con propietario, false = sin propietario, null = todos
    private Boolean hasTenant; // true = con inquilino, false = sin inquilino, null = todos
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "unitNumber";
    private String sortDirection = "asc";
}