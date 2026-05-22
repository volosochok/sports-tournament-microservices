package com.sportstournament.statistics.service;

import com.sportstournament.statistics.model.dto.MatchDTO;
import com.sportstournament.statistics.model.dto.TeamDTO;
import com.sportstournament.statistics.model.dto.TeamStandingDTO;
import com.sportstournament.statistics.model.dto.TournamentParticipationDTO;
import com.sportstournament.statistics.model.dto.TournamentDTO;
import com.sportstournament.statistics.model.dto.TeamStatisticsDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoringService {

    public List<TeamStandingDTO> calculateStandings(List<TeamDTO> teams, List<MatchDTO> matches) {
        Map<Long, TeamStats> statsMap = new HashMap<>();

        for (TeamDTO team : teams) {
            if (team != null && team.getId() != null) {
                statsMap.put(team.getId(), new TeamStats(team.getId(), team.getName()));
            }
        }

        if (statsMap.isEmpty()) {
            return new ArrayList<>();
        }

        for (MatchDTO match : matches) {
            if (match == null || !"FINISHED".equals(match.getStatus())) {
                continue;
            }

            Long team1Id = match.getTeam1Id();
            Long team2Id = match.getTeam2Id();
            Integer score1 = match.getTeam1Score();
            Integer score2 = match.getTeam2Score();

            if (team1Id == null || team2Id == null || score1 == null || score2 == null) {
                continue;
            }

            TeamStats stats1 = statsMap.get(team1Id);
            TeamStats stats2 = statsMap.get(team2Id);
            if (stats1 == null || stats2 == null) {
                continue;
            }

            stats1.goalsScored += score1;
            stats1.goalsConceded += score2;
            stats2.goalsScored += score2;
            stats2.goalsConceded += score1;

            stats1.matchesPlayed++;
            stats2.matchesPlayed++;

            if (score1 > score2) {
                stats1.wins++;
                stats1.points += 3;
                stats2.losses++;
            } else if (score2 > score1) {
                stats2.wins++;
                stats2.points += 3;
                stats1.losses++;
            } else {
                stats1.draws++;
                stats1.points += 1;
                stats2.draws++;
                stats2.points += 1;
            }
        }

        return statsMap.values().stream()
                .map(TeamStats::toDTO)
                .collect(Collectors.toList());
    }

    public TeamStatisticsDTO calculateTeamStatistics(TeamDTO team, List<MatchDTO> matches, List<TournamentDTO> tournaments) {
        TeamStatisticsDTO statistics = new TeamStatisticsDTO();
        statistics.setTeamId(team.getId());
        statistics.setTeamName(team.getName());

        System.out.println("=== calculateTeamStatistics for team: " + team.getName());
        System.out.println("Received " + matches.size() + " matches");

        int totalMatches = 0;
        int totalWins = 0;
        int totalDraws = 0;
        int totalLosses = 0;
        int totalGoalsScored = 0;
        int totalGoalsConceded = 0;
        int totalPoints = 0;

        // Статистика по турнірах
        Map<Long, TournamentParticipationDTO> tournamentStats = new HashMap<>();
        Map<Long, String> tournamentNames = new HashMap<>();

        for (TournamentDTO tournament : tournaments) {
            tournamentNames.put(tournament.getId(), tournament.getName());
        }

        for (MatchDTO match : matches) {
            System.out.println("Processing match: " + match.getId() + ", status: " + match.getStatus());
            if (!"FINISHED".equals(match.getStatus())) {
                System.out.println("  Skipping - match not finished");
                continue;
            }

            boolean isTeam1 = match.getTeam1Id() != null && match.getTeam1Id().equals(team.getId());
            boolean isTeam2 = match.getTeam2Id() != null && match.getTeam2Id().equals(team.getId());

            if (!isTeam1 && !isTeam2) {
                System.out.println("  Skipping - team not in this match");
                continue;
            }

            totalMatches++;

            int teamScore = isTeam1 ? match.getTeam1Score() : match.getTeam2Score();
            int opponentScore = isTeam1 ? match.getTeam2Score() : match.getTeam1Score();

            totalGoalsScored += teamScore;
            totalGoalsConceded += opponentScore;

            System.out.println("  Team score: " + teamScore + " vs " + opponentScore);

            TournamentParticipationDTO part = tournamentStats.computeIfAbsent(match.getTournamentId(),
                    k -> new TournamentParticipationDTO());
            part.setTournamentId(match.getTournamentId());
            part.setTournamentName(tournamentNames.getOrDefault(match.getTournamentId(), "Unknown"));

            if (teamScore > opponentScore) {
                totalWins++;
                totalPoints += 3;
                part.setWins(part.getWins() + 1);
                part.setPoints(part.getPoints() + 3);
            } else if (opponentScore > teamScore) {
                totalLosses++;
                part.setLosses(part.getLosses() + 1);
            } else {
                totalDraws++;
                totalPoints += 1;
                part.setDraws(part.getDraws() + 1);
                part.setPoints(part.getPoints() + 1);
            }
        }

        statistics.setTotalMatches(totalMatches);
        statistics.setTotalWins(totalWins);
        statistics.setTotalDraws(totalDraws);
        statistics.setTotalLosses(totalLosses);
        statistics.setTotalGoalsScored(totalGoalsScored);
        statistics.setTotalGoalsConceded(totalGoalsConceded);
        statistics.setTotalPoints(totalPoints);
        statistics.setTotalTournaments(tournamentStats.size());
        statistics.setTournaments(new ArrayList<>(tournamentStats.values()));

        System.out.println("Final statistics: matches=" + totalMatches +
                ", points=" + totalPoints +
                ", tournaments=" + tournamentStats.size());

        return statistics;
    }

    public List<TeamStandingDTO> getGlobalRanking(List<TeamDTO> teams, List<MatchDTO> allMatches) {
        List<TeamStandingDTO> standings = calculateStandings(teams, allMatches);

        standings.sort((a, b) -> {
            if (a.getPoints() != b.getPoints()) {
                return Integer.compare(b.getPoints(), a.getPoints());
            }
            if (a.getGoalDifference() != b.getGoalDifference()) {
                return Integer.compare(b.getGoalDifference(), a.getGoalDifference());
            }
            return Integer.compare(b.getGoalsScored(), a.getGoalsScored());
        });

        for (int i = 0; i < standings.size(); i++) {
            standings.get(i).setRank(i + 1);
        }

        return standings;
    }

    private static class TeamStats {
        Long teamId;
        String teamName;
        int points = 0;
        int matchesPlayed = 0;
        int wins = 0;
        int draws = 0;
        int losses = 0;
        int goalsScored = 0;
        int goalsConceded = 0;

        TeamStats(Long teamId, String teamName) {
            this.teamId = teamId;
            this.teamName = teamName != null ? teamName : "Unknown";
        }

        TeamStandingDTO toDTO() {
            return new TeamStandingDTO(
                    teamId, teamName, points, matchesPlayed,
                    wins, draws, losses, goalsScored, goalsConceded, 0
            );
        }
    }
}