package texthack.suffix;

/**
 * MODULE 2 / CO2 - Suffix Array.
 *
 * Definition: the suffix array of a text is the array of the starting indices
 * of all suffixes of the text, sorted in lexicographic order.
 *
 * Construction used here: PREFIX DOUBLING.
 *   Round 1 sorts suffixes by their first 1 character.
 *   Round 2 sorts by their first 2 characters (using round 1 ranks).
 *   Round 3 sorts by 4 characters, then 8, 16, ... until 2^k >= n.
 * Each round sorts n elements -> O(n log n) per round, O(n log^2 n) total.
 *
 * SA-IS (conceptual note, required by the syllabus):
 *   SA-IS classifies each suffix as S-type or L-type, finds "LMS" substrings,
 *   sorts those recursively and then induces the order of every other suffix
 *   from them. It builds the same array in O(n) but is much harder to write,
 *   so prefix doubling is used here for learning purposes.
 *
 * Suffix trees (preview level):
 *   A suffix tree is a compressed trie of all suffixes. Every substring of the
 *   text is a prefix of some root-to-leaf path, so substring queries take
 *   O(m) time. It answers the same questions as a suffix array but uses much
 *   more memory (roughly 10-20 bytes per character vs 4-8 for a suffix array)
 *   and is far harder to implement (Ukkonen's algorithm).
 *
 * Suffix automata (preview level):
 *   A suffix automaton is the smallest DFA that accepts exactly all suffixes
 *   of the text. It has at most 2n-1 states and is built online in O(n).
 *   It is excellent for counting distinct substrings and for the number of
 *   occurrences of a substring. A suffix array is a sorted *list*, an
 *   automaton is a *graph* - the array is simpler and cache friendly, the
 *   automaton is better for streaming and counting problems.
 *
 * EventFlow use: indexing an event document so that any activity name,
 * venue or keyword can be located, and so that repeated text can be found
 * later with LCP / Kasai.
 *
 * Time  : O(n log^2 n)
 * Space : O(n)
 *
 * No java.util sorting is used - a merge sort is written by hand.
 */
public class SuffixArray {

    private static int[] rank;        // current rank of each suffix
    private static int[] temporary;   // helper array for the merge sort
    private static int gap;           // current comparison offset (2^k)
    private static int length;

    /** Builds and returns the suffix array of the text. */
    public static int[] buildSuffixArray(String text) {

        length = text.length();
        int[] suffixArray = new int[length];

        if (length == 0) {
            return suffixArray;
        }

        rank = new int[length];
        temporary = new int[length];

        for (int i = 0; i < length; i++) {
            suffixArray[i] = i;
            rank[i] = text.charAt(i);      // round 0: rank = character code
        }

        for (gap = 1; ; gap = gap * 2) {

            mergeSort(suffixArray, 0, length - 1);

            int[] newRank = new int[length];
            newRank[suffixArray[0]] = 0;

            for (int i = 1; i < length; i++) {
                if (compare(suffixArray[i - 1], suffixArray[i]) < 0) {
                    newRank[suffixArray[i]] = newRank[suffixArray[i - 1]] + 1;
                } else {
                    newRank[suffixArray[i]] = newRank[suffixArray[i - 1]];
                }
            }

            for (int i = 0; i < length; i++) {
                rank[i] = newRank[i];
            }

            if (rank[suffixArray[length - 1]] == length - 1) {
                break;                      // every suffix has a unique rank
            }
            if (gap > length) {
                break;
            }
        }
        return suffixArray;
    }

    /**
     * Compares suffix a and suffix b using the ranks of the previous round.
     * The pair (rank[i], rank[i+gap]) represents the first 2*gap characters.
     */
    private static int compare(int a, int b) {

        if (rank[a] != rank[b]) {
            return rank[a] < rank[b] ? -1 : 1;
        }

        int rankA = (a + gap < length) ? rank[a + gap] : -1;
        int rankB = (b + gap < length) ? rank[b + gap] : -1;

        if (rankA != rankB) {
            return rankA < rankB ? -1 : 1;
        }
        return 0;
    }

    /** Hand-written merge sort so that no library sorting is used. */
    private static void mergeSort(int[] array, int left, int right) {

        if (left >= right) {
            return;
        }

        int middle = (left + right) / 2;

        mergeSort(array, left, middle);
        mergeSort(array, middle + 1, right);

        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            if (compare(array[i], array[j]) <= 0) {
                temporary[k++] = array[i++];
            } else {
                temporary[k++] = array[j++];
            }
        }
        while (i <= middle) {
            temporary[k++] = array[i++];
        }
        while (j <= right) {
            temporary[k++] = array[j++];
        }
        for (int index = left; index <= right; index++) {
            array[index] = temporary[index];
        }
    }

    /** Prints the suffix array with a short preview of each suffix. */
    public static void printSuffixArray(String text) {

        int[] suffixArray = buildSuffixArray(text);

        System.out.println("Index | Suffix (first 40 characters)");
        System.out.println("------------------------------------------");

        for (int i = 0; i < suffixArray.length; i++) {

            int start = suffixArray[i];
            int end = start + 40;
            if (end > text.length()) {
                end = text.length();
            }

            String preview = text.substring(start, end)
                    .replace('\n', ' ')
                    .replace('\r', ' ');

            System.out.println(pad(start) + " | " + preview);
        }
    }

    private static String pad(int value) {
        String s = "" + value;
        while (s.length() < 5) {
            s = " " + s;
        }
        return s;
    }

    public static void main(String[] args) {
        printSuffixArray("banana");
    }
}
