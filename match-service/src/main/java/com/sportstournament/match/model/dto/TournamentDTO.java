package com.sportstournament.match.model.dto;

import java.time.LocalDate;
import java.util.List;

public class TournamentDTO {
    private Long id;
    private String name;
    private String sportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private List<TeamDTO> teams;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<TeamDTO> getTeams() { return teams; }
    public void setTeams(List<TeamDTO> teams) { this.teams = teams; }
}