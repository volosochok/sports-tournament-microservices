package com.sportstournament.tournament.service;

import com.sportstournament.tournament.exception.DuplicateResourceException;
import com.sportstournament.tournament.exception.ResourceNotFoundException;
import com.sportstournament.tournament.model.dto.TeamRequestDTO;
import com.sportstournament.tournament.model.entity.Team;
import com.sportstournament.tournament.model.enums.TournamentStatus;
import com.sportstournament.tournament.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
    }

    public Team createTeam(TeamRequestDTO request) {
        if (teamRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Team already exists: " + request.getName());
        }

        Team team = new Team(request.getName(), request.getCity(), request.getCoachName());
        return teamRepository.save(team);
    }

    @Transactional
    public Team updateTeam(Long id, TeamRequestDTO request) {
        Team team = getTeamById(id);

        if (!team.getName().equals(request.getName()) && teamRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Team already exists: " + request.getName());
        }

        team.setName(request.getName());
        team.setCity(request.getCity());
        team.setCoachName(request.getCoachName());

        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        Team team = getTeamById(id);

        boolean inActiveTournament = team.getTournaments().stream()
                .anyMatch(t -> t.getStatus() == TournamentStatus.PENDING || t.getStatus() == TournamentStatus.ONGOING);

        if (inActiveTournament) {
            throw new IllegalStateException("Cannot delete team participating in active tournament");
        }

        teamRepository.delete(team);
    }
}