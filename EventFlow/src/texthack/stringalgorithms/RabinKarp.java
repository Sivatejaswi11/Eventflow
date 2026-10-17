package texthack.stringalgorithms;

/**
 * MODULE 2 / CO2 - Rabin-Karp using a polynomial rolling hash.
 *
 * Idea: compare hash values instead of characters. The hash of the next
 * window is obtained from the previous one in O(1) (rolling hash).
 * Because two different strings can share a hash (a "spurious hit"),
 * every hash match is verified character by character.
 *
 * EventFlow use: searching short fixed-length registration / booking codes,
 * where many equal-length windows must be tested quickly.
 *
 * Time  : O(n + m) average, O(n * m) worst case
 * Space : O(1)
 */
public class RabinKarp {

    private static final long BASE = 256;        // alphabet size
    private static final long MOD = 1000000007L; // large prime modulus

    /** Returns the index of the first occurrence, or -1. */
    public static int search(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return -1;
        }

        long patternHash = 0;
        long windowHash = 0;
        long highestPower = 1;       // BASE^(m-1) % MOD

        for (int i = 0; i < m - 1; i++) {
            highestPower = (highestPower * BASE) % MOD;
        }

        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
        }

        for (int i = 0; i + m <= n; i++) {

            if (patternHash == windowHash && equalAt(text, pattern, i)) {
                return i;            // verified real match
            }

            if (i + m < n) {
                // remove the leaving character, add the entering character
                windowHash = (windowHash
                        - (text.charAt(i) * highestPower) % MOD + MOD) % MOD;
                windowHash = (windowHash * BASE + text.charAt(i + m)) % MOD;
            }
        }
        return -1;
    }

    /** Character-by-character verification after a hash match. */
    private static boolean equalAt(String text, String pattern, int start) {

        for (int j = 0; j < pattern.length(); j++) {
            if (text.charAt(start + j) != pattern.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /** Returns every starting index where the pattern occurs. */
    public static int[] searchAll(String text, String pattern) {

        int n = text.length();
        int m = pattern.length();

        if (m == 0 || m > n) {
            return new int[0];
        }

        int[] found = new int[n];
        int total = 0;

        long patternHash = 0;
        long windowHash = 0;
        long highestPower = 1;

        for (int i = 0; i < m - 1; i++) {
            highestPower = (highestPower * BASE) % MOD;
        }

        for (int i = 0; i < m; i++) {
            patternHash = (patternHash * BASE + pattern.charAt(i)) % MOD;
            windowHash = (windowHash * BASE + text.charAt(i)) % MOD;
        }

        for (int i = 0; i + m <= n; i++) {

            if (patternHash == windowHash && equalAt(text, pattern, i)) {
                found[total++] = i;
            }

            if (i + m < n) {
                windowHash = (windowHash
                        - (text.charAt(i) * highestPower) % MOD + MOD) % MOD;
                windowHash = (windowHash * BASE + text.charAt(i + m)) % MOD;
            }
        }

        int[] result = new int[total];
        for (int k = 0; k < total; k++) {
            result[k] = found[k];
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(search("REG2026A REG2026B REG2026C", "REG2026B"));
    }
}
