package texthack.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MODULE 6 / CO6 - Reservoir Sampling for Continuous Registration Streams.
 *
 * Problem:
 *   Given a continuous, unbounded stream of event registrations where total count n
 *   is unknown in advance, select a uniform random sample of exactly k attendees
 *   in a single sequential pass using only O(k) memory.
 *
 * Algorithm (Algorithm R by Alan G. Waterman):
 *   1. Fill the reservoir array with the first k elements from the stream.
 *   2. For each subsequent i-th element (for i = k+1, k+2, ..., n):
 *      a. Generate a random integer j uniformly from [0, i - 1].
 *      b. If j < k, replace reservoir[j] with the i-th element.
 *      c. Otherwise, discard the i-th element.
 *
 * Mathematical Proof of Exact Uniform Probability (k / n):
 *   - Base case (i = k): Every element is placed into the reservoir with probability k/k = 1.
 *   - Inductive step: Assume after seeing n - 1 elements, every element is in the reservoir
 *     with probability k / (n - 1).
 *   - For the n-th element:
 *       P(n-th element enters reservoir) = k / n.
 *   - For any previously chosen element x in the reservoir:
 *       P(x remains in reservoir) = P(x was in reservoir) * P(x is not evicted by n-th element)
 *                                 = [k / (n - 1)] * [1 - (k / n) * (1 / k)]
 *                                 = [k / (n - 1)] * [(n - 1) / n]
 *                                 = k / n.
 *   - Thus, after any number of registrations n >= k, every attendee has an IDENTICAL
 *     probability k / n of being in the sample!
 *
 * EventFlow use: Live event lucky draws, uniform attendee feedback surveys, VIP meet-and-greet
 * random lotteries, and continuous crowd inspection without loading entire registration databases.
 *
 * Time  : O(n) single pass
 * Space : O(k) auxiliary storage
 */
public class ReservoirSampling<T> {

    private int k; // sample size
    private List<T> reservoir;
    private int count; // total items seen in the stream
    private Random random;

    public ReservoirSampling(int k) {
        this(k, new Random());
    }

    public ReservoirSampling(int k, Random random) {
        this.k = k;
        this.reservoir = new ArrayList<>(k);
        this.count = 0;
        this.random = random;
    }

    /**
     * Ingests the next item from the live registration stream.
     */
    public void ingest(T item) {
        count++;

        if (reservoir.size() < k) {
            // Fill initial reservoir
            reservoir.add(item);
        } else {
            // Pick random index between 0 and count - 1
            int j = random.nextInt(count);
            if (j < k) {
                reservoir.set(j, item); // evict and replace
            }
        }
    }

    public List<T> getSample() {
        return new ArrayList<>(reservoir);
    }

    public int getStreamCount() {
        return count;
    }

    public int getSampleSize() {
        return k;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        int sampleSize = 4;
        ReservoirSampling<String> sampler = new ReservoirSampling<>(sampleSize, new Random(101));

        String[] liveRegistrations = {
            "REG-001: Sarah Connor",
            "REG-002: Bruce Wayne",
            "REG-003: Clark Kent",
            "REG-004: Diana Prince",
            "REG-005: Peter Parker",
            "REG-006: Tony Stark",
            "REG-007: Natasha Romanoff",
            "REG-008: Barry Allen",
            "REG-009: Arthur Curry",
            "REG-010: Wanda Maximoff",
            "REG-011: Steve Rogers",
            "REG-012: Stephen Strange"
        };

        System.out.printf("Streaming %d continuous live event registrations into reservoir of size k = %d...\n\n",
                liveRegistrations.length, sampleSize);

        for (int i = 0; i < liveRegistrations.length; i++) {
            sampler.ingest(liveRegistrations[i]);
            if ((i + 1) % 3 == 0 || i == liveRegistrations.length - 1) {
                System.out.printf("After %2d arrivals, current random sample:\n", (i + 1));
                for (String attendee : sampler.getSample()) {
                    System.out.println("   * " + attendee);
                }
                System.out.println();
            }
        }

        System.out.printf("Stream finished. Total registrations: %d. Selection probability: %d/%d = %.2f%%\n",
                sampler.getStreamCount(), sampleSize, sampler.getStreamCount(),
                (100.0 * sampleSize / sampler.getStreamCount()));
    }
}
