package texthack.flow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

/**
 * MODULE 4 / CO4 - König's Theorem for Assignment Conflict Analysis.
 *
 * König's Theorem States:
 *   In any bipartite graph, the size of a Maximum Matching equals the size of a
 *   Minimum Vertex Cover:
 *       |Max Matching| == |Min Vertex Cover|
 *
 * Furthermore:
 *   |Max Independent Set| = |V| - |Min Vertex Cover|
 *
 * Construction of Min Vertex Cover from Max Matching:
 *   1. Orient edges:
 *      - Unmatched edges go from Left (Staff) to Right (Activities).
 *      - Matched edges go from Right (Activities) to Left (Staff).
 *   2. Start BFS/DFS from all UNMATCHED vertices in Left.
 *   3. Let Z be the set of all reachable vertices.
 *   4. Min Vertex Cover K = (Left \ Z) U (Right ∩ Z).
 *      - Exactly covers all conflict/eligibility edges with the fewest possible nodes.
 *
 * EventFlow use: Pinpointing the exact core bottleneck set of staff/activities causing
 * scheduling gridlock and assignment conflicts.
 *
 * Time  : O(V * E)
 * Space : O(V + E)
 */
public class KonigTheorem {

    public static class KonigResult {
        public int matchingSize;
        public List<Integer> minVertexCoverStaff = new ArrayList<>();
        public List<Integer> minVertexCoverActivities = new ArrayList<>();
        public List<Integer> maxIndependentSetStaff = new ArrayList<>();
        public List<Integer> maxIndependentSetActivities = new ArrayList<>();
    }

    /**
     * Solves and analyzes conflicts using König's Theorem.
     */
    public static KonigResult analyzeConflicts(BipartiteMatching bm) {
        int nStaff = bm.getNumStaff();
        int nActivities = bm.getNumActivities();
        boolean[][] eligible = bm.getEligibilityMatrix();

        int maxMatch = bm.maxMatching();
        int[] matchActivity = bm.getAssignments(); // matchActivity[v] = staff u

        int[] matchStaff = new int[nStaff];
        Arrays.fill(matchStaff, -1);
        for (int v = 0; v < nActivities; v++) {
            if (matchActivity[v] != -1) {
                matchStaff[matchActivity[v]] = v;
            }
        }

        // BFS from unmatched staff members along alternating paths
        boolean[] visitedStaff = new boolean[nStaff];
        boolean[] visitedActivity = new boolean[nActivities];
        Queue<Integer> queue = new ArrayDeque<>();

        for (int u = 0; u < nStaff; u++) {
            if (matchStaff[u] == -1) { // unmatched staff
                visitedStaff[u] = true;
                queue.add(u);
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < nActivities; v++) {
                // If there is an edge and v hasn't been reached yet
                if (eligible[u][v] && !visitedActivity[v]) {
                    // Traversing unmatched edge u -> v
                    if (matchStaff[u] != v) {
                        visitedActivity[v] = true;
                        int matchedU = matchActivity[v];
                        if (matchedU != -1 && !visitedStaff[matchedU]) {
                            // Traversing matched edge v -> matchedU
                            visitedStaff[matchedU] = true;
                            queue.add(matchedU);
                        }
                    }
                }
            }
        }

        KonigResult result = new KonigResult();
        result.matchingSize = maxMatch;

        // Min Vertex Cover:
        // Staff NOT in Z (Left \ Z)
        for (int u = 0; u < nStaff; u++) {
            if (!visitedStaff[u]) {
                result.minVertexCoverStaff.add(u);
            } else {
                result.maxIndependentSetStaff.add(u);
            }
        }
        // Activities IN Z (Right ∩ Z)
        for (int v = 0; v < nActivities; v++) {
            if (visitedActivity[v]) {
                result.minVertexCoverActivities.add(v);
            } else {
                result.maxIndependentSetActivities.add(v);
            }
        }

        return result;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        String[] staff = {"Staff 0", "Staff 1", "Staff 2", "Staff 3"};
        String[] activities = {"Task 0", "Task 1", "Task 2", "Task 3"};

        BipartiteMatching bm = new BipartiteMatching(4, 4);
        bm.addEligibility(0, 0);
        bm.addEligibility(0, 1);
        bm.addEligibility(1, 1);
        bm.addEligibility(2, 1);
        bm.addEligibility(2, 2);
        bm.addEligibility(3, 2);

        KonigResult res = analyzeConflicts(bm);

        System.out.println("König's Theorem Conflict Analysis:");
        System.out.println("Maximum Matching Size : " + res.matchingSize);
        int mvcSize = res.minVertexCoverStaff.size() + res.minVertexCoverActivities.size();
        System.out.println("Minimum Vertex Cover Size : " + mvcSize + " (Verified: " + (res.matchingSize == mvcSize) + ")");

        System.out.println("\nMinimum Vertex Cover Nodes (Root Bottlenecks):");
        for (int s : res.minVertexCoverStaff) {
            System.out.println("  " + staff[s] + " [Staff]");
        }
        for (int a : res.minVertexCoverActivities) {
            System.out.println("  " + activities[a] + " [Activity]");
        }

        System.out.println("\nMaximum Independent Set (Mutually Conflict-Free Subset):");
        for (int s : res.maxIndependentSetStaff) {
            System.out.println("  " + staff[s] + " [Staff]");
        }
        for (int a : res.maxIndependentSetActivities) {
            System.out.println("  " + activities[a] + " [Activity]");
        }
    }
}
