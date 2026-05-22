package com.sportstournament.tournament.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sportstournament.tournament.model.enums.TournamentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "sport_type", nullable = false)
    private String sportType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TournamentStatus status = TournamentStatus.PENDING;

    @ManyToMany
    @JoinTable(
            name = "tournament_teams",
            joinColumns = @JoinColumn(name = "tournament_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    @JsonIgnore
    private List<Team> teams = new ArrayList<>();

    public Tournament() {}

    public Tournament(String name, String sportType, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.sportType = sportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = TournamentStatus.PENDING;
    }

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

    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }

    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }
}