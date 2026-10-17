package texthack.np;

import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 5 / CO5 - Polynomial-Time Reduction: 3-SAT -> CLIQUE.
 *
 * Karp's Classic Reduction (Proving CLIQUE is NP-Complete):
 *   Given: A 3-SAT formula phi with k clauses C_1, C_2, ..., C_k, where each C_r = (l_{r,1} v l_{r,2} v l_{r,3}).
 *   Construct: A graph G = (V, E) and integer target k:
 *     - Vertices V: 3 vertices per clause (one for each literal position). |V| = 3k.
 *     - Edges E: Connect vertex (r, p) and vertex (s, q) IF AND ONLY IF:
 *         1. r != s (the two vertices belong to DIFFERENT clauses).
 *         2. l_{r,p} != ~l_{s,q} (the literals are NOT mutual negations; they are mutually consistent).
 *
 * Equivalence Proof:
 *   - If phi is satisfiable: At least one literal in each of the k clauses evaluates to TRUE.
 *     Selecting one true literal vertex from each clause yields k vertices. Because they are in
 *     distinct clauses and mutually consistent, an edge exists between every pair -> forms a k-clique!
 *   - Conversely: If G has a k-clique, since no edges exist within the same clause, the clique must
 *     pick exactly one vertex per clause. Because edges only link non-conflicting literals, we can
 *     set each chosen literal to TRUE without contradiction -> satisfies phi!
 *
 * EventFlow use: Transforming complex multi-constraint event rules into maximum compatible
 * activity cliques for conflict analysis.
 *
 * Reduction Time  : O(k^2)
 * Space Complexity: O(k^2)
 */
public class SATToClique {

    public static class Node {
        public int clauseIndex;
        public int literal;     // e.g. 1 for x1, -2 for ~x2
        public int id;

        public Node(int id, int clauseIndex, int literal) {
            this.id = id;
            this.clauseIndex = clauseIndex;
            this.literal = literal;
        }

        @Override
        public String toString() {
            return String.format("v%d[C%d:%s]", id, clauseIndex, (literal > 0 ? "x" + literal : "~x" + (-literal)));
        }
    }

    public static class ReductionGraph {
        public int k; // number of clauses (target clique size)
        public List<Node> nodes = new ArrayList<>();
        public boolean[][] adj;

        public ReductionGraph(int k, int totalNodes) {
            this.k = k;
            this.adj = new boolean[totalNodes][totalNodes];
        }

        public void addEdge(int u, int v) {
            adj[u][v] = true;
            adj[v][u] = true;
        }
    }

    /**
     * Transforms a 3-SAT instance into a CLIQUE graph instance in polynomial time O(k^2).
     */
    public static ReductionGraph reduce(SAT3SAT sat) {
        List<SAT3SAT.Clause> clauses = sat.getClauses();
        int k = clauses.size();
        int totalNodes = 3 * k;

        ReductionGraph graph = new ReductionGraph(k, totalNodes);

        // 1. Create 3 vertices per clause
        int nodeId = 0;
        for (int c = 0; c < k; c++) {
            SAT3SAT.Clause clause = clauses.get(c);
            for (int lit : clause.literals) {
                graph.nodes.add(new Node(nodeId++, c, lit));
            }
        }

        // 2. Add edges between compatible literals across different clauses
        for (int i = 0; i < totalNodes; i++) {
            Node u = graph.nodes.get(i);
            for (int j = i + 1; j < totalNodes; j++) {
                Node v = graph.nodes.get(j);

                // Must be in different clauses
                if (u.clauseIndex != v.clauseIndex) {
                    // Must not be conflicting literals (e.g. x1 and ~x1)
                    if (u.literal != -v.literal) {
                        graph.addEdge(i, j);
                    }
                }
            }
        }

        return graph;
    }

    /**
     * Checks if a subset of vertices forms a clique.
     */
    public static boolean isClique(ReductionGraph graph, List<Integer> subset) {
        for (int i = 0; i < subset.size(); i++) {
            for (int j = i + 1; j < subset.size(); j++) {
                int u = subset.get(i);
                int v = subset.get(j);
                if (!graph.adj[u][v]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Finds a clique of size k via backtracking search to demonstrate the reduction equivalence.
     */
    public static List<Integer> findCliqueOfSizeK(ReductionGraph graph) {
        List<Integer> current = new ArrayList<>();
        List<Integer> result = new ArrayList<>();
        backtrack(graph, 0, current, result);
        return result.isEmpty() ? null : result;
    }

    private static boolean backtrack(ReductionGraph graph, int start, List<Integer> current, List<Integer> result) {
        if (current.size() == graph.k) {
            result.addAll(current);
            return true;
        }

        int remainingNeeded = graph.k - current.size();
        if (graph.nodes.size() - start < remainingNeeded) {
            return false;
        }

        for (int i = start; i < graph.nodes.size(); i++) {
            // Check if node i connects to all nodes currently in the clique
            boolean canAdd = true;
            for (int inClique : current) {
                if (!graph.adj[inClique][i]) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                current.add(i);
                if (backtrack(graph, i + 1, current, result)) {
                    return true;
                }
                current.remove(current.size() - 1);
            }
        }
        return false;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // Create 3-SAT formula with 3 clauses (k = 3)
        // C0 = (x1 v x2 v x3)
        // C1 = (~x1 v ~x2 v x3)
        // C2 = (x1 v ~x3 v x2)
        SAT3SAT sat = new SAT3SAT(3);
        sat.addClause(1, 2, 3);
        sat.addClause(-1, -2, 3);
        sat.addClause(1, -3, 2);

        System.out.println("Input 3-SAT Formula:");
        for (SAT3SAT.Clause c : sat.getClauses()) {
            System.out.println("  " + c);
        }

        System.out.println("\nPerforming Polynomial Reduction 3-SAT -> CLIQUE...");
        ReductionGraph rg = reduce(sat);
        System.out.printf("Constructed Graph G: |V| = %d vertices (3 per clause), Target Clique Size k = %d\n",
                rg.nodes.size(), rg.k);

        List<Integer> clique = findCliqueOfSizeK(rg);
        if (clique != null) {
            System.out.println("\nFound k-Clique in Reduction Graph (Size = " + clique.size() + "):");
            for (int id : clique) {
                System.out.println("  " + rg.nodes.get(id));
            }
            System.out.println("\nConclusion: The 3-SAT event constraints are SATISFIABLE by the reduction theorem!");
        } else {
            System.out.println("No k-clique found. Formula is unsatisfiable.");
        }
    }
}
