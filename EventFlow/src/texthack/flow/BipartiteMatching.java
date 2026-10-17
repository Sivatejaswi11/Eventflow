package texthack.flow;

import java.util.Arrays;

/**
 * MODULE 4 / CO4 - Maximum Bipartite Matching for Volunteer & Staff Allocation.
 *
 * Problem:
 *   Given a set of staff/volunteers U and a set of event activities V,
 *   where an edge (u, v) exists if staff member u is qualified/available for activity v.
 *   Find the maximum number of simultaneous 1-to-1 assignments such that no staff member
 *   is double-booked and each activity is managed by at most one staff member.
 *
 * Algorithm:
 *   Augmenting Path algorithm (Berge's Lemma / Ford-Fulkerson specialization on unit bipartite networks).
 *   Uses DFS to find augmenting paths of alternating unmatched and matched edges.
 *
 * EventFlow use: Assigning certified volunteers and coordinators to stage presentations,
 * workshops, security gates, and technical helpdesks based on eligibility constraints.
 *
 * Time  : O(V * E)
 * Space : O(V + E)
 */
public class BipartiteMatching {

    private int numStaff;
    private int numActivities;
    private boolean[][] eligible; // eligible[u][v] = true if staff u can do activity v
    private int[] matchActivity; // matchActivity[v] = staff assigned to activity v (-1 if unassigned)
    private boolean[] visited;

    public BipartiteMatching(int numStaff, int numActivities) {
        this.numStaff = numStaff;
        this.numActivities = numActivities;
        this.eligible = new boolean[numStaff][numActivities];
        this.matchActivity = new int[numActivities];
        this.visited = new boolean[numActivities];
    }

    public void addEligibility(int staffId, int activityId) {
        if (staffId >= 0 && staffId < numStaff && activityId >= 0 && activityId < numActivities) {
            eligible[staffId][activityId] = true;
        }
    }

    private boolean dfs(int u) {
        for (int v = 0; v < numActivities; v++) {
            if (eligible[u][v] && !visited[v]) {
                visited[v] = true;

                // If activity v is not assigned, or its assigned staff can find an alternate activity
                if (matchActivity[v] < 0 || dfs(matchActivity[v])) {
                    matchActivity[v] = u;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Computes the maximum bipartite matching.
     * Returns total number of successfully assigned activities.
     */
    public int maxMatching() {
        Arrays.fill(matchActivity, -1);
        int totalMatches = 0;

        for (int u = 0; u < numStaff; u++) {
            Arrays.fill(visited, false);
            if (dfs(u)) {
                totalMatches++;
            }
        }
        return totalMatches;
    }

    public int[] getAssignments() {
        return matchActivity;
    }

    public boolean[][] getEligibilityMatrix() {
        return eligible;
    }

    public int getNumStaff() {
        return numStaff;
    }

    public int getNumActivities() {
        return numActivities;
    }

    /** Standalone testing demonstration. */
    public static void main(String[] args) {
        // Staff members:
        // 0: Alice, 1: Bob, 2: Charlie, 3: Diana, 4: Ethan
        // Activities:
        // 0: Keynote Audio, 1: AI Workshop, 2: Registration Desk, 3: Hackathon Lab, 4: VIP Lounge
        String[] staff = {"Alice", "Bob", "Charlie", "Diana", "Ethan"};
        String[] activities = {"Keynote Audio", "AI Workshop", "Registration Desk", "Hackathon Lab", "VIP Lounge"};

        BipartiteMatching bm = new BipartiteMatching(5, 5);
        // Alice can do Keynote Audio or VIP Lounge
        bm.addEligibility(0, 0);
        bm.addEligibility(0, 4);
        // Bob can do AI Workshop or Hackathon Lab
        bm.addEligibility(1, 1);
        bm.addEligibility(1, 3);
        // Charlie can do Keynote Audio or AI Workshop
        bm.addEligibility(2, 0);
        bm.addEligibility(2, 1);
        // Diana can do Registration Desk
        bm.addEligibility(3, 2);
        // Ethan can do AI Workshop or VIP Lounge
        bm.addEligibility(4, 1);
        bm.addEligibility(4, 4);

        int count = bm.maxMatching();
        System.out.println("Maximum Assigned Volunteer Pairs: " + count + " / " + activities.length);
        int[] assignments = bm.getAssignments();
        for (int v = 0; v < activities.length; v++) {
            if (assignments[v] != -1) {
                System.out.printf("  Activity '%s' <= Assigned to: %s\n", activities[v], staff[assignments[v]]);
            } else {
                System.out.printf("  Activity '%s' <= [UNFILLED]\n", activities[v]);
            }
        }
    }
}
