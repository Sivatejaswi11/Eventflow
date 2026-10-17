package texthack.stringalgorithms;

/**
 * MODULE 2 / CO2 - Naive (brute force) string matching.
 *
 * EventFlow use: simplest way to search an event name or activity inside a
 * document. It is the baseline we compare KMP / Z / Rabin-Karp against.
 *
 * Time  : O(n * m)
 * Space : O(1)
 */
public class NaiveSearch {

    /**
     * Returns the index of the FIRST occurrence of pattern in text,
     * or -1 if the pattern is not present.
     */
    public static int search(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return -1;
        }

        for (int i = 0; i + m <= n; i++) {

            int j = 0;

            while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }

            if (j == m) {
                return i;      // full pattern matched starting at i
            }
        }
        return -1;
    }

    /** Counts how many times the pattern occurs (overlapping allowed). */
    public static int countOccurrences(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();
        int count = 0;

        if (m == 0 || m > n) {
            return 0;
        }

        for (int i = 0; i + m <= n; i++) {

            int j = 0;

            while (j < m && text.charAt(i + j) == pattern.charAt(j)) {
                j++;
            }

            if (j == m) {
                count++;
            }
        }
        return count;
    }

    /** Independent test. */
    public static void main(String[] args) {
        String text = "National Hackathon is a coding competition";
        System.out.println(search(text, "coding"));
        System.out.println(search(text, "robotics"));
    }
}
