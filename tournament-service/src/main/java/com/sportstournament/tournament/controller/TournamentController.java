package com.sportstournament.tournament.controller;

import com.sportstournament.tournament.model.dto.TournamentRequestDTO;
import com.sportstournament.tournament.model.entity.Team;
import com.sportstournament.tournament.model.entity.Tournament;
import com.sportstournament.tournament.service.TournamentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {
    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getTournamentById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getTournamentById(id));
    }

    @GetMapping("/{id}/teams")
    public ResponseEntity<List<Team>> getTeamsByTournament(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getTeamsByTournament(id));
    }

    @PostMapping
    public ResponseEntity<Tournament> createTournament(@Valid @RequestBody TournamentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Long id,
                                                       @Valid @RequestBody TournamentRequestDTO request) {
        return ResponseEntity.ok(tournamentService.updateTournament(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/teams/{teamId}")
    public ResponseEntity<Void> addTeamToTournament(@PathVariable Long id, @PathVariable Long teamId) {
        tournamentService.addTeamToTournament(id, teamId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/teams/{teamId}")
    public ResponseEntity<Void> removeTeamFromTournament(@PathVariable Long id, @PathVariable Long teamId) {
        tournamentService.removeTeamFromTournament(id, teamId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateTournamentStatus(@PathVariable Long id, @RequestParam String status) {
        tournamentService.updateTournamentStatus(id, status);
        return ResponseEntity.ok().build();
    }
}