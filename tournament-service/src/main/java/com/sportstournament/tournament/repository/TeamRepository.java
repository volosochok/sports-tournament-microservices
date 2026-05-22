package com.sportstournament.tournament.repository;

import com.sportstournament.tournament.model.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByCity(String city);
    boolean existsByName(String name);
    List<Team> findByIdIn(List<Long> ids);
}