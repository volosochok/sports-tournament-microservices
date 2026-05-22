package com.sportstournament.tournament.model.dto;

import jakarta.validation.constraints.NotBlank;

public class TeamRequestDTO {
    @NotBlank(message = "Team name is required")
    private String name;

    private String city;
    private String coachName;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }
}