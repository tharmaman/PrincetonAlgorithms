import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private int nTeams;                                                    // number of teams
    private LinearProbingHashST<String, Integer> teamIDs;                  // look up ranking of teams by name
    private LinearProbingHashST<Integer, String> teamNames;
    private int[] win;
    private int[] lose;
    private int[] remain;
    private int[][]  versus;
    private boolean[] isEliminated;
    private boolean[] isTrivial;                                           // in order to not waste time on doing FF during the loop
    private FordFulkerson maxFlow;                                         // Ford-Fulkerson algorithm for solving max flow problem
    private LinearProbingHashST<String, Bag<String>> elimSets;             // cache eliminations

    /**
     * #DONE
     * create a baseball division from a given filename
     * @param filename
     */
    public BaseballElimination(String filename) {

        // handling corner cases
        if (filename == null) {
            throw new IllegalArgumentException("Cannot accept null");
        }

        // storing file as input
        In in = new In(filename);

        // reading teams variable
        nTeams = in.readInt();

        // initialize instance variables;
        teamIDs = new LinearProbingHashST<>();
        teamNames = new LinearProbingHashST<>();
        win = new int[nTeams];
        lose = new int[nTeams];
        remain = new int[nTeams];
        versus = new int[nTeams][nTeams];
        isEliminated = new boolean[nTeams];
        isTrivial = new boolean[nTeams];
        elimSets = new LinearProbingHashST<>();

        // loop through number of teams
        for (int i = 0; i < nTeams; i++) {

            // store data
            String saveTeam = in.readString();
            int saveWin = in.readInt();
            int saveLoss = in.readInt();
            int saveRemain = in.readInt();

            // add to data structures
            teamNames.put(i, saveTeam);
            teamIDs.put(saveTeam, i);
            win[i] = saveWin;
            lose[i] = saveLoss;
            remain[i] = saveRemain;

            // loop through versus
            for (int j = 0; j < nTeams; j++) {
                versus[i][j] = in.readInt();
            }

            // initialize empty bags for each team
            elimSets.put(saveTeam, new Bag<>());
        }

        // run trivial elimination
        runTrivialElimination();

        // run non trivial elimination
        runNonTrivialElimination();
    }

    /**
     * #DONE
     * @return number of teams
     */
    public int numberOfTeams() {
        return nTeams;
    }

    /**
     * #DONE
     * @return an iterable of all teams
     */
    public Iterable<String> teams() {
        return teamIDs.keys();
    }

    /**
     * #DONE
     * @param team to check
     * @return number of wins for a given team
     */
    public int wins(String team) {

        if (team == null) {
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }
        if (!teamIDs.contains(team)) {
            throw new IllegalArgumentException("Team not found");
        }

        return win[teamIDs.get(team)];
    }

    /**
     * #DONE
     * @param team to check
     * @return number of losses for a given team
     */
    public int losses(String team) {

        if (team == null) {
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }
        if (!teamIDs.contains(team)) {
            throw new IllegalArgumentException("Team not found");
        }

        return lose[teamIDs.get(team)];
    }

    /**
     * #DONE
     * @param team to check
     * @return number of remaining games for a given team
     */
    public int remaining(String team) {

        if (team == null) {
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }
        if (!teamIDs.contains(team)) {
            throw new IllegalArgumentException("Team not found");
        }

        return remain[teamIDs.get(team)];
    }

    /**
     * #DONE
     * @param team1 to base
     * @param team2 to check against
     * @return number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {

        // handling corner cases
        if (team1 == null) {
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }
        if (team2 == null) {
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }
        if (!teamIDs.contains(team1)) {
            throw new IllegalArgumentException("Team1 not found");
        }
        if (!teamIDs.contains(team2)) {
            throw new IllegalArgumentException("Team2 not found");
        }

        return versus[teamIDs.get(team1)][teamIDs.get(team2)];
    }

    /**
     * #DONE
     * @param team to check
     * @return is given team eliminated?
     */
    public boolean isEliminated(String team) {

        // handling corner cases
        if (team == null) {
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }
        if (!teamIDs.contains(team)) {
            throw new IllegalArgumentException("Team not found");
        }

        return isEliminated[teamIDs.get(team)];
    }

    /**
     * #DONE
     * @param team to eliminate
     * @return a subset R of teams that eliminates given team; null if not eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {

        // handling corner cases
        if (team == null) {
            throw new IllegalArgumentException("Cannot accept null as an argument");
        }

        // if not eliminated
        if (!isEliminated(team)){
            return null;
        }

        // grab from bags
        return elimSets.get(team);
    }

    /***************************************************************
     *                      HELPER FUNCTIONS
     ***************************************************************/

    /**
     * #DONE
     * check if trivial elimination is possible for all teams
     */
    private void runTrivialElimination() {

        // run for trivial elimination
        // loop through each team

        for (int i = 0; i < nTeams; i++) {

            int bullishWins = win[i] + remain[i];
            String currentTeam = teamNames.get(i);

            // check if bullish wins is less than
            // current wins of any other team
            for (int j = 0; j < nTeams; j++) {


                if (j != i && bullishWins < win[j]) {

                    // eliminate team
                    isEliminated[i] = true;
                    isTrivial[i] = true;
                    String eliminatingTeam = teamNames.get(j);


                    // add the j eliminator to the bag
                    elimSets.get(currentTeam).add(eliminatingTeam);
                }
            }
        }
    }

    /**
     * #FIXME
     * check if non-trivial elimination is possible
     */
    private void runNonTrivialElimination () {

        // initialize flow network
        // count number of vertices
        int teamVTotal = (nTeams - 1);
        int gameVTotal = teamVTotal * (teamVTotal - 1) / 2 ;
        int totalV = teamVTotal + gameVTotal + 2;                        // include 2 for s and t

        // count number of edges
        int totalE = gameVTotal + (2 * gameVTotal) + teamVTotal;         // left edges, middle edges, right edges

        // loop teams to perform max flow on them
        for (int currTeam = 0; currTeam < nTeams; currTeam++) {

            // if team was already trivially eliminated, skip
            if (isTrivial[currTeam]) {
                continue;
            }

            // otherwise initialize flow network
            FlowNetwork Flow = new FlowNetwork(totalV);

            // initialize source and sink variables
            int s = 0;
            int t = 1 + gameVTotal + teamVTotal;

            // adding a check before proceeding
            assert t == Flow.V() - 1 && t == totalV - 1 ;

            // store team vertices
            int teamV = gameVTotal + 1;                    // account for games + source vertices

            // create hash table to map teams to vertices
            LinearProbingHashST<Integer, Integer>  teamToV = new LinearProbingHashST<>();

            // loop through other teams and add to hash table
            for (int otherTeam = 0; otherTeam < nTeams; otherTeam++) {

                // if current team then skip
                if (otherTeam == currTeam) {
                    continue;
                }

                // otherwise add to hash table
                teamToV.put(otherTeam, teamV);
                teamV++;
            }

            // adding a check before proceeding
            assert teamV == t - 1;

            // connect game vertices
            int gameV = 1;                      // account for source vertex at 0

            // loop through i
            for (int i = 0; i < teamVTotal; i++) {

                // if i is the same as team, then skip
                if (i == currTeam) {
                    continue;
                }

                // loop through k (j is i + 1 in order to prevent duplicate game vertices)
                for (int j = i + 1; j < teamVTotal; j++) {

                    // if i is the same as team, then skip
                    if (j == currTeam) {
                        continue;
                    }

                    // create edges
                    // source to game
                    FlowEdge StoG = new FlowEdge(s,gameV, versus[i][j]);

                    // game to team i
                    FlowEdge GtoI = new FlowEdge(gameV, teamToV.get(i), Double.POSITIVE_INFINITY);

                    // game to team j
                    FlowEdge GtoJ = new FlowEdge(gameV, teamToV.get(j), Double.POSITIVE_INFINITY);

                    // add edges to graph
                    Flow.addEdge(StoG);
                    Flow.addEdge(GtoI);
                    Flow.addEdge(GtoJ);

                    // increment game vertex
                    gameV++;
                }
            }

            // adding a check before proceeding
            assert gameV == gameVTotal + 1;

            // connect team vertices to sink
            for (int otherTeam = 0; otherTeam < nTeams; otherTeam++) {

                // skip current team
                if (otherTeam == currTeam) {
                    continue;
                }

                // know if there is someway of completing all the games
                // so that currTeam ends up winning at least as many games
                // as otherTeam
                // otherTeam can win as many as w[otherTeam] + r[otherTeam] games
                // check if no team wins more games than currTeam
                // capacity from otherTeam to Sink t is
                // w[otherTeam] + r[otherTeam] - w[currTeam]
                int capacity = win[currTeam] + remain[currTeam] - win[otherTeam];

                // create & add edge from Team to sink t
                FlowEdge TeamToT = new FlowEdge(teamToV.get(otherTeam), t, capacity);
                Flow.addEdge(TeamToT);
            }

            // apply max flow algorithm to solve problem
            maxFlow = new FordFulkerson(Flow, s, t);

            // if ANY edges pointing from s are not full
            // then there is no scenario in which currTeam can win

            // loop through edges adjacent to source s
            for (FlowEdge edge : Flow.adj(s)) {

                // check if edges are full
                if (edge.flow() != edge.capacity()) {
                    isEliminated[currTeam] = true;
                    break;
                }
            }

            // if team is eliminated then add inCut vertices to elimination set
            if (isEliminated[currTeam]) {

                // add teams to subset
                for (int otherTeam = 0; otherTeam < nTeams; otherTeam++) {
                    if (otherTeam == currTeam) {
                        continue;
                    }

                    // test to see if vertex is inCut
                    if (maxFlow.inCut(teamToV.get(otherTeam))) {

                        String currTeamName = teamNames.get(currTeam);
                        String otherTeamName = teamNames.get(otherTeam);

                        elimSets.get(currTeamName).add(otherTeamName);
                    }

                }

            }

        }
    }

    // for unit testing
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
