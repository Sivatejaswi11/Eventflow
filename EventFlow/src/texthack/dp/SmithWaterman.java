package texthack.dp;

/**
 * MODULE 3 / CO3 - Smith-Waterman LOCAL sequence alignment.
 *
 * Local means: we look for the best matching REGION, not a full alignment.
 * The only change from Needleman-Wunsch is the extra 0 in the recurrence -
 * a negative running score is reset, so an alignment can start anywhere.
 *
 *   dp[i][j] = max(0,
 *                  dp[i-1][j-1] + (match or mismatch),
 *                  dp[i-1][j]   + gap,
 *                  dp[i][j-1]   + gap)
 *
 *   Base cases : first row and first column are 0
 *   Answer     : the MAXIMUM value anywhere in the table (not the corner)
 *
 * EventFlow use: finding a shared block of activities between two otherwise
 * different event schedules.
 *
 * Time  : O(n * m)
 * Space : O(n * m)
 */
public class SmithWaterman {

    public static int align(String a, String b, int match, int mismatch, int gap) {

        int n = a.length();
        int m = b.length();

        int[][] dp = new int[n + 1][m + 1];

        int bestScore = 0;

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

                int best = 0;                 // the local-alignment reset
                if (diagonalScore > best) {
                    best = diagonalScore;
                }
                if (up > best) {
                    best = up;
                }
                if (left > best) {
                    best = left;
                }

                dp[i][j] = best;

                if (best > bestScore) {
                    bestScore = best;
                }
            }
        }
        return bestScore;
    }

    public static void main(String[] args) {
        System.out.println(align("GGTTGACTA", "TGTTACGG", 1, -1, -2));
    }
}
