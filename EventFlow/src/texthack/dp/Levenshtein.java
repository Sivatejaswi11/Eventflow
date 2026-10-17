package texthack.dp;

/**
 * MODULE 3 / CO3 - Levenshtein edit distance (Wagner-Fischer DP).
 *
 * DP framework:
 *   State       : dp[i][j] = edit distance between a[0..i-1] and b[0..j-1]
 *   Base cases  : dp[i][0] = i (delete everything), dp[0][j] = j (insert all)
 *   Recurrence  : if characters match -> dp[i][j] = dp[i-1][j-1]
 *                 else 1 + min(delete dp[i-1][j],
 *                              insert dp[i][j-1],
 *                              replace dp[i-1][j-1])
 *   Overlapping subproblems: every cell is reused by the cells right and below.
 *   Optimal substructure   : the best edit of a prefix pair extends to the next.
 *
 * EventFlow use: spelling correction of event names and participant names
 * ("Hackaton" -> "Hackathon").
 *
 * Time  : O(n * m)
 * Space : O(n * m)
 */
public class Levenshtein {

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

                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int delete = dp[i - 1][j];
                    int insert = dp[i][j - 1];
                    int replace = dp[i - 1][j - 1];

                    int best = delete;
                    if (insert < best) {
                        best = insert;
                    }
                    if (replace < best) {
                        best = replace;
                    }
                    dp[i][j] = best + 1;
                }
            }
        }
        return dp[n][m];
    }

    /** Prints the full DP table so the recurrence can be traced by hand. */
    public static void printTable(String a, String b) {

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
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    int best = dp[i - 1][j];
                    if (dp[i][j - 1] < best) {
                        best = dp[i][j - 1];
                    }
                    if (dp[i - 1][j - 1] < best) {
                        best = dp[i - 1][j - 1];
                    }
                    dp[i][j] = best + 1;
                }
            }
        }

        System.out.print("        ");
        for (int j = 0; j < m; j++) {
            System.out.print("  " + b.charAt(j));
        }
        System.out.println();

        for (int i = 0; i <= n; i++) {

            if (i == 0) {
                System.out.print("     ");
            } else {
                System.out.print("   " + a.charAt(i - 1) + " ");
            }

            for (int j = 0; j <= m; j++) {
                String cell = "" + dp[i][j];
                while (cell.length() < 3) {
                    cell = " " + cell;
                }
                System.out.print(cell);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println(distance("kitten", "sitting"));
        printTable("kitten", "sitting");
    }
}
