package texthack.flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * MODULE 4 / CO4 - Dinic's Algorithm for Large-Scale Event Flow Networks.
 *
 * Dinic's algorithm significantly speeds up max-flow computation on large graphs:
 *   1. Level Graph Construction (BFS): Assigns each vertex a level = shortest distance from source.
 *   2. Blocking Flow (DFS): Pushes flow along admissible edges (level[v] == level[u] + 1)
 *      until no more flow can reach the sink.
 *   3. Dead-end Elimination: Maintains an array 'ptr[u]' pointing to the first usable edge from u,
 *      avoiding re-exploration of saturated or blocked paths.
 *
 * Properties:
 *   - Number of phases is at most V - 1 because level[sink] strictly increases each phase.
 *   - Each phase takes O(V * E) time, leading to O(V^2 * E) total time.
 *   - On unit networks (capacities = 1), runs in O(E * sqrt(V)).
 *
 * EventFlow use: Handling large-scale multi-venue city festivals, transit lines, and complex
 * multi-stage exhibition floor plans with hundreds or thousands of interconnected pathways.
 *
 * Time  : O(V^2 * E) general, O(E * sqrt(V)) unit networks
 * Space : O(V + E)
 */
public class Dinic {

    public static class Edge {
        public int to;
        public int rev;
        public int capacity;
        public int flow;

        public Edge(int to, int rev, int capacity) {
            this.to = to;
            this.rev = rev;
            this.capacity = capacity;
            this.flow = 0;
        }
    }

    private int vertices;
    private List<Edge>[] adj;
    private int[] level;
    private int[] ptr;

    @SuppressWarnings("unchecked")
    public Dinic(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList[vertices];
        for (int i = 0; i < vertices; i++) {
            adj[i] = new ArrayList<>();
        }
        level = new int[vertices];
        ptr = new int[vertices];
    }

    public void addEdge(int from, int to, int cap) {
        Edge a = new Edge(to, adj[to].size(), cap);
        Edge b = new Edge(from, adj[from].size(), 0);
        adj[from].add(a);
        adj[to].add(b);
    }

    private boolean bfs(int s, int t) {
        Arrays.fill(level, -1);
        level[s] = 0;
        Queue<Integer> q = new ArrayDeque<>();
        q.add(s);

        while (!q.isEmpty()) {
            int u = q.poll();
            for (Edge e : adj[u]) {
                if (e.capacity - e.flow > 0 && level[e.to] == -1) {
                    level[e.to] = level[u] + 1;
                    q.add(e.to);
                }
            }
        }
        return level[t] != -1;
    }

    private int dfs(int u, int t, int pushed) {
        if (pushed == 0 || u == t) {
            return pushed;
        }

        for (; ptr[u] < adj[u].size(); ptr[u]++) {
            Edge e = adj[u].get(ptr[u]);
            int tr = e.to;

            if (level[u] + 1 != level[tr] || e.capacity - e.flow == 0) {
                continue;
            }

            int push = dfs(tr, t, Math.min(pushed, e.capacity - e.flow));
            if (push == 0) {
                continue;
            }

            e.flow += push;
            adj[tr].get(e.rev).flow -= push;
            return push;
        }

        return 0;
    }

    /** Computes maximum participant flow using Dinic's blocking flow algorithm. */
    public int maxFlow(int s, int t) {
        int flow = 0;
        while (bfs(s, t)) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dfs(s, t, Integer.MAX_VALUE);
                if (pushed == 0) {
                    break;
                }
                flow += pushed;
            }
        }
        return flow;
    }

    public List<Edge>[] getGraph() {
        return adj;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // Large event transit network
        // 0: Metro Station
        // 1: East Gate
        // 2: West Gate
        // 3: South Gate
        // 4: Central Plaza
        // 5: Tech Expo Hub
        // 6: Cultural Stage
        // 7: Main Stadium (Sink)
        Dinic eventNetwork = new Dinic(8);

        eventNetwork.addEdge(0, 1, 100);
        eventNetwork.addEdge(0, 2, 80);
        eventNetwork.addEdge(0, 3, 60);

        eventNetwork.addEdge(1, 4, 70);
        eventNetwork.addEdge(2, 4, 50);
        eventNetwork.addEdge(3, 4, 40);

        eventNetwork.addEdge(1, 5, 40);
        eventNetwork.addEdge(2, 5, 40);
        eventNetwork.addEdge(3, 6, 30);

        eventNetwork.addEdge(4, 7, 120);
        eventNetwork.addEdge(5, 7, 60);
        eventNetwork.addEdge(6, 7, 40);

        int totalCrowd = eventNetwork.maxFlow(0, 7);
        System.out.println("Dinic's Algorithm - Max Event Flow Result:");
        System.out.println("Maximum crowd evacuation / throughput capacity: " + totalCrowd + " persons/minute");
    }
}
