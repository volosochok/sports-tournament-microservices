package com.sportstournament.match.repository;

import com.sportstournament.match.model.entity.Match;
import com.sportstournament.match.model.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTournamentId(Long tournamentId);

    List<Match> findByTournamentIdAndRound(Long tournamentId, int round);

    List<Match> findByTournamentIdAndStatus(Long tournamentId, MatchStatus status);

    List<Match> findByTeam1IdOrTeam2Id(Long team1Id, Long team2Id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Match m WHERE m.tournamentId = :tournamentId")
    void deleteByTournamentId(@Param("tournamentId") Long tournamentId);

    long countByTournamentIdAndStatus(Long tournamentId, MatchStatus status);
}