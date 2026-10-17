package texthack.dp;

/**
 * MODULE 3 / CO3 - Optimal Binary Search Tree (interval DP).
 *
 * Input: sorted keys and how often each key is searched.
 * Goal : build the BST with the minimum expected search cost, where a key at
 *        depth d (root = depth 1) costs frequency * d.
 *
 *   State      : dp[i][j] = best cost for keys i..j
 *   Base case  : dp[i][i] = frequency[i]
 *   Recurrence : dp[i][j] = min over r in [i..j] of
 *                dp[i][r-1] + dp[r+1][j] + sum(frequency[i..j])
 *
 *   The sum is added because making every key one level deeper costs one more
 *   comparison for each of its searches.
 *
 * The standard O(n^3) version is used here for clarity. Knuth's optimisation
 * restricts the choice of root to a narrower range and brings it down to
 * O(n^2), but understanding comes first.
 *
 * EventFlow use: arranging frequently searched event information (popular
 * event names, top venues) so that common lookups are fastest.
 *
 * Time  : O(n^3)
 * Space : O(n^2)
 */
public class OptimalBST {

    public static int minimumCost(int[] keys, int[] frequency) {

        int n = keys.length;

        if (n == 0) {
            return 0;
        }

        int[][] dp = new int[n][n];

        for (int i = 0; i < n; i++) {
            dp[i][i] = frequency[i];
        }

        for (int chainLength = 2; chainLength <= n; chainLength++) {

            for (int i = 0; i + chainLength - 1 < n; i++) {

                int j = i + chainLength - 1;

                int total = sum(frequency, i, j);

                dp[i][j] = Integer.MAX_VALUE;

                for (int root = i; root <= j; root++) {

                    int leftCost = (root > i) ? dp[i][root - 1] : 0;
                    int rightCost = (root < j) ? dp[root + 1][j] : 0;

                    int cost = leftCost + rightCost + total;

                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                    }
                }
            }
        }
        return dp[0][n - 1];
    }

    private static int sum(int[] frequency, int from, int to) {

        int total = 0;
        for (int i = from; i <= to; i++) {
            total = total + frequency[i];
        }
        return total;
    }

    public static void main(String[] args) {
        int[] keys = { 10, 12, 20 };
        int[] frequency = { 34, 8, 50 };
        System.out.println(minimumCost(keys, frequency));   // 142
    }
}
