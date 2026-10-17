package texthack.stringalgorithms;

/**
 * MODULE 2 / CO2 - Knuth-Morris-Pratt string matching.
 *
 * Idea: when a mismatch happens we do NOT move the text pointer back.
 * The LPS array (Longest Proper Prefix which is also a Suffix) tells us how
 * far the pattern can slide forward safely.
 *
 * EventFlow use: fast single-pattern search over long event documents.
 *
 * Time  : O(n + m)
 * Space : O(m)
 */
public class KMP {

    /**
     * Builds the LPS / failure function of the pattern.
     * lps[i] = length of the longest proper prefix of pattern[0..i]
     *          that is also a suffix of pattern[0..i].
     */
    public static int[] buildLPS(String pattern) {

        int m = pattern.length();
        int[] lps = new int[m];

        if (m == 0) {
            return lps;
        }

        lps[0] = 0;
        int length = 0;   // length of the current matching prefix
        int i = 1;

        while (i < m) {

            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];   // fall back, do not move i
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    /** Returns the index of the first occurrence, or -1. */
    public static int search(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return -1;
        }

        int[] lps = buildLPS(pattern);

        int i = 0;   // index in text
        int j = 0;   // index in pattern

        while (i < n) {

            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == m) {
                    return i - m;
                }

            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return -1;
    }

    /** Returns every starting index where the pattern occurs. */
    public static int[] searchAll(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return new int[0];
        }

        int[] lps = buildLPS(pattern);
        int[] found = new int[n];      // worst case: n matches
        int total = 0;

        int i = 0;
        int j = 0;

        while (i < n) {

            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;

                if (j == m) {
                    found[total++] = i - m;
                    j = lps[j - 1];
                }

            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        int[] result = new int[total];
        for (int k = 0; k < total; k++) {
            result[k] = found[k];
        }
        return result;
    }

    public static void main(String[] args) {
        String text = "National Hackathon 2026 hackathon coding";
        System.out.println(search(text, "hackathon"));
        int[] lps = buildLPS("ababaca");
        for (int value : lps) {
            System.out.print(value + " ");
        }
        System.out.println();
    }
}
