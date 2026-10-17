package texthack.dp;

/**
 * MODULE 3 / CO3 - Needleman-Wunsch GLOBAL sequence alignment.
 *
 * Global means: the WHOLE of sequence a is aligned against the WHOLE of
 * sequence b, inserting gaps where needed.
 *
 *   State      : dp[i][j] = best score aligning a[0..i-1] with b[0..j-1]
 *   Base cases : dp[i][0] = i * gap, dp[0][j] = j * gap
 *   Recurrence : dp[i][j] = max(dp[i-1][j-1] + (match or mismatch),
 *                               dp[i-1][j]   + gap,
 *                               dp[i][j-1]   + gap)
 *   Answer     : dp[n][m]  (bottom-right corner)
 *
 * Typical scores: match = 1, mismatch = -1, gap = -2.
 *
 * EventFlow use: comparing two complete event schedules / activity sequences
 * from start to finish.
 *
 * Time  : O(n * m)
 * Space : O(n * m)
 */
public class NeedlemanWunsch {

    public static int align(String a, String b, int match, int mismatch, int gap) {

        int n = a.length();
        int m = b.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * gap;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * gap;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                int diagonalScore;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    diagonalScore = dp[i - 1][j - 1] + match;
                } else {
                    diagonalScore = dp[i - 1][j - 1] + mismatch;
                }

                int up = dp[i - 1][j] + gap;
                int left = dp[i][j - 1] + gap;

                int best = diagonalScore;
                if (up > best) {
                    best = up;
                }
                if (left > best) {
                    best = left;
                }
                dp[i][j] = best;
            }
        }
        return dp[n][m];
    }

    public static void main(String[] args) {
        System.out.println(align("GATTACA", "GCATGCU", 1, -1, -2));
    }
}
