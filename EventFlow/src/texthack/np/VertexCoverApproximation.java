package texthack.np;

import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 5 / CO5 - Vertex-Cover 2-Approximation Algorithm.
 *
 * Problem:
 *   Minimum Vertex Cover is NP-hard. In large-scale event management, computing the exact
 *   optimum by brute-force is intractable (O(2^V)).
 *   A polynomial-time approximation algorithm guarantees a solution within a bounded factor
 *   of the optimum.
 *
 * Algorithm (Maximal Matching 2-Approximation):
 *   1. Initialize cover set C = empty.
 *   2. While there exists an edge e = (u, v) in E not yet covered:
 *      a. Add BOTH u and v to C: C = C U {u, v}.
 *      b. Remove / mark covered all edges incident to either u or v.
 *   3. Return C.
 *
 * Mathematical Proof of 2-Approximation Ratio (C <= 2 * OPT):
 *   - Let M be the set of edges selected in step 2a.
 *   - By construction, once edge (u, v) is selected, all other edges incident to u or v
 *     are discarded. Thus, no two edges in M share an endpoint -> M is a MATCHING!
 *   - Any valid vertex cover C* (including OPT) MUST pick at least one endpoint from
 *     every edge in M to cover that edge. Because edges in M are disjoint:
 *         |OPT| >= |M|
 *   - The algorithm selects exactly 2 vertices for each edge in M:
 *         |C| = 2 * |M|
 *   - Combining the two inequalities:
 *         |C| = 2 * |M| <= 2 * |OPT|
 *   - Hence, the algorithm is a tight 2-approximation with O(V + E) runtime!
 *
 * EventFlow use: Identifying a compact set of supervisors or monitoring stations
 * that cover all simultaneous equipment and venue conflict edges.
 *
 * Time  : O(V + E)
 * Space : O(V + E)
 */
public class VertexCoverApproximation {

    public static class Edge {
        public int u;
        public int v;

        public Edge(int u, int v) {
            this.u = u;
            this.v = v;
        }

        @Override
        public String toString() {
            return "(" + u + " - " + v + ")";
        }
    }

    public static class ApproxResult {
        public List<Integer> cover = new ArrayList<>();
        public List<Edge> selectedMatchingEdges = new ArrayList<>();
        public int totalVertices;
        public int totalEdges;
    }

    /**
     * Computes a 2-approximate vertex cover in O(V + E) time.
     */
    public static ApproxResult solve2Approx(int n, List<Edge> edges) {
        boolean[] inCover = new boolean[n];
        boolean[] edgeCovered = new boolean[edges.size()];
        ApproxResult result = new ApproxResult();
        result.totalVertices = n;
        result.totalEdges = edges.size();

        for (int i = 0; i < edges.size(); i++) {
            if (!edgeCovered[i]) {
                Edge e = edges.get(i);

                // If neither endpoint is in cover, pick this edge
                if (!inCover[e.u] && !inCover[e.v]) {
                    inCover[e.u] = true;
                    inCover[e.v] = true;
                    result.cover.add(e.u);
                    result.cover.add(e.v);
                    result.selectedMatchingEdges.add(e);

                    // Mark all incident edges as covered
                    for (int j = i; j < edges.size(); j++) {
                        Edge other = edges.get(j);
                        if (other.u == e.u || other.u == e.v || other.v == e.u || other.v == e.v) {
                            edgeCovered[j] = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        String[] resources = {
            "Stage A Projector",  // 0
            "Stage B Audio Deck", // 1
            "Keynote Mic 1",      // 2
            "Keynote Mic 2",      // 3
            "VR Headset Station", // 4
            "Livestream Encoder", // 5
            "Main Lighting Rig"   // 6
        };

        // Conflicts between resources (shared power or overlapping operators)
        List<Edge> conflictEdges = new ArrayList<>();
        conflictEdges.add(new Edge(0, 1));
        conflictEdges.add(new Edge(1, 2));
        conflictEdges.add(new Edge(2, 3));
        conflictEdges.add(new Edge(3, 4));
        conflictEdges.add(new Edge(4, 5));
        conflictEdges.add(new Edge(5, 6));
        conflictEdges.add(new Edge(1, 5));

        System.out.println("Resource Conflict Graph (" + resources.length + " nodes, " + conflictEdges.size() + " conflict edges):");
        for (Edge e : conflictEdges) {
            System.out.printf("  Conflict: [%s] <---> [%s]\n", resources[e.u], resources[e.v]);
        }

        ApproxResult res = solve2Approx(resources.length, conflictEdges);

        System.out.println("\n2-Approximation Vertex Cover Result:");
        System.out.println("Disjoint Edges in Maximal Matching (|M|): " + res.selectedMatchingEdges.size());
        for (Edge e : res.selectedMatchingEdges) {
            System.out.printf("  Selected Edge: %s <---> %s\n", resources[e.u], resources[e.v]);
        }

        System.out.println("\nCovering Resource Set Size (|C| = 2*|M|): " + res.cover.size());
        for (int v : res.cover) {
            System.out.println("  * " + resources[v]);
        }

        System.out.println("\nApproximation Guarantee:");
        System.out.println("  Optimal Min Vertex Cover OPT satisfies: " + res.selectedMatchingEdges.size() + " <= OPT <= " + res.cover.size());
        System.out.println("  Approximation ratio bounded by: |C| / OPT <= 2.0");
    }
}
