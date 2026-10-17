package texthack.suffix;

/**
 * MODULE 2 / CO2 - LCP array built with Kasai's algorithm.
 *
 * lcp[i] = length of the longest common prefix of the suffixes
 *          suffixArray[i] and suffixArray[i-1]  (adjacent in sorted order).
 *
 * Kasai's trick: process the suffixes in the order they appear in the TEXT,
 * not in sorted order. When we move from suffix i to suffix i+1 we lose at
 * most one character, so the running length k only has to drop by 1 each
 * step. That makes the whole thing linear.
 *
 * EventFlow use: finding text that repeats inside or between event
 * documents - duplicated descriptions, copied activity lists, the longest
 * repeated substring.
 *
 * Time  : O(n)
 * Space : O(n)
 */
public class Kasai {

    /** Builds the LCP array for a text and its suffix array. */
    public static int[] buildLCP(String text, int[] suffixArray) {

        int n = text.length();
        int[] lcp = new int[n];

        if (n == 0) {
            return lcp;
        }

        // rank[i] = position of suffix i inside the suffix array
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) {
            rank[suffixArray[i]] = i;
        }

        int k = 0;   // current common prefix length

        for (int i = 0; i < n; i++) {

            if (rank[i] == 0) {
                k = 0;            // first suffix has no previous neighbour
                continue;
            }

            int previousSuffix = suffixArray[rank[i] - 1];

            while (i + k < n
                    && previousSuffix + k < n
                    && text.charAt(i + k) == text.charAt(previousSuffix + k)) {
                k++;
            }

            lcp[rank[i]] = k;

            if (k > 0) {
                k--;              // the key Kasai observation
            }
        }
        return lcp;
    }

    /** Prints the LCP value of every adjacent pair of suffixes. */
    public static void printLCP(String text, int[] suffixArray) {

        int[] lcp = buildLCP(text, suffixArray);

        System.out.println("Rank | SA index | LCP | Suffix (first 30 chars)");
        System.out.println("-----------------------------------------------");

        for (int i = 0; i < lcp.length; i++) {

            int start = suffixArray[i];
            int end = start + 30;
            if (end > text.length()) {
                end = text.length();
            }

            String preview = text.substring(start, end)
                    .replace('\n', ' ')
                    .replace('\r', ' ');

            System.out.println(i + " | " + start + " | " + lcp[i] + " | " + preview);
        }
    }

    /** Longest substring that appears at least twice = maximum LCP value. */
    public static String longestRepeatedSubstring(String text, int[] suffixArray) {

        int[] lcp = buildLCP(text, suffixArray);

        int best = 0;
        int bestPosition = -1;

        for (int i = 0; i < lcp.length; i++) {
            if (lcp[i] > best) {
                best = lcp[i];
                bestPosition = suffixArray[i];
            }
        }

        if (bestPosition == -1 || best == 0) {
            return "";
        }
        return text.substring(bestPosition, bestPosition + best);
    }

    public static void main(String[] args) {
        String text = "banana";
        int[] sa = SuffixArray.buildSuffixArray(text);
        printLCP(text, sa);
        System.out.println("Longest repeated: " + longestRepeatedSubstring(text, sa));
    }
}
