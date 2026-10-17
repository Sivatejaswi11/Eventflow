package texthack.dp;

/**
 * MODULE 3 / CO3 - Damerau-Levenshtein distance.
 *
 * Same as Levenshtein plus one extra operation: TRANSPOSITION
 * (swapping two adjacent characters), which costs 1.
 *
 * Extra recurrence line:
 *   if a[i-1] == b[j-2] and a[i-2] == b[j-1]
 *       dp[i][j] = min(dp[i][j], dp[i-2][j-2] + 1)
 *
 * This is the "restricted" (optimal string alignment) version, which is what
 * the course uses.
 *
 * EventFlow use: correcting typing slips in participant data - "form" typed
 * instead of "from", "Hcakathon" instead of "Hackathon".
 *
 * Time  : O(n * m)
 * Space : O(n * m)
 */
public class DamerauLevenshtein {

    public static int distance(String a, String b) {

        int n = a.length();
        int m = b.length();

        int[][] dp = new int[n + 1][m + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                int cost;
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    cost = 0;
                } else {
                    cost = 1;
                }

                int delete = dp[i - 1][j] + 1;
                int insert = dp[i][j - 1] + 1;
                int replace = dp[i - 1][j - 1] + cost;

                int best = delete;
                if (insert < best) {
                    best = insert;
                }
                if (replace < best) {
                    best = replace;
                }

                // transposition of two adjacent characters
                if (i > 1 && j > 1
                        && a.charAt(i - 1) == b.charAt(j - 2)
                        && a.charAt(i - 2) == b.charAt(j - 1)) {

                    int transpose = dp[i - 2][j - 2] + 1;
                    if (transpose < best) {
                        best = transpose;
                    }
                }

                dp[i][j] = best;
            }
        }
        return dp[n][m];
    }

    public static void main(String[] args) {
        System.out.println(distance("form", "from"));   // 1
        System.out.println(distance("ca", "abc"));      // 3 (restricted version)
    }
}
