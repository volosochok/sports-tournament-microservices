package com.sportstournament.tournament.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class TournamentRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Sport type is required")
    private String sportType;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate endDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}