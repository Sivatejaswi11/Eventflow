package texthack.flow;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * MODULE 4 / CO4 - Edmonds-Karp Algorithm for Venue-Capacity Analysis.
 *
 * Implements the Ford-Fulkerson method using Breadth-First Search (BFS) to find
 * the shortest augmenting path (fewest edges) from source to sink in the residual graph.
 *
 * Key Properties:
 *   - Monotonicity: The shortest path distance from source to any node never decreases.
 *   - Guaranteed polynomial termination: Exactly O(V * E^2), completely independent of
 *     edge capacity magnitudes (immune to pathological slow convergence).
 *
 * EventFlow use: Venue-capacity analysis, evaluating the maximum safe entry/exit rate
 * across stadium gates, passageways, and exhibition booths to avoid congestion.
 *
 * Time  : O(V * E^2)
 * Space : O(V^2)
 */
public class EdmondsKarp {

    private int vertices;
    private int[][] capacity;
    private int[][] flow;
    private int[] parent;

    public EdmondsKarp(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
        this.flow = new int[vertices][vertices];
        this.parent = new int[vertices];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] += cap;
    }

    private boolean bfs(int s, int t) {
        for (int i = 0; i < vertices; i++) {
            parent[i] = -1;
        }
        parent[s] = s;

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == t) {
                return true;
            }

            for (int v = 0; v < vertices; v++) {
                if (parent[v] == -1 && (capacity[u][v] - flow[u][v]) > 0) {
                    parent[v] = u;
                    queue.add(v);
                }
            }
        }
        return false;
    }

    /**
     * Computes the maximum venue capacity flow from source to sink.
     */
    public int maxFlow(int s, int t) {
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                flow[i][j] = 0;
            }
        }

        int totalFlow = 0;

        while (bfs(s, t)) {
            // Find bottleneck capacity along the BFS path
            int bottleneck = Integer.MAX_VALUE;
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                bottleneck = Math.min(bottleneck, capacity[u][v] - flow[u][v]);
            }

            // Augment flow
            for (int v = t; v != s; v = parent[v]) {
                int u = parent[v];
                flow[u][v] += bottleneck;
                flow[v][u] -= bottleneck;
            }

            totalFlow += bottleneck;
        }

        return totalFlow;
    }

    /** Identifies saturated edges carrying capacity at 100%. */
    public void printSaturatedPassages() {
        System.out.println("Saturated Passageways (Operating at 100% capacity):");
        boolean found = false;
        for (int u = 0; u < vertices; u++) {
            for (int v = 0; v < vertices; v++) {
                if (capacity[u][v] > 0 && flow[u][v] == capacity[u][v]) {
                    System.out.printf("  Venue corridor [%d -> %d]: %d / %d attendees/min (FULL)\n",
                            u, v, flow[u][v], capacity[u][v]);
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("  None (Network has surplus capacity).");
        }
    }

    public int[][] getFlow() {
        return flow;
    }

    public int[][] getCapacity() {
        return capacity;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // Venue capacity model for a tech summit
        // 0: West Parking / Transit Hub
        // 1: Security Zone 1
        // 2: Security Zone 2
        // 3: Expo Hall A
        // 4: Expo Hall B
        // 5: Main Keynote Arena
        EdmondsKarp venue = new EdmondsKarp(6);
        venue.addEdge(0, 1, 10);
        venue.addEdge(0, 2, 10);
        venue.addEdge(1, 2, 2);
        venue.addEdge(1, 3, 4);
        venue.addEdge(1, 4, 8);
        venue.addEdge(2, 4, 9);
        venue.addEdge(3, 5, 10);
        venue.addEdge(4, 3, 6);
        venue.addEdge(4, 5, 10);

        int maxCapacity = venue.maxFlow(0, 5);
        System.out.println("Max Venue Admittance Rate: " + maxCapacity + " visitors/min");
        venue.printSaturatedPassages();
    }
}
