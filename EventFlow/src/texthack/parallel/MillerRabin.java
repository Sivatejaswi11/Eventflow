package texthack.parallel;

import java.math.BigInteger;
import java.util.Random;

/**
 * MODULE 6 / CO6 - Miller-Rabin Probabilistic Primality Test for Large Numerical Processing.
 *
 * Problem:
 *   In large numerical processing, standard trial division O(sqrt(n)) is completely infeasible
 *   for 64-bit or 100-digit integers. Miller-Rabin determines primality in polynomial time
 *   O(k * log^3 n) with an exponentially negligible error bound <= 4^(-k).
 *
 * Mathematical Foundation:
 *   Let n be an odd prime. Write n - 1 = 2^s * d, where d is odd.
 *   By Fermat's Little Theorem and properties of square roots of 1 modulo a prime:
 *     For any base a coprime to n:
 *       EITHER: a^d = 1 (mod n)
 *       OR:     a^(d * 2^r) = -1 (mod n) for some 0 <= r < s.
 *   If NEITHER condition holds, 'a' is a Miller-Rabin witness to the compositeness of n.
 *
 * Error Probability:
 *   For any odd composite n, at least 3/4 of all bases a in [2, n - 2] are witnesses.
 *   Testing k independent random bases reduces error probability to at most (1/4)^k.
 *   For k = 20 rounds, error probability < 10^(-12).
 *
 * EventFlow use: Cryptographic ticketing authentication, anti-counterfeit QR code generation,
 * large prime modulus selection for distributed hash tables in high-throughput attendee tracking.
 *
 * Time  : O(k * log^3 n)
 * Space : O(log n)
 */
public class MillerRabin {

    private static final Random random = new Random(42);

    /**
     * Modular exponentiation: computes (base^exp) % mod using binary exponentiation.
     */
    public static long modPow(long base, long exp, long mod) {
        long result = 1;
        base = base % mod;
        while (exp > 0) {
            if ((exp & 1) == 1) {
                result = (long) (((BigInteger.valueOf(result).multiply(BigInteger.valueOf(base)))
                        .mod(BigInteger.valueOf(mod))).longValue());
            }
            base = (long) (((BigInteger.valueOf(base).multiply(BigInteger.valueOf(base)))
                    .mod(BigInteger.valueOf(mod))).longValue());
            exp >>= 1;
        }
        return result;
    }

    /**
     * Performs a single round of the Miller-Rabin test for base a.
     * Returns true if n passes as a probable prime, false if proven composite.
     */
    public static boolean checkWitness(long n, long a, long d, int s) {
        long x = modPow(a, d, n);

        if (x == 1 || x == n - 1) {
            return true;
        }

        for (int r = 1; r < s; r++) {
            x = (long) (((BigInteger.valueOf(x).multiply(BigInteger.valueOf(x)))
                    .mod(BigInteger.valueOf(n))).longValue());
            if (x == n - 1) {
                return true;
            }
            if (x == 1) {
                return false; // found non-trivial square root of 1
            }
        }
        return false;
    }

    /**
     * Tests if n is prime with k rounds of Miller-Rabin.
     */
    public static boolean isProbablePrime(long n, int k) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0) return false;

        // Factor n - 1 into 2^s * d
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }

        // Deterministic bases for standard 64-bit testing
        long[] deterministicBases = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

        for (int round = 0; round < k; round++) {
            long a;
            if (round < deterministicBases.length && deterministicBases[round] < n) {
                a = deterministicBases[round];
            } else {
                a = 2 + (Math.abs(random.nextLong()) % (n - 3));
            }

            if (!checkWitness(n, a, d, s)) {
                return false; // proven composite
            }
        }
        return true; // highly probable prime
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        long[] candidateTicketCodes = {
            1000000007L,         // Prime (famous modulus)
            1000000009L,         // Prime
            1000000011L,         // Composite (3 * 333333337)
            2147483647L,         // Mersenne Prime 2^31 - 1
            999999999999999989L, // Large 18-digit prime
            999999999999999991L  // Large 18-digit composite
        };

        System.out.println("Miller-Rabin Primality Testing for Large Numerical Event Processing:");
        System.out.println("Rounds k = 20 (Error probability < 1 in 1,000,000,000,000)\n");

        for (long code : candidateTicketCodes) {
            boolean prime = isProbablePrime(code, 20);
            System.out.printf("  Number: %-20d -> %s\n", code, (prime ? "PRIME (Secure Hash / Token)" : "COMPOSITE"));
        }
    }
}
