package com.example.ticketcard.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static List<List<Match>> generateDoubleRoundRobinFixtures(List<Team> teams) {
        List<List<Match>> fixtures = new ArrayList<>();
        int numTeams = teams.size();

        // Check if the number of teams is odd
        boolean isOdd = numTeams % 2 != 0;

        // Add a dummy team if the number of teams is odd
        if (isOdd) {
            teams.add(new Team("BYE", "", ""));
            numTeams++;
        }

        int rounds = (numTeams - 1) * 2; // Double round robin
        int halfSize = numTeams / 2;

        List<Team> teamsCopy = new ArrayList<>(teams);
        teamsCopy.remove(0);

        int teamsSize = teamsCopy.size();

        // First half fixtures
        for (int round = 0; round < rounds / 2; round++) {
            List<Match> roundFixtures = new ArrayList<>();
            int teamIdx = round % teamsSize;

            // Match for the first team against the rotating team
            roundFixtures.add(new Match(
                    teams.get(0).getTeamName(),
                    teams.get(0).getTeamLogoUrl(),
                    teamsCopy.get(teamIdx).getTeamName(),
                    teamsCopy.get(teamIdx).getTeamLogoUrl(),
                    teams.get(0).getHomeStadium(), // Team A's home stadium
                    "", 0, "", "", 0, 0));

            // Other matches in the round
            for (int idx = 1; idx < halfSize; idx++) {
                int firstTeam = (round + idx) % teamsSize;
                int secondTeam = (round + teamsSize - idx) % teamsSize;
                roundFixtures.add(new Match(
                        teamsCopy.get(firstTeam).getTeamName(),
                        teamsCopy.get(firstTeam).getTeamLogoUrl(),
                        teamsCopy.get(secondTeam).getTeamName(),
                        teamsCopy.get(secondTeam).getTeamLogoUrl(),
                        teamsCopy.get(firstTeam).getHomeStadium(), // Team A's home stadium
                        "", 0, "", "", 0, 0));
            }

            fixtures.add(roundFixtures);
        }

        // Second half fixtures (reverse fixtures)
        for (int round = 0; round < rounds / 2; round++) {
            List<Match> reversedRound = new ArrayList<>();
            for (int i = 0; i < fixtures.get(round).size(); i++) {
                Match match = fixtures.get(round).get(i);
                Team teamA = teams.stream().filter(t -> t.getTeamName().equals(match.getTeamA())).findFirst().orElse(null);
                Team teamB = teams.stream().filter(t -> t.getTeamName().equals(match.getTeamB())).findFirst().orElse(null);

                if (teamA != null && teamB != null) {
                    reversedRound.add(new Match(
                            match.getTeamB(),
                            match.getTeamBLogoUrl(),
                            match.getTeamA(),
                            match.getTeamALogoUrl(),
                            teamB.getHomeStadium(), // Team B's home stadium in the second half
                            "", 0, "", "", 0, 0));
                }
            }
            fixtures.add(reversedRound);
        }

        // Remove the fixtures that involve the dummy "BYE" team
        if (isOdd) {
            fixtures.removeIf(roundFixtures -> roundFixtures.removeIf(match -> match.getTeamA().equals("BYE") || match.getTeamB().equals("BYE")));
        }

        return fixtures;
    }

    public static String getNextValidDate(Calendar calendar) {
        // Ensure that the matches are not scheduled on Sundays (can be customized)
        while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = calendar.get(Calendar.YEAR);

        return String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month, year);
    }

    public static String sanitizeKey(String key) {
        return key.replace(".", "_")
                .replace("#", "_")
                .replace("$", "_")
                .replace("[", "_")
                .replace("]", "_");
    }

}