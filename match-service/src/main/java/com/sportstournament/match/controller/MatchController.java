package com.sportstournament.match.controller;

import com.sportstournament.match.model.dto.MatchResultDTO;
import com.sportstournament.match.model.entity.Match;
import com.sportstournament.match.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<Match>> getMatchesByTournament(@PathVariable Long tournamentId) {
        return ResponseEntity.ok(matchService.getMatchesByTournament(tournamentId));
    }

    @PostMapping("/tournament/{tournamentId}/generate-bracket")
    public ResponseEntity<Void> generateBracket(@PathVariable Long tournamentId) {
        matchService.generateBracket(tournamentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/result")
    public ResponseEntity<Void> recordMatchResult(@PathVariable Long id,
                                                  @Valid @RequestBody MatchResultDTO result) {
        matchService.recordMatchResult(id, result);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<Match>> getMatchesByTeam(@PathVariable Long teamId) {
        List<Match> matches = matchService.getMatchesByTeam(teamId);
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/test/team/{teamId}")
    public ResponseEntity<String> testGetMatchesByTeam(@PathVariable Long teamId) {
        List<Match> matches = matchService.getMatchesByTeam(teamId);
        return ResponseEntity.ok("Found " + matches.size() + " matches for team " + teamId);
    }
}