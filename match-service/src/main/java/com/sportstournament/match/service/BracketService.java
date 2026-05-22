package com.sportstournament.match.service;

import com.sportstournament.match.model.dto.TeamDTO;
import com.sportstournament.match.model.entity.Match;
import com.sportstournament.match.model.enums.MatchStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BracketService {

    public List<Match> generateFirstRound(Long tournamentId, List<TeamDTO> teams) {
        List<Match> matches = new ArrayList<>();

        if (teams == null || teams.size() < 2) {
            return matches;
        }

        List<TeamDTO> shuffledTeams = new ArrayList<>(teams);
        Collections.shuffle(shuffledTeams);

        int teamCount = shuffledTeams.size();
        int optimalSize = nextPowerOfTwo(teamCount);
        int byes = optimalSize - teamCount;

        System.out.println("Tournament setup: " + teamCount + " teams, " + byes + " byes");

        List<Long> byeTeams = new ArrayList<>();
        List<TeamDTO> teamsToPlay = new ArrayList<>(shuffledTeams);

        if (byes > 0) {
            Collections.shuffle(teamsToPlay);
            for (int i = 0; i < byes; i++) {
                byeTeams.add(teamsToPlay.remove(0).getId());
            }
            System.out.println("Bye teams: " + byeTeams);
        }

        int round = 1;
        for (int i = 0; i < teamsToPlay.size(); i += 2) {
            if (i + 1 < teamsToPlay.size()) {
                Match match = new Match(round, teamsToPlay.get(i).getId(), teamsToPlay.get(i + 1).getId(), tournamentId);
                matches.add(match);
                System.out.println("Created match: Team " + teamsToPlay.get(i).getId() + " vs Team " + teamsToPlay.get(i + 1).getId());
            }
        }

        for (Long byeTeamId : byeTeams) {
            Match byeMatch = new Match(round, byeTeamId, null, tournamentId);
            byeMatch.setStatus(MatchStatus.FINISHED);
            byeMatch.setWinnerId(byeTeamId);
            byeMatch.setTeam1Score(1);
            byeMatch.setTeam2Score(0);
            matches.add(byeMatch);
            System.out.println("Bye match for team: " + byeTeamId);
        }

        System.out.println("Generated " + matches.size() + " matches for round " + round);
        return matches;
    }

    public List<Match> generateNextRound(Long tournamentId, List<Match> currentRoundMatches, int currentRound) {
        List<Match> nextRoundMatches = new ArrayList<>();

        List<Long> winners = new ArrayList<>();
        for (Match match : currentRoundMatches) {
            if (match.getStatus() == MatchStatus.FINISHED && match.getWinnerId() != null) {
                winners.add(match.getWinnerId());
            } else {
                return null;
            }
        }

        if (winners.size() <= 1) {
            return nextRoundMatches;
        }

        int nextRound = currentRound + 1;
        for (int i = 0; i < winners.size(); i += 2) {
            if (i + 1 < winners.size()) {
                Match match = new Match(nextRound, winners.get(i), winners.get(i + 1), tournamentId);
                nextRoundMatches.add(match);
                System.out.println("Created round " + nextRound + " match: Team " + winners.get(i) + " vs Team " + winners.get(i + 1));
            }
        }

        return nextRoundMatches;
    }

    public int calculateTotalRounds(int teamCount) {
        return (int) Math.ceil(Math.log(teamCount) / Math.log(2));
    }

    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) {
            power <<= 1;
        }
        return power;
    }
}