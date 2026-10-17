package texthack.dp;

/**
 * MODULE 3 / CO3 - Matrix-Chain Multiplication (interval DP).
 *
 * Input: dimensions[] of length n+1 describing n matrices, where
 *        matrix i has size dimensions[i-1] x dimensions[i].
 *
 * Example: dimensions = {10, 30, 5, 60}
 *          A = 10x30, B = 30x5, C = 5x60
 *          (AB)C = 10*30*5 + 10*5*60 = 1500 + 3000 = 4500   <- best
 *          A(BC) = 30*5*60 + 10*30*60 = 9000 + 18000 = 27000
 *
 *   State      : dp[i][j] = minimum multiplications to multiply matrices i..j
 *   Base case  : dp[i][i] = 0 (a single matrix needs no work)
 *   Recurrence : dp[i][j] = min over k of
 *                dp[i][k] + dp[k+1][j] + d[i-1]*d[k]*d[j]
 *
 * EventFlow use: choosing the cheapest order for a chain of data-processing
 * / report-generation operations.
 *
 * Time  : O(n^3)
 * Space : O(n^2)
 */
public class MatrixChainMultiplication {

    public static int minimumCost(int[] dimensions) {

        int n = dimensions.length - 1;      // number of matrices

        if (n < 1) {
            return 0;
        }

        int[][] dp = new int[n + 1][n + 1];

        for (int i = 1; i <= n; i++) {
            dp[i][i] = 0;
        }

        // chainLength = how many matrices are in the current interval
        for (int chainLength = 2; chainLength <= n; chainLength++) {

            for (int i = 1; i + chainLength - 1 <= n; i++) {

                int j = i + chainLength - 1;
                dp[i][j] = Integer.MAX_VALUE;

                for (int k = i; k < j; k++) {

                    int cost = dp[i][k] + dp[k + 1][j]
                            + dimensions[i - 1] * dimensions[k] * dimensions[j];

                    if (cost < dp[i][j]) {
                        dp[i][j] = cost;
                    }
                }
            }
        }
        return dp[1][n];
    }

    public static void main(String[] args) {
        int[] dimensions = { 10, 30, 5, 60 };
        System.out.println(minimumCost(dimensions));   // 4500
    }
}
