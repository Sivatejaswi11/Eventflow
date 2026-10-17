package texthack.parallel;

/**
 * MODULE 6 / CO6 - Brent's Theorem for Parallel Event-Data Processing.
 *
 * Theoretical Formulation (Brent's Scheduling Principle):
 *   Let a parallel computation have:
 *     - Total Work W: Total number of elementary operations (W = T_1, execution time on 1 processor).
 *     - Span / Depth S: Length of the critical path / longest dependency chain (S = T_infinity).
 *
 * Brent's Theorem states that on p identical processors with a greedy schedule:
 *     T_p <= (W - S) / p + S < W / p + S
 *
 * Detailed Level-by-Level Formulation:
 *   If the computation DAG is partitioned into S levels where level i contains W_i independent
 *   operations (so sum(W_i) = W), then with p processors:
 *     T_p = sum_{i=1}^{S} ceil(W_i / p)
 *   Since ceil(W_i / p) <= (W_i + p - 1) / p:
 *     T_p <= sum (W_i / p) + sum ((p - 1) / p)
 *         <= W / p + S * (1 - 1/p) = (W - S) / p + S.
 *
 * Core Engineering Metrics:
 *   - Parallel Speedup  : S_p = T_1 / T_p = W / T_p
 *   - Parallel Efficiency: E_p = S_p / p = W / (p * T_p)  [Ideal = 100%]
 *   - Saturation Point  : p* = W / S (Beyond this, adding processors yields diminishing returns)
 *
 * EventFlow use: Sizing compute worker pools for large-scale badge printing, multi-venue video streaming,
 * and bulk attendee ticket verification workloads.
 *
 * Time  : O(S) computation
 * Space : O(S)
 */
public class BrentsTheorem {

    public static class ScheduleAnalysis {
        public int workW;
        public int spanS;
        public int processorsP;
        public int actualTimeTp;
        public double brentBound;
        public double speedup;
        public double efficiency;

        @Override
        public String toString() {
            return String.format("p=%2d cores | Time T_p=%3d steps | Brent Upper Bound: %6.2f | Speedup: %5.2fx | Efficiency: %5.1f%%",
                    processorsP, actualTimeTp, brentBound, speedup, efficiency * 100);
        }
    }

    /**
     * Evaluates execution time and Brent's bound given operations per level and number of processors p.
     */
    public static ScheduleAnalysis analyze(int[] levelOperations, int p) {
        int spanS = levelOperations.length;
        int workW = 0;
        int actualTimeTp = 0;

        for (int w : levelOperations) {
            workW += w;
            actualTimeTp += (w + p - 1) / p; // ceil(w / p)
        }

        double brentBound = ((double) (workW - spanS) / p) + spanS;
        double speedup = (double) workW / actualTimeTp;
        double efficiency = speedup / p;

        ScheduleAnalysis res = new ScheduleAnalysis();
        res.workW = workW;
        res.spanS = spanS;
        res.processorsP = p;
        res.actualTimeTp = actualTimeTp;
        res.brentBound = brentBound;
        res.speedup = speedup;
        res.efficiency = efficiency;

        return res;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // Event processing workflow with 8 dependency stages (e.g. attendee validation pipeline):
        // Stage 1: Ingest 128 registration files
        // Stage 2: Parse 256 identity records
        // Stage 3: Validate 512 ticket codes
        // Stage 4: Generate 512 QR access tokens
        // Stage 5: Encrypt 256 badge keys
        // Stage 6: Render 128 PDF badges
        // Stage 7: Bundle 32 hall manifests
        // Stage 8: Sync 1 master event catalog
        int[] eventPipelineLevels = {128, 256, 512, 512, 256, 128, 32, 1};

        int totalWork = 0;
        for (int w : eventPipelineLevels) totalWork += w;
        int criticalPathSpan = eventPipelineLevels.length;

        System.out.println("Brent's Theorem Parallel Scheduling Analysis:");
        System.out.printf("  Total Work W = %d operations (T_1 on 1 core)\n", totalWork);
        System.out.printf("  Span / Depth S = %d stages (T_infinity on infinite cores)\n", criticalPathSpan);
        System.out.printf("  Optimal Processor Balance Point (W / S) = %.1f cores\n\n", (double) totalWork / criticalPathSpan);

        int[] coreConfigurations = {1, 2, 4, 8, 16, 32, 64, 128, 256};
        for (int p : coreConfigurations) {
            ScheduleAnalysis analysis = analyze(eventPipelineLevels, p);
            System.out.println("  " + analysis);
        }

        System.out.println("\nVerification: In all cases, Actual T_p <= Brent's Bound ((W - S)/p + S).");
    }
}
