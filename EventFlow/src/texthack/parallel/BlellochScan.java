package texthack.parallel;

import java.util.Arrays;

/**
 * MODULE 6 / CO6 - Blelloch Parallel Prefix Scan for Cumulative Registrations.
 *
 * Traditional sequential prefix sum takes O(n) time and O(n) span (no parallelism).
 * Guy Blelloch's Work-Efficient Parallel Scan algorithm achieves:
 *   - Total Work : O(n) operations (work-efficient, matching sequential work!)
 *   - Parallel Span / Depth : O(log n) steps!
 *
 * Two-Phase Algorithmic Structure:
 *   1. Up-Sweep (Reduce Phase):
 *      - Traverses up a conceptual balanced binary tree.
 *      - Builds partial sums at stride 2^d for d = 0 to log2(n) - 1.
 *      - Takes O(log n) parallel steps and O(n) work.
 *   2. Down-Sweep Phase:
 *      - Sets the root element (last position) to 0 (the identity element for addition).
 *      - Traverses down the tree from d = log2(n) - 1 down to 0.
 *      - At each node:
 *          t = leftChild;
 *          leftChild = rightChild;
 *          rightChild = t + rightChild;
 *      - Generates the exclusive prefix scan in O(log n) parallel steps!
 *
 * EventFlow use: Real-time calculation of cumulative attendee entries across parallel turnstiles,
 * gate congestion forecasting, and continuous prefix sum analytics on registration batches.
 *
 * Work : O(n)
 * Span : O(log n)
 */
public class BlellochScan {

    /**
     * Executes Blelloch Exclusive Scan on an array of integers in-place.
     * Pads array length to next power of 2 internally.
     */
    public static int[] exclusiveScan(int[] input) {
        if (input == null || input.length == 0) {
            return new int[0];
        }

        int origLen = input.length;
        int n = 1;
        while (n < origLen) {
            n <<= 1;
        }

        int[] a = new int[n];
        System.arraycopy(input, 0, a, 0, origLen);

        // 1. Up-Sweep (Reduce Phase)
        for (int d = 0; (1 << d) < n; d++) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < n; k += step) {
                a[k + step - 1] += a[k + half - 1];
            }
        }

        // Set root to identity (0)
        a[n - 1] = 0;

        // 2. Down-Sweep Phase
        for (int d = 31 - Integer.numberOfLeadingZeros(n) - 1; d >= 0; d--) {
            int step = 1 << (d + 1);
            int half = 1 << d;
            for (int k = 0; k < n; k += step) {
                int leftChildIndex = k + half - 1;
                int rightChildIndex = k + step - 1;
                int temp = a[leftChildIndex];
                a[leftChildIndex] = a[rightChildIndex];
                a[rightChildIndex] += temp;
            }
        }

        // Return result truncated to original length
        return Arrays.copyOf(a, origLen);
    }

    /**
     * Computes the Inclusive Prefix Scan using Blelloch's algorithm:
     *   inclusive[i] = exclusive[i] + input[i]
     */
    public static int[] inclusiveScan(int[] input) {
        int[] excl = exclusiveScan(input);
        int[] incl = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            incl[i] = excl[i] + input[i];
        }
        return incl;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // Hourly registration counts across 8 parallel registration gates
        int[] hourlyRegistrations = {45, 60, 35, 80, 110, 95, 70, 120};

        System.out.println("Hourly Gate Check-in Stream:");
        for (int i = 0; i < hourlyRegistrations.length; i++) {
            System.out.printf("  Gate Turnstile %d: %3d arrivals\n", (i + 1), hourlyRegistrations[i]);
        }

        int[] exclusive = exclusiveScan(hourlyRegistrations);
        int[] inclusive = inclusiveScan(hourlyRegistrations);

        System.out.println("\nBlelloch Parallel Scan Results (Work: O(n), Span: O(log n)):");
        System.out.println("Gate | Count | Cumulative Before (Exclusive) | Total So Far (Inclusive)");
        System.out.println("-----+-------+-------------------------------+-------------------------");
        for (int i = 0; i < hourlyRegistrations.length; i++) {
            System.out.printf(" %2d  |  %3d  |              %4d             |          %4d\n",
                    (i + 1), hourlyRegistrations[i], exclusive[i], inclusive[i]);
        }
    }
}
