package texthack.np;

import java.util.ArrayList;
import java.util.List;

/**
 * MODULE 5 / CO5 - Karp Reductions: CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER.
 *
 * Demonstrates the fundamental structural equivalences among NP-Complete problems:
 *
 * Reduction 1: CLIQUE -> INDEPENDENT-SET
 *   Theorem: S is a Clique in graph G = (V, E) IF AND ONLY IF S is an Independent Set
 *            in the complement graph G_bar = (V, E_bar).
 *   Proof: In a clique, every pair of vertices in S has an edge in G. Therefore, in the
 *          complement graph G_bar, no two vertices in S share an edge -> S is independent!
 *
 * Reduction 2: INDEPENDENT-SET -> VERTEX-COVER
 *   Theorem: S is an Independent Set in graph H = (V, E) IF AND ONLY IF V \ S is a
 *            Vertex Cover in H.
 *   Proof: If S is independent, no edge has both endpoints in S. Thus, every edge in E
 *          must have at least one endpoint outside S (in V \ S) -> V \ S covers all edges!
 *          Conversely, if V \ S is a vertex cover, every edge has an endpoint in V \ S,
 *          so no edge can be entirely inside S -> S is independent.
 *
 * EventFlow Activity Conflict Application:
 *   - Activity Conflict Graph: vertices = event sessions; edges = resource/time clashes.
 *   - Maximum Independent Set = The maximum number of activities that can run SIMULTANEOUSLY
 *     with ZERO conflicts.
 *   - Minimum Vertex Cover = The minimum set of activities/rooms that must be reassigned or
 *     actively monitored to eliminate ALL schedule conflicts across the entire event.
 *
 * Reduction Time  : O(V^2)
 * Space Complexity: O(V^2)
 */
public class CliqueToIndependentSetToVertexCover {

    public static class Graph {
        public int n;
        public String[] names;
        public boolean[][] adj;

        public Graph(int n, String[] names) {
            this.n = n;
            this.names = names;
            this.adj = new boolean[n][n];
        }

        public void addEdge(int u, int v) {
            if (u != v) {
                adj[u][v] = true;
                adj[v][u] = true;
            }
        }

        /** Computes the complement graph G_bar where edge exists iff absent in G. */
        public Graph complement() {
            Graph comp = new Graph(n, names);
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (!this.adj[i][j]) {
                        comp.addEdge(i, j);
                    }
                }
            }
            return comp;
        }
    }

    /**
     * Verifies if subset S forms an Independent Set in graph.
     */
    public static boolean isIndependentSet(Graph g, List<Integer> subset) {
        for (int i = 0; i < subset.size(); i++) {
            for (int j = i + 1; j < subset.size(); j++) {
                int u = subset.get(i);
                int v = subset.get(j);
                if (g.adj[u][v]) return false;
            }
        }
        return true;
    }

    /**
     * Verifies if subset C forms a Vertex Cover in graph (covers every edge).
     */
    public static boolean isVertexCover(Graph g, List<Integer> subset) {
        boolean[] inCover = new boolean[g.n];
        for (int v : subset) {
            inCover[v] = true;
        }

        for (int u = 0; u < g.n; u++) {
            for (int v = u + 1; v < g.n; v++) {
                if (g.adj[u][v]) {
                    if (!inCover[u] && !inCover[v]) {
                        return false; // uncovered edge!
                    }
                }
            }
        }
        return true;
    }

    /**
     * Transforms an Independent Set S into its complementary Vertex Cover V \ S.
     */
    public static List<Integer> independentSetToVertexCover(Graph g, List<Integer> indSet) {
        boolean[] inSet = new boolean[g.n];
        for (int v : indSet) {
            inSet[v] = true;
        }

        List<Integer> vertexCover = new ArrayList<>();
        for (int i = 0; i < g.n; i++) {
            if (!inSet[i]) {
                vertexCover.add(i);
            }
        }
        return vertexCover;
    }

    /**
     * Transforms a Vertex Cover C into its complementary Independent Set V \ C.
     */
    public static List<Integer> vertexCoverToIndependentSet(Graph g, List<Integer> vertexCover) {
        boolean[] inCover = new boolean[g.n];
        for (int v : vertexCover) {
            inCover[v] = true;
        }

        List<Integer> indSet = new ArrayList<>();
        for (int i = 0; i < g.n; i++) {
            if (!inCover[i]) {
                indSet.add(i);
            }
        }
        return indSet;
    }

    /**
     * Finds the Maximum Independent Set in graph using exponential branch search.
     */
    public static List<Integer> findMaxIndependentSet(Graph g) {
        List<Integer> best = new ArrayList<>();
        findIndSetBacktrack(g, 0, new ArrayList<>(), best);
        return best;
    }

    private static void findIndSetBacktrack(Graph g, int idx, List<Integer> current, List<Integer> best) {
        if (current.size() > best.size()) {
            best.clear();
            best.addAll(current);
        }

        if (idx == g.n) return;
        if (current.size() + (g.n - idx) <= best.size()) return; // branch and bound

        // Option 1: Try including vertex idx if independent
        boolean canInclude = true;
        for (int v : current) {
            if (g.adj[idx][v]) {
                canInclude = false;
                break;
            }
        }

        if (canInclude) {
            current.add(idx);
            findIndSetBacktrack(g, idx + 1, current, best);
            current.remove(current.size() - 1);
        }

        // Option 2: Exclude vertex idx
        findIndSetBacktrack(g, idx + 1, current, best);
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        String[] sessions = {
            "Keynote (Hall A)",       // 0
            "AI Demo (Hall A)",       // 1 - conflicts with 0
            "Robotics Lab (Hall B)",  // 2
            "Startup Pitch (Hall B)", // 3 - conflicts with 2
            "Networking Lunch",       // 4 - conflicts with 0, 1, 2, 3
            "Hackathon Coding"        // 5 - standalone
        };

        Graph conflictGraph = new Graph(6, sessions);
        conflictGraph.addEdge(0, 1); // room clash
        conflictGraph.addEdge(2, 3); // room clash
        conflictGraph.addEdge(0, 4); // time clash
        conflictGraph.addEdge(1, 4);
        conflictGraph.addEdge(2, 4);
        conflictGraph.addEdge(3, 4);

        System.out.println("Event Activity Conflict Graph:");
        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                if (conflictGraph.adj[i][j]) {
                    System.out.printf("  Conflict: %s <---> %s\n", sessions[i], sessions[j]);
                }
            }
        }

        // Maximum Independent Set (Compatible simultaneous sessions)
        List<Integer> maxIndSet = findMaxIndependentSet(conflictGraph);
        System.out.println("\n1. Maximum Independent Set (Max Conflict-Free Parallel Schedule, size " + maxIndSet.size() + "):");
        for (int v : maxIndSet) {
            System.out.println("   * " + sessions[v]);
        }

        // Vertex Cover via reduction: V \ MaxIndependentSet
        List<Integer> minVC = independentSetToVertexCover(conflictGraph, maxIndSet);
        System.out.println("\n2. Minimum Vertex Cover via V \\ IndependentSet (size " + minVC.size() + "):");
        for (int v : minVC) {
            System.out.println("   * " + sessions[v]);
        }
        System.out.println("   Valid Vertex Cover check: " + isVertexCover(conflictGraph, minVC));

        // Complement graph clique connection
        Graph comp = conflictGraph.complement();
        System.out.println("\n3. Dual Relationship: Max Clique in Complement Graph G_bar:");
        System.out.println("   Size of Clique in G_bar equals Max Independent Set in G: " + maxIndSet.size());
    }
}
