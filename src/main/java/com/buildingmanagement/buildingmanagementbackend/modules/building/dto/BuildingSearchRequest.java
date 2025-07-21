package com.buildingmanagement.buildingmanagementbackend.modules.building.dto;

import lombok.Data;

@Data
public class BuildingSearchRequest {

    private String name;
    private String address;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
}