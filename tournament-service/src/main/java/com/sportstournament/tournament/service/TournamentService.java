package com.sportstournament.tournament.service;

import com.sportstournament.tournament.exception.ResourceNotFoundException;
import com.sportstournament.tournament.model.dto.TournamentRequestDTO;
import com.sportstournament.tournament.model.entity.Team;
import com.sportstournament.tournament.model.entity.Tournament;
import com.sportstournament.tournament.model.enums.TournamentStatus;
import com.sportstournament.tournament.repository.TeamRepository;
import com.sportstournament.tournament.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    public TournamentService(TournamentRepository tournamentRepository, TeamRepository teamRepository) {
        this.tournamentRepository = tournamentRepository;
        this.teamRepository = teamRepository;
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Tournament getTournamentById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found: " + id));
    }

    public Tournament createTournament(TournamentRequestDTO request) {
        Tournament tournament = new Tournament(
                request.getName(),
                request.getSportType(),
                request.getStartDate(),
                request.getEndDate()
        );
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public Tournament updateTournament(Long id, TournamentRequestDTO request) {
        Tournament tournament = getTournamentById(id);

        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new IllegalStateException("Cannot update tournament not in PENDING status");
        }

        tournament.setName(request.getName());
        tournament.setSportType(request.getSportType());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());

        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteTournament(Long id) {
        Tournament tournament = getTournamentById(id);

        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new IllegalStateException("Cannot delete tournament not in PENDING status");
        }

        tournamentRepository.delete(tournament);
    }

    @Transactional
    public void addTeamToTournament(Long tournamentId, Long teamId) {
        Tournament tournament = getTournamentById(tournamentId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new IllegalStateException("Cannot add teams to tournament not in PENDING status");
        }

        if (!tournament.getTeams().contains(team)) {
            tournament.getTeams().add(team);
            tournamentRepository.save(tournament);
        }
    }

    @Transactional
    public void removeTeamFromTournament(Long tournamentId, Long teamId) {
        Tournament tournament = getTournamentById(tournamentId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamId));

        if (tournament.getStatus() != TournamentStatus.PENDING) {
            throw new IllegalStateException("Cannot remove teams from tournament not in PENDING status");
        }

        tournament.getTeams().remove(team);
        tournamentRepository.save(tournament);
    }

    public List<Team> getTeamsByTournament(Long tournamentId) {
        Tournament tournament = getTournamentById(tournamentId);
        return tournament.getTeams();
    }

    @Transactional
    public void updateTournamentStatus(Long id, String status) {
        Tournament tournament = getTournamentById(id);
        tournament.setStatus(TournamentStatus.valueOf(status));
        tournamentRepository.save(tournament);
        System.out.println("Tournament " + id + " status updated to " + status);
    }
}