
package com.carbontrack.carbontrackbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FootprintAggregateDTO {

    private final LocalDate date;
    private final String category;
    private final BigDecimal totalCo2eKg;

    public FootprintAggregateDTO(
            LocalDate date,
            String category,
            BigDecimal totalCo2eKg) {
        this.date = date;
        this.category = category;
        this.totalCo2eKg = totalCo2eKg;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getTotalCo2eKg() {
        return totalCo2eKg;
    }
}
