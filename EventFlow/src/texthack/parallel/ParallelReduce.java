package texthack.parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * MODULE 6 / CO6 - Parallel Tree Reduction for Event Statistics.
 *
 * Problem:
 *   Aggregating event analytics (total revenue, max gate queue, min workshop attendance)
 *   sequentially takes O(n) time on a single core. With parallel reduction, computations
 *   are executed in parallel across multiple CPU cores following a divide-and-conquer tree.
 *
 * Algorithm:
 *   - Balanced binary tree reduction using Java's ForkJoinPool.
 *   - Base condition: Subarrays below a threshold are processed sequentially to avoid thread overhead.
 *   - Recursive split: Subarrays are divided into left and right halves and forked in parallel.
 *   - Associative Operators: Supports SUM, MAX, MIN, and AVERAGE.
 *
 * Complexity Analysis:
 *   - Total Work W : O(n) total operations
 *   - Span / Depth S : O(log n) critical path length
 *   - Speedup on p cores : S_p = W / T_p <= p
 *
 * EventFlow use: Real-time calculation of overall event statistics, total ticket sales,
 * peak hall utilization, and crowd analytics across dozens of simultaneous halls.
 *
 * Work : O(n)
 * Span : O(log n)
 */
public class ParallelReduce {

    private static final int SEQUENTIAL_THRESHOLD = 500;
    private static final ForkJoinPool POOL = ForkJoinPool.commonPool();

    public enum Operator {
        SUM, MAX, MIN
    }

    public static class ReductionTask extends RecursiveTask<Long> {
        private long[] data;
        private int start;
        private int end;
        private Operator op;

        public ReductionTask(long[] data, int start, int end, Operator op) {
            this.data = data;
            this.start = start;
            this.end = end;
            this.op = op;
        }

        @Override
        protected Long compute() {
            int length = end - start;
            if (length <= SEQUENTIAL_THRESHOLD) {
                return sequentialCompute();
            }

            int mid = start + length / 2;
            ReductionTask leftTask = new ReductionTask(data, start, mid, op);
            ReductionTask rightTask = new ReductionTask(data, mid, end, op);

            leftTask.fork();
            long rightResult = rightTask.compute();
            long leftResult = leftTask.join();

            return combine(leftResult, rightResult);
        }

        private long sequentialCompute() {
            long result = (op == Operator.MIN) ? Long.MAX_VALUE : (op == Operator.MAX ? Long.MIN_VALUE : 0);
            for (int i = start; i < end; i++) {
                result = combine(result, data[i]);
            }
            return result;
        }

        private long combine(long a, long b) {
            switch (op) {
                case SUM: return a + b;
                case MAX: return Math.max(a, b);
                case MIN: return Math.min(a, b);
                default:  return a + b;
            }
        }
    }

    public static long reduce(long[] data, Operator op) {
        if (data == null || data.length == 0) return 0;
        return POOL.invoke(new ReductionTask(data, 0, data.length, op));
    }

    public static double average(long[] data) {
        if (data == null || data.length == 0) return 0.0;
        long totalSum = reduce(data, Operator.SUM);
        return (double) totalSum / data.length;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        int n = 10000;
        long[] sessionTurnouts = new long[n];
        for (int i = 0; i < n; i++) {
            sessionTurnouts[i] = (i * 73L + 127L) % 500 + 50; // simulated attendance [50..549]
        }

        System.out.println("Parallel Tree Reduction for Large-Scale Event Analytics:");
        System.out.println("Processing " + n + " session turnout records using ForkJoinPool...");
        System.out.println("Available CPU Parallelism Cores: " + POOL.getParallelism());

        long sum = reduce(sessionTurnouts, Operator.SUM);
        long max = reduce(sessionTurnouts, Operator.MAX);
        long min = reduce(sessionTurnouts, Operator.MIN);
        double avg = average(sessionTurnouts);

        System.out.println("\nEvent Statistics Computed via Parallel Reduce:");
        System.out.printf("  Total Event Attendance (SUM) : %,d attendees\n", sum);
        System.out.printf("  Peak Hall Attendance   (MAX) : %d attendees\n", max);
        System.out.printf("  Lowest Session Crowd   (MIN) : %d attendees\n", min);
        System.out.printf("  Average Attendance Per Hall  : %.2f attendees\n", avg);
        System.out.println("\nTheoretical Complexity: Work O(n), Parallel Span O(log n).");
    }
}
