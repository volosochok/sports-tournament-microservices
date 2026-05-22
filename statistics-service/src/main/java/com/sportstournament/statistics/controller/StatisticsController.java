package com.sportstournament.statistics.controller;

import com.sportstournament.statistics.model.dto.StandingResponseDTO;
import com.sportstournament.statistics.model.dto.TeamStandingDTO;
import com.sportstournament.statistics.model.dto.TeamStatisticsDTO;
import com.sportstournament.statistics.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/tournament/{tournamentId}/standings")
    public ResponseEntity<StandingResponseDTO> getTournamentStandings(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(statisticsService.getTournamentStandings(tournamentId));
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamStatisticsDTO> getTeamStatistics(@PathVariable Long teamId) {
        return ResponseEntity.ok(statisticsService.getTeamStatistics(teamId));
    }

    @GetMapping("/teams/ranking")
    public ResponseEntity<List<TeamStandingDTO>> getGlobalRanking() {
        return ResponseEntity.ok(statisticsService.getGlobalRanking());
    }
}