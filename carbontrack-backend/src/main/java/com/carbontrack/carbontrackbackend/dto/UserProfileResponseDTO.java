
package com.carbontrack.carbontrackbackend.dto;

public class UserProfileResponseDTO {

    private Long userId;
    private String username;
    private String email;
    private String role;
    private String preferredUnit;
    private boolean goalVisibility;

    public UserProfileResponseDTO(
            Long userId,
            String username,
            String email,
            String role,
            String preferredUnit,
            boolean goalVisibility) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.preferredUnit = preferredUnit;
        this.goalVisibility = goalVisibility;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getPreferredUnit() {
        return preferredUnit;
    }

    public boolean isGoalVisibility() {
        return goalVisibility;
    }
}

