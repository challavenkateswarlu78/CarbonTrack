
package com.carbontrack.carbontrackbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50,
            message = "Username must contain 3 to 50 characters")
    private String username;

    @NotBlank(message = "Preferred unit is required")
    @Pattern(
            regexp = "KG_CO2E|TONNES_CO2E",
            message = "Preferred unit must be KG_CO2E or TONNES_CO2E"
    )
    private String preferredUnit;

    private boolean goalVisibility;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPreferredUnit() {
        return preferredUnit;
    }

    public void setPreferredUnit(String preferredUnit) {
        this.preferredUnit = preferredUnit;
    }

    public boolean isGoalVisibility() {
        return goalVisibility;
    }

    public void setGoalVisibility(boolean goalVisibility) {
        this.goalVisibility = goalVisibility;
    }
}

