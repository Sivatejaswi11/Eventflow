package texthack.flow;

/**
 * MODULE 4 / CO4 - Ford-Fulkerson Algorithm.
 *
 * Models participant flow through event facilities:
 *   - Entrances, security checkpoints, registration desks, keynote halls, and exit gates
 *     are modeled as vertices.
 *   - Corridors, queues, and doorways are directed edges with capacities (people/min).
 *   - Augmenting paths in the residual graph are found using Depth-First Search (DFS).
 *   - Flow is augmented along the bottleneck capacity until no s-t augmenting path exists.
 *
 * Max-Flow Min-Cut Theorem:
 *   The value of the maximum flow equals the capacity of the minimum cut (the bottleneck
 *   constricting total participant throughput).
 *
 * EventFlow use: Modeling continuous crowd flow from main entrances to various auditoriums
 * to prevent stampedes and identify facility bottlenecks.
 *
 * Time  : O(E * max_flow)
 * Space : O(V^2) or O(V + E)
 */
public class FordFulkerson {

    private int vertices;
    private int[][] capacity;
    private int[][] flow;
    private boolean[] visited;

    public FordFulkerson(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
        this.flow = new int[vertices][vertices];
        this.visited = new boolean[vertices];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] += cap;
    }

    private int dfs(int u, int t, int push) {
        if (u == t) {
            return push;
        }
        visited[u] = true;

        for (int v = 0; v < vertices; v++) {
            int residual = capacity[u][v] - flow[u][v];
            if (!visited[v] && residual > 0) {
                int nextPush = Math.min(push, residual);
                int tr = dfs(v, t, nextPush);
                if (tr > 0) {
                    flow[u][v] += tr;
                    flow[v][u] -= tr;
                    return tr;
                }
            }
        }
        return 0;
    }

    /**
     * Computes the maximum participant flow from source to sink.
     */
    public int maxFlow(int source, int sink) {
        // Reset flows
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                flow[i][j] = 0;
            }
        }

        int totalFlow = 0;
        while (true) {
            for (int i = 0; i < vertices; i++) {
                visited[i] = false;
            }
            int pushed = dfs(source, sink, Integer.MAX_VALUE);
            if (pushed == 0) {
                break;
            }
            totalFlow += pushed;
        }
        return totalFlow;
    }

    /**
     * Identifies the minimum cut partitions: returns an array where true = in Source set S,
     * false = in Sink set T.
     */
    public boolean[] minCut(int source) {
        boolean[] inS = new boolean[vertices];
        dfsCut(source, inS);
        return inS;
    }

    private void dfsCut(int u, boolean[] inS) {
        inS[u] = true;
        for (int v = 0; v < vertices; v++) {
            if (!inS[v] && (capacity[u][v] - flow[u][v]) > 0) {
                dfsCut(v, inS);
            }
        }
    }

    public int[][] getFlowMatrix() {
        return flow;
    }

    public int[][] getCapacityMatrix() {
        return capacity;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // 6 event checkpoints:
        // 0: Main Gate (Source)
        // 1: Security Screening A
        // 2: Security Screening B
        // 3: Registration Desk 1
        // 4: Registration Desk 2
        // 5: Auditorium Entry (Sink)
        FordFulkerson network = new FordFulkerson(6);
        network.addEdge(0, 1, 16); // Gate -> Security A (cap 16 people/min)
        network.addEdge(0, 2, 13); // Gate -> Security B (cap 13 people/min)
        network.addEdge(1, 2, 10); // Security A -> Security B crossover
        network.addEdge(1, 3, 12); // Security A -> Desk 1
        network.addEdge(2, 1, 4);  // Security B -> Security A crossover
        network.addEdge(2, 4, 14); // Security B -> Desk 2
        network.addEdge(3, 2, 9);  // Desk 1 -> Security B
        network.addEdge(3, 5, 20); // Desk 1 -> Auditorium
        network.addEdge(4, 3, 7);  // Desk 2 -> Desk 1
        network.addEdge(4, 5, 4);  // Desk 2 -> Auditorium

        int maxRate = network.maxFlow(0, 5);
        System.out.println("Maximum Participant Flow Throughput: " + maxRate + " participants/minute");

        boolean[] inS = network.minCut(0);
        System.out.println("\nBottleneck Cut (Min Cut):");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (inS[i] && !inS[j] && network.capacity[i][j] > 0) {
                    System.out.printf("  Bottleneck corridor: Checkpoint %d -> Checkpoint %d (Capacity: %d)\n",
                            i, j, network.capacity[i][j]);
                }
            }
        }
    }
}
