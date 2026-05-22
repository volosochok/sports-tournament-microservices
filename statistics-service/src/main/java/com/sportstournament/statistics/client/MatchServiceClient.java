package com.sportstournament.statistics.client;

import com.sportstournament.statistics.model.dto.MatchDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "match-service", url = "${match.service.url:http://localhost:8082}")
public interface MatchServiceClient {

    @GetMapping("/api/matches/tournament/{tournamentId}")
    List<MatchDTO> getMatchesByTournament(@PathVariable("tournamentId") Long tournamentId);

    @GetMapping("/api/matches/{id}")
    MatchDTO getMatchById(@PathVariable("id") Long id);

    @GetMapping("/api/matches/team/{teamId}")
    List<MatchDTO> getMatchesByTeam(@PathVariable("teamId") Long teamId);
}