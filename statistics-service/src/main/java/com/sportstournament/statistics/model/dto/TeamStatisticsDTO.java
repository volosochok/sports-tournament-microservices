package com.sportstournament.statistics.model.dto;

import java.util.List;

public class TeamStatisticsDTO {
    private Long teamId;
    private String teamName;
    private int totalTournaments;
    private int totalMatches;
    private int totalWins;
    private int totalDraws;
    private int totalLosses;
    private int totalGoalsScored;
    private int totalGoalsConceded;
    private int totalPoints;
    private List<TournamentParticipationDTO> tournaments;

    // Getters and Setters
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public int getTotalTournaments() { return totalTournaments; }
    public void setTotalTournaments(int totalTournaments) { this.totalTournaments = totalTournaments; }

    public int getTotalMatches() { return totalMatches; }
    public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }

    public int getTotalWins() { return totalWins; }
    public void setTotalWins(int totalWins) { this.totalWins = totalWins; }

    public int getTotalDraws() { return totalDraws; }
    public void setTotalDraws(int totalDraws) { this.totalDraws = totalDraws; }

    public int getTotalLosses() { return totalLosses; }
    public void setTotalLosses(int totalLosses) { this.totalLosses = totalLosses; }

    public int getTotalGoalsScored() { return totalGoalsScored; }
    public void setTotalGoalsScored(int totalGoalsScored) { this.totalGoalsScored = totalGoalsScored; }

    public int getTotalGoalsConceded() { return totalGoalsConceded; }
    public void setTotalGoalsConceded(int totalGoalsConceded) { this.totalGoalsConceded = totalGoalsConceded; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public List<TournamentParticipationDTO> getTournaments() { return tournaments; }
    public void setTournaments(List<TournamentParticipationDTO> tournaments) { this.tournaments = tournaments; }
}