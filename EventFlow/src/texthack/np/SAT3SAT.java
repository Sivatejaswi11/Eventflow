package texthack.np;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * MODULE 5 / CO5 - SAT & 3-SAT for Event-Scheduling Constraints.
 *
 * Problem Representation:
 *   Event scheduling involves complex multi-way constraints:
 *     - Variable x_i: Boolean condition (e.g. "Keynote in Hall A", "Workshop in Morning Slot", "Speaker B present").
 *     - Literals: x_i (positive) or ~x_i (negative / not).
 *     - Clause: Disjunction of literals (l1 OR l2 OR l3) representing requirements
 *       (e.g. "Hall A must host either Keynote, Opening, or Panel").
 *     - 3-SAT Formula: Conjunction of 3-literal clauses: C1 AND C2 AND ... AND Ck.
 *
 * Algorithm:
 *   DPLL (Davis-Putnam-Logemann-Loveland) backtracking algorithm:
 *     1. Unit Propagation: If a clause contains only a single unassigned literal, it must be TRUE.
 *     2. Early Failure Detection: If any clause is entirely false under current assignments, backtrack.
 *     3. Branching: Choose an unassigned variable and recursively try assigning TRUE, then FALSE.
 *
 * EventFlow use: Verifying whether a proposed event timetable with room capacities,
 * equipment needs, and speaker non-overlap rules is mathematically satisfiable.
 *
 * Time  : O(2^n) worst case (NP-Complete)
 * Space : O(n + m) recursion depth & clause storage
 */
public class SAT3SAT {

    public static class Clause {
        public int[] literals; // e.g. [1, -2, 3] where positive means x_i, negative means ~x_i

        public Clause(int l1, int l2, int l3) {
            this.literals = new int[]{l1, l2, l3};
        }

        public Clause(int[] lits) {
            this.literals = lits;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < literals.length; i++) {
                int lit = literals[i];
                sb.append(lit > 0 ? "x" + lit : "~x" + (-lit));
                if (i < literals.length - 1) sb.append(" v ");
            }
            sb.append(")");
            return sb.toString();
        }
    }

    private int numVariables;
    private List<Clause> clauses;

    public SAT3SAT(int numVariables) {
        this.numVariables = numVariables;
        this.clauses = new ArrayList<>();
    }

    public void addClause(int l1, int l2, int l3) {
        clauses.add(new Clause(l1, l2, l3));
    }

    public void addClause(Clause c) {
        clauses.add(c);
    }

    public List<Clause> getClauses() {
        return clauses;
    }

    public int getNumVariables() {
        return numVariables;
    }

    /**
     * Solves the 3-SAT problem using the DPLL algorithm.
     * Returns an int array of size numVariables + 1, where:
     *   1  = variable is TRUE
     *   0  = variable is FALSE
     *  -1  = unsatisfiable
     */
    public int[] solve() {
        int[] assignment = new int[numVariables + 1];
        Arrays.fill(assignment, -1); // -1 = unassigned

        boolean sat = dpll(assignment);
        if (!sat) {
            return null; // unsatisfiable
        }
        return assignment;
    }

    private boolean dpll(int[] assignment) {
        // Unit propagation
        boolean changed = true;
        while (changed) {
            changed = false;
            for (Clause c : clauses) {
                int unassignedLit = 0;
                int unassignedCount = 0;
                boolean clauseSatisfied = false;

                for (int lit : c.literals) {
                    int var = Math.abs(lit);
                    int val = assignment[var];

                    if (val != -1) {
                        boolean litVal = (lit > 0) ? (val == 1) : (val == 0);
                        if (litVal) {
                            clauseSatisfied = true;
                            break;
                        }
                    } else {
                        unassignedCount++;
                        unassignedLit = lit;
                    }
                }

                if (clauseSatisfied) {
                    continue;
                }

                // If all literals are false, contradiction!
                if (unassignedCount == 0) {
                    return false;
                }

                // Unit clause: exactly one unassigned literal, must satisfy it
                if (unassignedCount == 1) {
                    int var = Math.abs(unassignedLit);
                    assignment[var] = (unassignedLit > 0) ? 1 : 0;
                    changed = true;
                }
            }
        }

        // Check if all clauses are satisfied
        boolean allSatisfied = true;
        for (Clause c : clauses) {
            boolean clauseSat = false;
            for (int lit : c.literals) {
                int var = Math.abs(lit);
                int val = assignment[var];
                if (val != -1) {
                    boolean litVal = (lit > 0) ? (val == 1) : (val == 0);
                    if (litVal) {
                        clauseSat = true;
                        break;
                    }
                }
            }
            if (!clauseSat) {
                allSatisfied = false;
                break;
            }
        }

        if (allSatisfied) {
            // Fill any remaining unassigned variables with 0
            for (int i = 1; i <= numVariables; i++) {
                if (assignment[i] == -1) assignment[i] = 1;
            }
            return true;
        }

        // Choose first unassigned variable to branch
        int chooseVar = -1;
        for (int i = 1; i <= numVariables; i++) {
            if (assignment[i] == -1) {
                chooseVar = i;
                break;
            }
        }

        if (chooseVar == -1) {
            return false;
        }

        // Branch 1: Try TRUE
        int[] copy1 = Arrays.copyOf(assignment, assignment.length);
        copy1[chooseVar] = 1;
        if (dpll(copy1)) {
            System.arraycopy(copy1, 0, assignment, 0, assignment.length);
            return true;
        }

        // Branch 2: Try FALSE
        int[] copy2 = Arrays.copyOf(assignment, assignment.length);
        copy2[chooseVar] = 0;
        if (dpll(copy2)) {
            System.arraycopy(copy2, 0, assignment, 0, assignment.length);
            return true;
        }

        return false;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // Event constraint scenario with 4 scheduling variables:
        // x1: Morning AI Workshop
        // x2: Afternoon AI Workshop
        // x3: Keynote Audio Setup
        // x4: Evening Networking Gala
        SAT3SAT solver = new SAT3SAT(4);

        // Clause 1: Either Morning AI (x1) or Afternoon AI (x2) or Audio Setup (x3) must occur
        solver.addClause(1, 2, 3);
        // Clause 2: Cannot have both Morning and Afternoon AI without Audio (~x1 v ~x2 v x3)
        solver.addClause(-1, -2, 3);
        // Clause 3: Audio setup implies Networking Gala (~x3 v 4 v 1)
        solver.addClause(-3, 4, 1);
        // Clause 4: Morning Workshop or Gala (~x1 v 4 v 2)
        solver.addClause(-1, 4, 2);

        System.out.println("3-SAT Event Scheduling Formulation:");
        for (Clause c : solver.getClauses()) {
            System.out.println("  Constraint: " + c);
        }

        int[] sol = solver.solve();
        if (sol != null) {
            System.out.println("\nSchedule Constraints are SATISFIABLE!");
            for (int i = 1; i <= 4; i++) {
                System.out.printf("  x%d = %s\n", i, (sol[i] == 1 ? "TRUE (Scheduled)" : "FALSE (Not Scheduled)"));
            }
        } else {
            System.out.println("\nConstraints are UNSATISFIABLE - Conflict in requirements.");
        }
    }
}
