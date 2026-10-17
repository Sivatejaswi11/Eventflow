package texthack.stringalgorithms;

/**
 * MODULE 2 / CO2 - Z-function string matching.
 *
 * z[i] = length of the longest substring starting at i that is also a
 * prefix of the whole string.
 *
 * Search trick: build the string  pattern + '\u0001' + text.
 * Wherever z[i] == pattern.length() we have found an occurrence.
 *
 * EventFlow use: alternative linear-time search; also useful for finding
 * repeated prefixes inside event descriptions.
 *
 * Time  : O(n + m)
 * Space : O(n + m)
 */
public class ZFunction {

    /** Builds the Z-array of a string. */
    public static int[] buildZ(String s) {

        int n = s.length();
        int[] z = new int[n];

        if (n == 0) {
            return z;
        }

        z[0] = n;

        int left = 0;
        int right = 0;   // [left, right] is the rightmost matching segment

        for (int i = 1; i < n; i++) {

            if (i < right) {
                int mirror = i - left;
                z[i] = Math.min(right - i, z[mirror]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] > right) {
                left = i;
                right = i + z[i];
            }
        }
        return z;
    }

    /** Returns the index of the first occurrence, or -1. */
    public static int search(String text, String pattern) {

        int m = pattern.length();

        if (m == 0 || m > text.length()) {
            return -1;
        }

        String combined = pattern + '\u0001' + text;
        int[] z = buildZ(combined);

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] == m) {
                return i - m - 1;      // shift back to a text index
            }
        }
        return -1;
    }

    /** Returns every starting index where the pattern occurs. */
    public static int[] searchAll(String text, String pattern) {

        int m = pattern.length();

        if (m == 0 || m > text.length()) {
            return new int[0];
        }

        String combined = pattern + '\u0001' + text;
        int[] z = buildZ(combined);

        int[] found = new int[text.length()];
        int total = 0;

        for (int i = m + 1; i < combined.length(); i++) {
            if (z[i] == m) {
                found[total++] = i - m - 1;
            }
        }

        int[] result = new int[total];
        for (int k = 0; k < total; k++) {
            result[k] = found[k];
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(search("aabxaabxcaabxaabxay", "aabxaabxc"));
    }
}
