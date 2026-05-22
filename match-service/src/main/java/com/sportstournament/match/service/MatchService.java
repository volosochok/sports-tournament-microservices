package com.sportstournament.match.service;

import com.sportstournament.match.client.TournamentServiceClient;
import com.sportstournament.match.exception.ResourceNotFoundException;
import com.sportstournament.match.model.dto.MatchResultDTO;
import com.sportstournament.match.model.dto.TeamDTO;
import com.sportstournament.match.model.dto.TournamentDTO;
import com.sportstournament.match.model.entity.Match;
import com.sportstournament.match.model.enums.MatchStatus;
import com.sportstournament.match.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final BracketService bracketService;
    private final TournamentServiceClient tournamentClient;

    public MatchService(MatchRepository matchRepository,
                        BracketService bracketService,
                        TournamentServiceClient tournamentClient) {
        this.matchRepository = matchRepository;
        this.bracketService = bracketService;
        this.tournamentClient = tournamentClient;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<Match> getMatchesByTournament(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    public List<Match> getMatchesByTeam(Long teamId) {
        return matchRepository.findByTeam1IdOrTeam2Id(teamId, teamId);
    }

    public Match getMatchById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + id));
    }

    @Transactional
    public void generateBracket(Long tournamentId) {
        System.out.println("=== generateBracket called for tournamentId: " + tournamentId);

        TournamentDTO tournament = tournamentClient.getTournamentById(tournamentId);
        List<TeamDTO> teams = tournamentClient.getTeamsByTournament(tournamentId);

        if (teams == null || teams.size() < 2) {
            throw new IllegalStateException("At least 2 teams are required to generate bracket");
        }

        matchRepository.deleteByTournamentId(tournamentId);

        List<Match> firstRound = bracketService.generateFirstRound(tournamentId, teams);

        for (Match match : firstRound) {
            matchRepository.save(match);
        }

        tournamentClient.updateTournamentStatus(tournamentId, "ONGOING");

        System.out.println("Generated " + firstRound.size() + " matches. Tournament status updated to ONGOING");
    }

    @Transactional
    public void recordMatchResult(Long matchId, MatchResultDTO result) {
        Match match = getMatchById(matchId);

        if (match.getStatus() == MatchStatus.FINISHED) {
            throw new IllegalStateException("Match is already finished");
        }

        if (result.getTeam1Score().equals(result.getTeam2Score())) {
            throw new IllegalArgumentException("Draw is not allowed in knockout tournament. Please enter a winner.");
        }

        match.setTeam1Score(result.getTeam1Score());
        match.setTeam2Score(result.getTeam2Score());
        match.setStatus(MatchStatus.FINISHED);

        if (result.getTeam1Score() > result.getTeam2Score()) {
            match.setWinnerId(match.getTeam1Id());
        } else if (result.getTeam2Score() > result.getTeam1Score()) {
            match.setWinnerId(match.getTeam2Id());
        }

        matchRepository.save(match);
        System.out.println("Match " + matchId + " recorded. Winner: " + match.getWinnerId());

        advanceTournament(match.getTournamentId());
    }

    @Transactional
    public void advanceTournament(Long tournamentId) {
        System.out.println("=== advanceTournament called for tournamentId: " + tournamentId);

        // Find the current round (the highest round that has any matches)
        List<Match> allMatches = matchRepository.findByTournamentId(tournamentId);
        if (allMatches.isEmpty()) {
            System.out.println("No matches found");
            return;
        }

        int maxRound = allMatches.stream()
                .mapToInt(Match::getRound)
                .max()
                .orElse(0);

        System.out.println("Max round: " + maxRound);

        // Get matches in the current round
        List<Match> currentRoundMatches = matchRepository.findByTournamentIdAndRound(tournamentId, maxRound);

        // Check if all matches in current round are finished
        boolean allFinished = currentRoundMatches.stream()
                .allMatch(m -> m.getStatus() == MatchStatus.FINISHED);

        if (!allFinished) {
            System.out.println("Round " + maxRound + " not finished yet. " +
                    currentRoundMatches.stream().filter(m -> m.getStatus() != MatchStatus.FINISHED).count() +
                    " matches remaining");
            return;
        }

        // Get winners from current round
        List<Long> winners = currentRoundMatches.stream()
                .filter(m -> m.getWinnerId() != null)
                .map(Match::getWinnerId)
                .toList();

        System.out.println("Round " + maxRound + " finished. Winners: " + winners);

        // If only one winner, tournament is over
        if (winners.size() <= 1) {
            tournamentClient.updateTournamentStatus(tournamentId, "FINISHED");
            System.out.println("🏆 TOURNAMENT FINISHED! Champion: Team " +
                    (winners.isEmpty() ? "none" : winners.get(0)));
            return;
        }

        // Create next round matches
        int nextRound = maxRound + 1;

        // Check if next round already exists
        List<Match> existingNextRound = matchRepository.findByTournamentIdAndRound(tournamentId, nextRound);
        if (!existingNextRound.isEmpty()) {
            System.out.println("Next round already exists");
            return;
        }

        // Create matches for next round
        List<Match> nextRoundMatches = new ArrayList<>();
        for (int i = 0; i < winners.size(); i += 2) {
            if (i + 1 < winners.size()) {
                Match match = new Match(nextRound, winners.get(i), winners.get(i + 1), tournamentId);
                nextRoundMatches.add(match);
                System.out.println("Created match for round " + nextRound + ": Team " +
                        winners.get(i) + " vs Team " + winners.get(i + 1));
            }
        }

        // Save next round matches
        for (Match match : nextRoundMatches) {
            matchRepository.save(match);
        }

        System.out.println("Round " + nextRound + " created with " + nextRoundMatches.size() + " matches");
    }

    @Transactional
    public void processTournamentMatches(Long tournamentId) {
        System.out.println("=== processTournamentMatches for tournamentId: " + tournamentId);

        // Отримуємо всі раунди турніру
        List<Integer> rounds = matchRepository.findByTournamentId(tournamentId).stream()
                .map(Match::getRound)
                .distinct()
                .sorted()
                .toList();

        if (rounds.isEmpty()) {
            System.out.println("No rounds found");
            return;
        }

        // Починаємо з першого раунду
        int currentRound = 1;
        boolean tournamentFinished = false;

        while (!tournamentFinished) {
            List<Match> currentRoundMatches = matchRepository.findByTournamentIdAndRound(tournamentId, currentRound);

            // Перевіряємо, чи всі матчі поточного раунду завершені
            boolean allFinished = currentRoundMatches.stream()
                    .allMatch(m -> m.getStatus() == MatchStatus.FINISHED);

            if (!allFinished) {
                System.out.println("Round " + currentRound + " not finished yet. Waiting for more results...");
                break;
            }

            // Отримуємо переможців поточного раунду
            List<Long> winners = currentRoundMatches.stream()
                    .filter(m -> m.getWinnerId() != null)
                    .map(Match::getWinnerId)
                    .toList();

            System.out.println("Round " + currentRound + " complete. Winners: " + winners);

            // Якщо тільки один переможець - турнір завершено
            if (winners.size() <= 1) {
                tournamentClient.updateTournamentStatus(tournamentId, "FINISHED");
                System.out.println("Tournament " + tournamentId + " finished! Champion: Team " +
                        (winners.isEmpty() ? "none" : winners.get(0)));
                tournamentFinished = true;
                break;
            }

            // Генеруємо наступний раунд
            int nextRound = currentRound + 1;
            List<Match> existingNextRound = matchRepository.findByTournamentIdAndRound(tournamentId, nextRound);

            // Якщо наступний раунд вже існує, перевіряємо чи потрібно його оновлювати
            if (!existingNextRound.isEmpty()) {
                System.out.println("Round " + nextRound + " already exists");
                currentRound = nextRound;
                continue;
            }

            // Створюємо матчі наступного раунду
            List<Match> nextRoundMatches = new ArrayList<>();
            for (int i = 0; i < winners.size(); i += 2) {
                if (i + 1 < winners.size()) {
                    Match match = new Match(nextRound, winners.get(i), winners.get(i + 1), tournamentId);
                    nextRoundMatches.add(match);
                    System.out.println("Created match for round " + nextRound + ": Team " +
                            winners.get(i) + " vs Team " + winners.get(i + 1));
                }
            }

            // Зберігаємо матчі наступного раунду
            for (Match match : nextRoundMatches) {
                matchRepository.save(match);
            }

            // Переходимо до наступного раунду
            currentRound = nextRound;
        }
    }
}