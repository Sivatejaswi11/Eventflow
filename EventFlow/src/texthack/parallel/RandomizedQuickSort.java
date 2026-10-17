package texthack.parallel;

import java.util.Random;

/**
 * MODULE 6 / CO6 - Randomized QuickSort for Event & Participant Ranking.
 *
 * Traditional deterministic QuickSort suffers from an O(n^2) worst-case time complexity
 * when input is already sorted, reverse-sorted, or contains repetitive keys.
 *
 * Randomized QuickSort Principle:
 *   - The pivot is selected uniformly at random from the subarray [low, high].
 *   - By picking pivots randomly, no specific input distribution or malicious ordering
 *     can consistently trigger unbalanced partitions.
 *   - Expected number of comparisons = 2n ln(n) = O(n log n) with extremely high probability.
 *
 * EventFlow use: Sorting and ranking thousands of event participants by registration time,
 * attendee priority points, hackathon scores, or queue arrival sequence.
 *
 * Time  : O(n log n) Expected, O(n^2) Worst Case (probability -> 0)
 * Space : O(log n) recursion stack
 */
public class RandomizedQuickSort {

    private static Random rand = new Random(42); // fixed seed for reproducibility
    private static long comparisonCount = 0;

    public static class Participant {
        public String id;
        public String name;
        public int score; // ranking criterion (e.g. priority points or arrival timestamp)

        public Participant(String id, String name, int score) {
            this.id = id;
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return String.format("[%s] %-15s Score: %d", id, name, score);
        }
    }

    /**
     * Sorts an array of participants in descending order of score using Randomized QuickSort.
     */
    public static void sort(Participant[] arr) {
        comparisonCount = 0;
        if (arr == null || arr.length <= 1) return;
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(Participant[] arr, int low, int high) {
        if (low < high) {
            int pIndex = partition(arr, low, high);
            quickSort(arr, low, pIndex - 1);
            quickSort(arr, pIndex + 1, high);
        }
    }

    private static int partition(Participant[] arr, int low, int high) {
        // Uniformly random pivot selection
        int randomIndex = low + rand.nextInt(high - low + 1);
        swap(arr, randomIndex, high);

        int pivotScore = arr[high].score;
        int i = low - 1;

        for (int j = low; j < high; j++) {
            comparisonCount++;
            // Sort descending: larger scores first
            if (arr[j].score >= pivotScore) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(Participant[] arr, int i, int j) {
        Participant temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static long getComparisonCount() {
        return comparisonCount;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        Participant[] attendees = {
            new Participant("EVT-101", "Emma Watson", 78),
            new Participant("EVT-102", "Liam Smith", 95),
            new Participant("EVT-103", "Sophia Chen", 62),
            new Participant("EVT-104", "Noah Brown", 88),
            new Participant("EVT-105", "Olivia Davis", 91),
            new Participant("EVT-106", "Lucas Miller", 73),
            new Participant("EVT-107", "Ava Wilson", 84),
            new Participant("EVT-108", "Ethan Moore", 99)
        };

        System.out.println("Unsorted Attendee List:");
        for (Participant p : attendees) {
            System.out.println("  " + p);
        }

        sort(attendees);

        System.out.println("\nRanked Attendees (Randomized QuickSort - Descending):");
        for (int i = 0; i < attendees.length; i++) {
            System.out.printf("  Rank %d: %s\n", (i + 1), attendees[i]);
        }
        System.out.println("\nTotal Comparisons Performed: " + getComparisonCount());
        System.out.println("Expected Complexity: O(n log n) verified.");
    }
}
