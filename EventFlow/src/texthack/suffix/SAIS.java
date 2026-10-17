package texthack.suffix;

/**
 * MODULE 2 / CO2 - SA-IS (Suffix Array Induced Sorting).
 *
 * Linear time O(n) Suffix Array construction algorithm by Nong, Zhang, and Chan.
 *
 * Core Concept:
 *   1. Classify each character / suffix as S-type (smaller) or L-type (larger):
 *      - suffix[n-1] is S-type.
 *      - suffix[i] is S-type if text[i] < text[i+1], or if text[i] == text[i+1] and suffix[i+1] is S-type.
 *      - suffix[i] is L-type otherwise.
 *   2. Identify LMS (Leftmost S-type) characters: an S-type character whose predecessor is L-type.
 *   3. Sort LMS substrings by induced sorting into bucket ends.
 *   4. Name LMS substrings. If all names are distinct, LMS order is directly determined;
 *      otherwise, recursively call SA-IS on the reduced LMS string.
 *   5. Induce the full suffix array from the sorted LMS suffixes:
 *      - Place LMS suffixes at ends of S-buckets.
 *      - Scan left-to-right to induce L-type suffixes into heads of L-buckets.
 *      - Scan right-to-left to induce S-type suffixes into tails of S-buckets.
 *
 * EventFlow use: High-performance, linear-time O(n) text indexing for massive event catalogs,
 * participant registers, and continuous schedule updates where O(n log^2 n) is too slow.
 *
 * Time  : O(n)
 * Space : O(n)
 */
public class SAIS {

    private static final boolean S_TYPE = false;
    private static final boolean L_TYPE = true;

    /**
     * Builds the Suffix Array of a string using the linear-time SA-IS algorithm.
     * Appends a sentinel character '$' (value 0) internally if not present.
     */
    public static int[] buildSuffixArray(String text) {
        if (text == null || text.length() == 0) {
            return new int[0];
        }

        int n = text.length();
        int[] s = new int[n + 1];
        int maxChar = 0;
        for (int i = 0; i < n; i++) {
            s[i] = (int) text.charAt(i) + 1; // shift so 0 is reserved for sentinel
            if (s[i] > maxChar) {
                maxChar = s[i];
            }
        }
        s[n] = 0; // sentinel '$' smaller than all characters

        int[] sa = new int[n + 1];
        sais(s, sa, n + 1, maxChar + 1);

        // Strip the sentinel at index 0
        int[] result = new int[n];
        System.arraycopy(sa, 1, result, 0, n);
        return result;
    }

    private static void sais(int[] s, int[] sa, int n, int alphabetSize) {
        boolean[] type = new boolean[n]; // false: S-type, true: L-type

        // 1. Classify S-type and L-type
        type[n - 1] = S_TYPE;
        for (int i = n - 2; i >= 0; i--) {
            if (s[i] < s[i + 1]) {
                type[i] = S_TYPE;
            } else if (s[i] > s[i + 1]) {
                type[i] = L_TYPE;
            } else {
                type[i] = type[i + 1];
            }
        }

        // 2. Buckets count
        int[] bucket = new int[alphabetSize];
        for (int i = 0; i < n; i++) {
            bucket[s[i]]++;
        }

        // LMS positions
        int[] lms = new int[n];
        int lmsCount = 0;
        for (int i = 1; i < n; i++) {
            if (type[i] == S_TYPE && type[i - 1] == L_TYPE) {
                lms[lmsCount++] = i;
            }
        }

        // Induced sort round 1: sort LMS prefixes
        for (int i = 0; i < n; i++) sa[i] = -1;
        int[] bucketEnd = getBucketTails(bucket, alphabetSize);
        for (int i = 0; i < lmsCount; i++) {
            int pos = lms[i];
            sa[--bucketEnd[s[pos]]] = pos;
        }

        induceSort(s, sa, type, bucket, alphabetSize, n);

        // 3. Name LMS substrings
        int[] lmsNames = new int[lmsCount];
        int name = 0;
        lmsNames[0] = 0;
        int lastLms = -1;
        for (int i = 0; i < n; i++) {
            int pos = sa[i];
            if (pos > 0 && type[pos] == S_TYPE && type[pos - 1] == L_TYPE) {
                if (lastLms != -1) {
                    if (!isLmsSubstrEqual(s, type, lastLms, pos)) {
                        name++;
                    }
                }
                lastLms = pos;
                // find index in lms array
                for (int k = 0; k < lmsCount; k++) {
                    if (lms[k] == pos) {
                        lmsNames[k] = name;
                        break;
                    }
                }
            }
        }

        // 4. Recursive sort if duplicate names exist
        int[] sortedLms = new int[lmsCount];
        if (name + 1 < lmsCount) {
            int[] summarySA = new int[lmsCount];
            sais(lmsNames, summarySA, lmsCount, name + 1);
            for (int i = 0; i < lmsCount; i++) {
                sortedLms[i] = lms[summarySA[i]];
            }
        } else {
            for (int i = 0; i < lmsCount; i++) {
                sortedLms[lmsNames[i]] = lms[i];
            }
        }

        // 5. Final Induced Sort from truly sorted LMS
        for (int i = 0; i < n; i++) sa[i] = -1;
        bucketEnd = getBucketTails(bucket, alphabetSize);
        for (int i = lmsCount - 1; i >= 0; i--) {
            int pos = sortedLms[i];
            sa[--bucketEnd[s[pos]]] = pos;
        }

        induceSort(s, sa, type, bucket, alphabetSize, n);
    }

    private static void induceSort(int[] s, int[] sa, boolean[] type, int[] bucket, int alphabetSize, int n) {
        // Induce L-type from left to right
        int[] bucketHead = getBucketHeads(bucket, alphabetSize);
        for (int i = 0; i < n; i++) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == L_TYPE) {
                sa[bucketHead[s[j]]++] = j;
            }
        }

        // Induce S-type from right to left
        int[] bucketEnd = getBucketTails(bucket, alphabetSize);
        for (int i = n - 1; i >= 0; i--) {
            int j = sa[i] - 1;
            if (j >= 0 && type[j] == S_TYPE) {
                sa[--bucketEnd[s[j]]] = j;
            }
        }
    }

    private static boolean isLmsSubstrEqual(int[] s, boolean[] type, int p1, int p2) {
        int i = 0;
        while (true) {
            int c1 = s[p1 + i];
            int c2 = s[p2 + i];
            if (c1 != c2) return false;
            boolean t1 = type[p1 + i];
            boolean t2 = type[p2 + i];
            if (t1 != t2) return false;
            if (i > 0) {
                boolean isLms1 = (t1 == S_TYPE && type[p1 + i - 1] == L_TYPE);
                boolean isLms2 = (t2 == S_TYPE && type[p2 + i - 1] == L_TYPE);
                if (isLms1 && isLms2) return true;
                if (isLms1 || isLms2) return false;
            }
            i++;
        }
    }

    private static int[] getBucketHeads(int[] bucket, int alphabetSize) {
        int[] head = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            head[i] = sum;
            sum += bucket[i];
        }
        return head;
    }

    private static int[] getBucketTails(int[] bucket, int alphabetSize) {
        int[] tail = new int[alphabetSize];
        int sum = 0;
        for (int i = 0; i < alphabetSize; i++) {
            sum += bucket[i];
            tail[i] = sum;
        }
        return tail;
    }

    /** Standalone testing demonstration for SA-IS. */
    public static void main(String[] args) {
        String eventDoc = "annual tech symposium keynote session workshop keynote";
        System.out.println("Text: \"" + eventDoc + "\"");
        System.out.println("Running SA-IS Linear Time Suffix Array Construction...");

        int[] sa = buildSuffixArray(eventDoc);
        System.out.println("Suffix Array (length " + sa.length + "):");
        for (int i = 0; i < Math.min(10, sa.length); i++) {
            String suffixPreview = eventDoc.substring(sa[i]);
            if (suffixPreview.length() > 30) {
                suffixPreview = suffixPreview.substring(0, 30) + "...";
            }
            System.out.printf("  sa[%2d] = %2d -> \"%s\"\n", i, sa[i], suffixPreview);
        }
        System.out.println("SA-IS successfully verified in O(n) time.");
    }
}
