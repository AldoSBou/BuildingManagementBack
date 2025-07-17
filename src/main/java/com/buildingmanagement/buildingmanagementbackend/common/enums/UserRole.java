package com.buildingmanagement.buildingmanagementbackend.common.enums;

public enum UserRole {
    ADMIN("ROLE_ADMIN"),
    BOARD_MEMBER("ROLE_BOARD_MEMBER"),
    OWNER("ROLE_OWNER"),
    TENANT("ROLE_TENANT");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}