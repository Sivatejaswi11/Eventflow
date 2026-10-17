package texthack.dp;

/**
 * MODULE 3 / CO3 - Weighted edit distance.
 *
 * Same DP as Levenshtein, but the three operations no longer cost the same:
 *
 *   INSERT_COST       = 1
 *   DELETE_COST       = 1
 *   SUBSTITUTION_COST = 2
 *
 * With substitution costing 2, a replace is never cheaper than a delete plus
 * an insert, so the algorithm prefers to rewrite rather than swap characters.
 *
 * EventFlow use: correcting registration codes, where changing a character is
 * a more serious error than a missing or extra character.
 *
 * Time  : O(n * m)
 * Space : O(n * m)
 */
public class WeightedEditDistance {

    public static final int INSERT_COST = 1;
    public static final int DELETE_COST = 1;
    public static final int SUBSTITUTION_COST = 2;

    public static int distance(String a, String b) {

        int n = a.length();
        int m = b.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i * DELETE_COST;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j * INSERT_COST;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                if (a.charAt(i - 1) == b.charAt(j - 1)) {

                    dp[i][j] = dp[i - 1][j - 1];

                } else {

                    int delete = dp[i - 1][j] + DELETE_COST;
                    int insert = dp[i][j - 1] + INSERT_COST;
                    int substitute = dp[i - 1][j - 1] + SUBSTITUTION_COST;

                    int best = delete;
                    if (insert < best) {
                        best = insert;
                    }
                    if (substitute < best) {
                        best = substitute;
                    }
                    dp[i][j] = best;
                }
            }
        }
        return dp[n][m];
    }

    public static void main(String[] args) {
        System.out.println(distance("kitten", "sitting"));
    }
}
