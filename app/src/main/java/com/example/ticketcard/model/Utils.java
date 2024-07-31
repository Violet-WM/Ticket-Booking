package com.example.ticketcard.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

        for (int round = 0; round < rounds / 2; round++) {
            List<Match> roundFixtures = new ArrayList<>();

            int teamIdx = round % teamsSize;

            roundFixtures.add(new Match(teamsCopy.get(teamIdx).getTeamName(),
                    teamsCopy.get(teamIdx).getTeamLogoUrl(),
                    teams.get(0).getTeamName(),
                    teams.get(0).getTeamLogoUrl(),
                    "", "", 0, "", "", 0, 0));

            for (int idx = 1; idx < halfSize; idx++) {
                int firstTeam = (round + idx) % teamsSize;
                int secondTeam = (round + teamsSize - idx) % teamsSize;
                roundFixtures.add(new Match(teamsCopy.get(firstTeam).getTeamName(),
                        teamsCopy.get(firstTeam).getTeamLogoUrl(),
                        teamsCopy.get(secondTeam).getTeamName(),
                        teamsCopy.get(secondTeam).getTeamLogoUrl(),
                        "", "", 0, "", "", 0, 0));
            }

            fixtures.add(roundFixtures);
        }

        // Add reverse fixtures for the second half
        List<List<Match>> reverseFixtures = new ArrayList<>();
        for (List<Match> rFixtures : fixtures) {
            List<Match> reversedRound = new ArrayList<>();
            for (Match match : rFixtures) {
                reversedRound.add(new Match(match.getTeamB(), match.getTeamBLogoUrl(), match.getTeamA(), match.getTeamALogoUrl(), "", "", 0, "", "", 0, 0));
            }
            reverseFixtures.add(reversedRound);
        }
        fixtures.addAll(reverseFixtures);

        // Remove the fixtures that involve the dummy "BYE" team
        if (isOdd) {
            fixtures.removeIf(roundFixtures -> roundFixtures.removeIf(match -> match.getTeamA().equals("BYE") || match.getTeamB().equals("BYE")));
        }

        return fixtures;
    }

    public static String getNextValidDate(Calendar calendar) {
        while (true) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            if ((month == Calendar.DECEMBER && day == 25) || (month == Calendar.JANUARY && day == 1) || dayOfWeek == Calendar.SUNDAY) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                return calendar.getTime().toString();
            }
        }
    }
}
