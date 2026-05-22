package com.sportstournament.statistics.client;

import com.sportstournament.statistics.model.dto.TeamDTO;
import com.sportstournament.statistics.model.dto.TournamentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "tournament-service", url = "${tournament.service.url:http://localhost:8081}")
public interface TournamentServiceClient {

    @GetMapping("/api/tournaments/{id}")
    TournamentDTO getTournamentById(@PathVariable("id") Long id);

    @GetMapping("/api/tournaments/{id}/teams")
    List<TeamDTO> getTeamsByTournament(@PathVariable("id") Long id);

    @GetMapping("/api/teams/{id}")
    TeamDTO getTeamById(@PathVariable("id") Long id);

    @GetMapping("/api/teams")
    List<TeamDTO> getAllTeams();

    @GetMapping("/api/tournaments")
    List<TournamentDTO> getAllTournaments();
}