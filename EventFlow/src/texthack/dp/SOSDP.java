package texthack.dp;

/**
 * MODULE 3 / CO3 - Sum Over Subsets (SOS) DP.
 *
 * Problem: given values[mask] for every mask of n bits, compute
 *
 *      dp[mask] = sum of values[submask] for every submask of mask
 *
 * The naive way is O(3^n). SOS DP does it in O(n * 2^n) by adding one bit at
 * a time:
 *
 *   for each bit b:
 *       for each mask:
 *           if mask has bit b:
 *               dp[mask] += dp[mask without b]
 *
 * Meaning of dp[mask] after processing bits 0..b-1: the sum over all
 * submasks that differ from mask only in those first b bits. After the last
 * bit, dp[mask] is the full sum over subsets.
 *
 * EventFlow use: for every combination of activity categories, the total
 * number of participants interested in any subset of those categories.
 *
 * Time  : O(n * 2^n)
 * Space : O(2^n)
 *
 * WHEN DP IS THE WRONG TOOL (CO3 discussion point):
 *   - no overlapping subproblems -> plain recursion or divide and conquer
 *   - a greedy rule is provably correct -> greedy is simpler and faster
 *   - the state space is too large (n = 40 here would need 2^40 states)
 *   - bitmask compression still leaves an exponential table
 *   - the problem has no optimal-substructure property at all
 */
public class SOSDP {

    /**
     * values.length must be a power of two (2^n).
     * Returns dp where dp[mask] = sum of values over all submasks of mask.
     */
    public static int[] calculate(int[] values) {

        int size = values.length;

        if (size == 0) {
            return new int[0];
        }

        int bits = 0;
        while ((1 << bits) < size) {
            bits++;
        }

        int[] dp = new int[size];
        for (int mask = 0; mask < size; mask++) {
            dp[mask] = values[mask];
        }

        for (int bit = 0; bit < bits; bit++) {

            for (int mask = 0; mask < size; mask++) {

                if ((mask & (1 << bit)) != 0) {
                    dp[mask] = dp[mask] + dp[mask ^ (1 << bit)];
                }
            }
        }
        return dp;
    }

    public static void main(String[] args) {

        int[] values = { 1, 2, 3, 4 };        // n = 2 bits
        int[] dp = calculate(values);

        for (int mask = 0; mask < dp.length; mask++) {
            System.out.println("dp[" + mask + "] = " + dp[mask]);
        }
        // dp[3] = 1 + 2 + 3 + 4 = 10
    }
}
