package com.sportstournament.statistics.service;

import com.sportstournament.statistics.client.MatchServiceClient;
import com.sportstournament.statistics.client.TournamentServiceClient;
import com.sportstournament.statistics.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {
    private final TournamentServiceClient tournamentClient;
    private final MatchServiceClient matchClient;
    private final ScoringService scoringService;

    public StatisticsService(TournamentServiceClient tournamentClient,
                             MatchServiceClient matchClient,
                             ScoringService scoringService) {
        this.tournamentClient = tournamentClient;
        this.matchClient = matchClient;
        this.scoringService = scoringService;
    }

    public StandingResponseDTO getTournamentStandings(Long tournamentId) {
        System.out.println("=== getTournamentStandings called for tournamentId: " + tournamentId);
        // Get tournament data
        TournamentDTO tournament = tournamentClient.getTournamentById(tournamentId);
        System.out.println("Tournament: " + tournament.getName() + ", status: " + tournament.getStatus());

        List<TeamDTO> teams = tournamentClient.getTeamsByTournament(tournamentId);
        System.out.println("Teams in tournament: " + teams.size());

        // Get matches for this tournament
        List<MatchDTO> matches = matchClient.getMatchesByTournament(tournamentId);
        System.out.println("Matches in tournament: " + matches.size());

        // Calculate standings
        List<TeamStandingDTO> standings = scoringService.calculateStandings(teams, matches);
        System.out.println("Standings calculated: " + standings.size());

        // Sort and assign ranks
        if (!standings.isEmpty()) {
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
        }

        // Determine winner
        TeamStandingDTO winner = null;
        if ("FINISHED".equals(tournament.getStatus()) && !standings.isEmpty()) {
            winner = standings.get(0);
            System.out.println("Winner: " + winner.getTeamName());
        }

        return new StandingResponseDTO(tournamentId, tournament.getName(), standings, winner);
    }

    public TeamStatisticsDTO getTeamStatistics(Long teamId) {
        System.out.println("=== getTeamStatistics called for teamId: " + teamId);

        // Get team data
        TeamDTO team = tournamentClient.getTeamById(teamId);
        if (team == null) {
            throw new RuntimeException("Team not found: " + teamId);
        }
        System.out.println("Team found: " + team.getName() + ", ID: " + team.getId());

        // Get all matches for this team
        List<MatchDTO> teamMatches = matchClient.getMatchesByTeam(teamId);
        System.out.println("Matches found for team " + teamId + ": " + teamMatches.size());

        for (MatchDTO match : teamMatches) {
            System.out.println("  Match ID: " + match.getId() +
                    ", Team1: " + match.getTeam1Id() +
                    ", Team2: " + match.getTeam2Id() +
                    ", Score: " + match.getTeam1Score() + "-" + match.getTeam2Score() +
                    ", Status: " + match.getStatus());
        }

        // Get all tournaments (for tournament names)
        List<TournamentDTO> allTournaments = tournamentClient.getAllTournaments();
        System.out.println("Total tournaments: " + allTournaments.size());

        TeamStatisticsDTO statistics = scoringService.calculateTeamStatistics(team, teamMatches, allTournaments);

        System.out.println("Statistics calculated: totalMatches=" + statistics.getTotalMatches() +
                ", totalPoints=" + statistics.getTotalPoints() +
                ", totalTournaments=" + statistics.getTotalTournaments());

        return statistics;
    }

    public List<TeamStandingDTO> getGlobalRanking() {
        System.out.println("=== getGlobalRanking called");

        List<TeamDTO> allTeams = tournamentClient.getAllTeams();
        System.out.println("Total teams: " + allTeams.size());

        List<MatchDTO> allMatches = new ArrayList<>();
        List<TournamentDTO> allTournaments = tournamentClient.getAllTournaments();

        for (TournamentDTO tournament : allTournaments) {
            List<MatchDTO> tournamentMatches = matchClient.getMatchesByTournament(tournament.getId());
            allMatches.addAll(tournamentMatches);
        }
        System.out.println("Total matches: " + allMatches.size());

        return scoringService.getGlobalRanking(allTeams, allMatches);
    }
}