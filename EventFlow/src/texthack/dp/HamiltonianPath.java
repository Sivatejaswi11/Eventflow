package texthack.dp;

/**
 * MODULE 3 / CO3 - Hamiltonian path detection with bitmask DP.
 *
 * A Hamiltonian path visits every vertex exactly once (it does not need to
 * return to the start, that would be a Hamiltonian cycle).
 *
 *   State      : dp[mask][last] = true if there is a path that visits exactly
 *                the vertices in mask and ends at vertex 'last'
 *   Base case  : dp[1 << v][v] = true for every vertex v
 *   Recurrence : if dp[mask][last] and graph[last][next] == 1 and next is not
 *                in mask, then dp[mask | (1<<next)][next] = true
 *   Answer     : true if dp[fullMask][v] is true for any v
 *
 * EventFlow use: checking whether a schedule exists that covers every
 * activity exactly once given the allowed transitions between them.
 *
 * Time  : O(2^n * n^2)
 * Space : O(2^n * n)
 */
public class HamiltonianPath {

    /** graph is an adjacency matrix: graph[i][j] == 1 means edge i -> j. */
    public static boolean exists(int[][] graph) {

        int n = graph.length;

        if (n == 0) {
            return false;
        }
        if (n == 1) {
            return true;
        }

        int fullMask = (1 << n) - 1;

        boolean[][] dp = new boolean[1 << n][n];

        for (int v = 0; v < n; v++) {
            dp[1 << v][v] = true;       // a path can start anywhere
        }

        for (int mask = 1; mask <= fullMask; mask++) {

            for (int last = 0; last < n; last++) {

                if (!dp[mask][last]) {
                    continue;
                }

                for (int next = 0; next < n; next++) {

                    if ((mask & (1 << next)) != 0) {
                        continue;
                    }
                    if (graph[last][next] == 0) {
                        continue;
                    }
                    dp[mask | (1 << next)][next] = true;
                }
            }
        }

        for (int last = 0; last < n; last++) {
            if (dp[fullMask][last]) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {

        int[][] graph = {
                { 0, 1, 0, 1 },
                { 1, 0, 1, 1 },
                { 0, 1, 0, 1 },
                { 1, 1, 1, 0 }
        };
        System.out.println(exists(graph));   // true
    }
}
