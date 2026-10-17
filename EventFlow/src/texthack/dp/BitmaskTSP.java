package texthack.dp;

/**
 * MODULE 3 / CO3 - Travelling Salesman Problem with bitmask DP.
 *
 *   A mask is an integer whose bits say which cities have been visited.
 *   Bit i of mask = 1 means city i is already visited.
 *
 *   State      : dp[mask][current] = cheapest way to start at city 0,
 *                visit exactly the cities in mask, and stand at 'current'
 *   Base case  : dp[1][0] = 0  (only city 0 visited, standing at city 0)
 *   Recurrence : dp[mask | (1<<next)][next] =
 *                   min(that, dp[mask][current] + distance[current][next])
 *   Answer     : min over last of dp[fullMask][last] + distance[last][0]
 *
 * EventFlow use: planning the cheapest route between venues, or choosing the
 * order of a small set of activities.
 *
 * Time  : O(2^n * n^2)
 * Space : O(2^n * n)
 */
public class BitmaskTSP {

    private static final int INFINITY = 100000000;

    /** Returns the minimum cost of a tour that starts and ends at city 0. */
    public static int solve(int[][] distance) {

        int n = distance.length;

        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 0;
        }

        int fullMask = (1 << n) - 1;

        int[][] dp = new int[1 << n][n];

        for (int mask = 0; mask <= fullMask; mask++) {
            for (int city = 0; city < n; city++) {
                dp[mask][city] = INFINITY;
            }
        }

        dp[1][0] = 0;      // start at city 0

        for (int mask = 1; mask <= fullMask; mask++) {

            for (int current = 0; current < n; current++) {

                if ((mask & (1 << current)) == 0) {
                    continue;                     // current is not in the mask
                }
                if (dp[mask][current] == INFINITY) {
                    continue;                     // state not reachable
                }

                for (int next = 0; next < n; next++) {

                    if ((mask & (1 << next)) != 0) {
                        continue;                 // already visited
                    }

                    int newMask = mask | (1 << next);
                    int candidate = dp[mask][current] + distance[current][next];

                    if (candidate < dp[newMask][next]) {
                        dp[newMask][next] = candidate;
                    }
                }
            }
        }

        int answer = INFINITY;

        for (int last = 0; last < n; last++) {

            if (dp[fullMask][last] == INFINITY) {
                continue;
            }
            int total = dp[fullMask][last] + distance[last][0];

            if (total < answer) {
                answer = total;
            }
        }
        return answer;
    }

    public static void main(String[] args) {

        int[][] distance = {
                { 0, 10, 15, 20 },
                { 10, 0, 35, 25 },
                { 15, 35, 0, 30 },
                { 20, 25, 30, 0 }
        };
        System.out.println(solve(distance));   // 80
    }
}
