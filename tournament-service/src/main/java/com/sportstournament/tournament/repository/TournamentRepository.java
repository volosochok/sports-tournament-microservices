package com.sportstournament.tournament.repository;

import com.sportstournament.tournament.model.entity.Tournament;
import com.sportstournament.tournament.model.enums.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByStatus(TournamentStatus status);
    boolean existsByName(String name);
}