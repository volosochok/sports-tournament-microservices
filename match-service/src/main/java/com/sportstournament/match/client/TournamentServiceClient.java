package com.sportstournament.match.client;

import com.sportstournament.match.model.dto.TeamDTO;
import com.sportstournament.match.model.dto.TournamentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @PutMapping("/api/tournaments/{id}/status")
    void updateTournamentStatus(@PathVariable("id") Long id, @RequestParam("status") String status);
}