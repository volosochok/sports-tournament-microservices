package com.sportstournament.statistics.model.dto;

public class TournamentParticipationDTO {
    private Long tournamentId;
    private String tournamentName;
    private int points;
    private int wins;
    private int draws;
    private int losses;
    private int rank;

    public Long getTournamentId() { return tournamentId; }
    public void setTournamentId(Long tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}