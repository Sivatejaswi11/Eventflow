# EventFlow – Large-Scale Event Management Platform
Advanced Data Structures and Algorithms (DSA) — Complete 6 Modules (CO1 – CO6).

## High-Level Description
**EventFlow** models a comprehensive event-management platform that manages:
- Events, participants, and registrations
- Venues and available facilities
- Event schedules and activity information
- Staff and volunteer resources
- Continuous registration streams and event-activity data

The system processes large-scale event information and supports:
1. **Event Searching**: High-throughput single & multi-pattern text scanning.
2. **Document Indexing**: Linear-time and logarithmic suffix arrays and suffix automata.
3. **Schedule Optimization**: Sequence alignment, matrix chaining, and bitmask TSP.
4. **Resource Allocation**: Max-flow networks, bipartite volunteer matching, and bottleneck cut identification.
5. **Constraint Handling**: 3-SAT constraint solving and polynomial reductions to Clique, Independent Set, and Vertex Cover.
6. **Real-Time Event Analytics**: Work-efficient parallel prefix scans, randomized quicksort ranking, streaming reservoir sampling, and Brent's parallel processing.

---

## Where Each DSA Fits (Course Outcomes CO1 – CO6)

### Module 1: String Algorithms (M1 – CO1)
* **KMP (Knuth-Morris-Pratt)**: Search event names, registration codes, and activity names with O(n + m) time using failure functions (LPS array).
* **Z-Function**: Identify repeated event descriptions and substrings in O(n + m) time.
* **Rabin-Karp**: Search participant and booking codes using rolling polynomial hashing in O(n + m) average time.
* **Aho-Corasick**: Detect multiple event-related keywords in a single linear pass using a trie with BFS failure and output transitions.
* **Naive Search**: Baseline exact substring search.

### Module 2: Suffix Structures – Document Similarity (M2 – CO2)
* **Suffix Arrays (Prefix Doubling)**: Index event descriptions and documents in O(n log² n) time.
* **SA-IS (Suffix Array Induced Sorting)**: Large-scale event-text indexing in strictly linear O(n) time using S/L-type classification and LMS substrings.
* **LCP / Kasai**: Compute the Longest Common Prefix array in O(n) to identify common event descriptions and duplicate text.
* **Suffix Automaton (SAM)**: Minimal DFA accepting all suffixes of event text in O(n) time and O(m) substring search / Longest Common Substring (LCS).

### Module 3: Advanced Dynamic Programming (M3 – CO3)
* **Levenshtein Distance**: Spelling correction of participant names and event tags in O(n·m).
* **Damerau-Levenshtein Distance**: Correction of accidental character transpositions / typing-slips in O(n·m).
* **Weighted Edit Distance**: Asymmetric penalty edit costs for critical registration code alterations.
* **Needleman-Wunsch**: Global alignment comparing two full event schedules end-to-end.
* **Smith-Waterman**: Local alignment finding the best matching shared block of activities between schedules.
* **Matrix-Chain Multiplication**: Optimizing the execution order of data-processing and report-generation pipelines in O(n³).
* **Optimal Binary Search Tree (OBST)**: Minimizing expected search cost for frequently accessed event catalog entries in O(n³).
* **Bitmask TSP**: Optimal ordering and shortest transit tour across multiple event venues in O(2ⁿ·n²).
* **Hamiltonian Path**: Verifying the existence of a valid schedule covering every activity in O(2ⁿ·n²).
* **Sum Over Subsets (SOS DP)**: Aggregating analytics and counts across all sub-combinations of event categories in O(n·2ⁿ).

### Module 4: Network Flow (M4 – CO4)
* **Ford-Fulkerson**: Model participant flow through event checkpoints (gates, security, registration desks, auditoriums) using DFS augmenting paths.
* **Edmonds-Karp**: Venue-capacity analysis guaranteeing O(V·E²) polynomial convergence via BFS shortest augmenting paths; detects 100% saturated corridors.
* **Dinic's Algorithm**: High-throughput blocking flow algorithm on level graphs for large-scale multi-zone festival and transit networks in O(V²·E).
* **Maximum Bipartite Matching**: Optimal 1-to-1 allocation of staff and volunteers to eligible event sessions and activities in O(V·E).
* **König's Theorem**: Analysis of assignment conflicts; derives the Minimum Vertex Cover (bottleneck constraints) and Maximum Independent Set directly from maximum matching.

### Module 5: NP-Completeness and Approximation (M5 – CO5)
* **SAT / 3-SAT Solver**: Representation and resolution of event-scheduling constraints (room capacities, speaker non-overlap) using DPLL with unit propagation.
* **3-SAT -> CLIQUE Reduction**: Polynomial-time Karp reduction transforming event constraints into graph cliques.
* **CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER**: Formal reductions applied to activity conflicts:
  * *Maximum Independent Set*: Maximum simultaneous conflict-free activities.
  * *Minimum Vertex Cover*: Minimum supervised activities required to monitor all conflicts.
* **Vertex-Cover 2-Approximation**: Maximal matching edge-picking algorithm providing guaranteed O(V + E) 2-approximation (`|C| <= 2 * OPT`) for resource conflict coverage.

### Module 6: Randomized and Parallel Algorithms (M6 – CO6)
* **Randomized QuickSort**: Sorting and ranking event attendees and participants in expected O(n log n) time with high probability, immune to sorted/adversarial orders.
* **Reservoir Sampling (Algorithm R)**: Uniform random sampling of `k` attendees from continuous, unbounded registration streams of unknown total size `n` with exact probability `k/n`.
* **Miller-Rabin Primality Test**: Probabilistic primality verification in O(k log³ n) with error `< 4⁻ᵏ` for cryptographic ticketing tokens and secure QR codes.
* **Blelloch Parallel Scan**: Work-efficient parallel prefix sum (Up-Sweep reduce + Down-Sweep) computing cumulative gate arrivals in O(n) work and O(log n) parallel span.
* **Parallel Reduce**: Tree-based parallel aggregation using Java's `ForkJoinPool` to compute overall attendance (SUM), peak crowd (MAX), and lowest turnout (MIN) in O(n) work and O(log n) span.
* **Brent's Theorem**: Evaluating parallel scheduling speedup `T_p <= (W - S)/p + S`, processor allocation, and efficiency `E_p` across multi-stage event processing workloads.

---

## Folder Structure

```
EventFlow/
├── .project                     <- Eclipse project configuration
├── .classpath                   <- Eclipse classpath definition
├── README.md                    <- System overview & module documentation
├── ALGORITHM_GUIDE.md           <- Detailed exam-style guide for all topics
├── bin/                         <- Compiled bytecode (.class)
└── src/
    ├── datasets/
    │   ├── event1.txt
    │   ├── event2.txt
    │   └── event3.txt
    └── texthack/
        ├── Article.java         <- Document representation
        ├── Corpus.java          <- Corpus loader
        ├── EventFlow.java       <- Unified console application (Options 1–34)
        ├── stringalgorithms/    <- Module 1 (CO1)
        │   ├── NaiveSearch.java
        │   ├── KMP.java
        │   ├── ZFunction.java
        │   ├── RabinKarp.java
        │   └── AhoCorasick.java
        ├── suffix/              <- Module 2 (CO2)
        │   ├── SuffixArray.java
        │   ├── SAIS.java
        │   ├── Kasai.java
        │   └── SuffixAutomaton.java
        ├── dp/                  <- Module 3 (CO3)
        │   ├── Levenshtein.java
        │   ├── DamerauLevenshtein.java
        │   ├── WeightedEditDistance.java
        │   ├── NeedlemanWunsch.java
        │   ├── SmithWaterman.java
        │   ├── MatrixChainMultiplication.java
        │   ├── OptimalBST.java
        │   ├── BitmaskTSP.java
        │   ├── HamiltonianPath.java
        │   └── SOSDP.java
        ├── flow/                <- Module 4 (CO4)
        │   ├── FordFulkerson.java
        │   ├── EdmondsKarp.java
        │   ├── Dinic.java
        │   ├── BipartiteMatching.java
        │   └── KonigTheorem.java
        ├── np/                  <- Module 5 (CO5)
        │   ├── SAT3SAT.java
        │   ├── SATToClique.java
        │   ├── CliqueToIndependentSetToVertexCover.java
        │   └── VertexCoverApproximation.java
        └── parallel/            <- Module 6 (CO6)
            ├── RandomizedQuickSort.java
            ├── ReservoirSampling.java
            ├── MillerRabin.java
            ├── BlellochScan.java
            ├── ParallelReduce.java
            └── BrentsTheorem.java
```

---

## How to Run in Eclipse IDE

1. **Open Eclipse IDE**:
   * Select your workspace directory.
2. **Import the Project**:
   * Click **File** → **Import...**
   * Under **General**, choose **Existing Projects into Workspace** and click **Next**.
   * In **Select root directory**, click **Browse...** and choose the `EventFlow` folder:
     `...\EventFlow`
   * Ensure `EventFlow` is checked under **Projects** and click **Finish**.
3. **Verify Build Path**:
   * Eclipse automatically detects the `.project` and `.classpath` files.
   * `src` is marked as the **Source Folder**, and output goes to `bin`.
   * Standard JRE System Library (Java SE 11+) is linked automatically.
4. **Run the Application**:
   * In the Package Explorer, expand `src` → `texthack`.
   * Right-click `EventFlow.java` → **Run As** → **Java Application**.
   * The interactive console menu appears in Eclipse's **Console** tab with all 34 options!
5. **Testing Individual Algorithm Classes**:
   * Every single algorithm file includes its own self-contained `main` method.
   * Right-click any `.java` file (e.g. `FordFulkerson.java`, `BlellochScan.java`, `SAIS.java`) → **Run As** → **Java Application**.

---

## Command Line Execution

### Compile:
```powershell
javac -d bin $(Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

### Run Main EventFlow Application:
```powershell
java -cp bin texthack.EventFlow
```

### Run Specific Algorithms Directly:
```powershell
java -cp bin texthack.flow.FordFulkerson
java -cp bin texthack.flow.KonigTheorem
java -cp bin texthack.np.SAT3SAT
java -cp bin texthack.np.VertexCoverApproximation
java -cp bin texthack.parallel.BlellochScan
java -cp bin texthack.parallel.ParallelReduce
java -cp bin texthack.parallel.BrentsTheorem
```

---

## Complete Menu & Algorithm Mapping Table

| # | Menu Item | Module | Class | Time Complexity | Space Complexity |
|---|-----------|--------|-------|-----------------|------------------|
| 1 | Naive Search | M1 | `stringalgorithms.NaiveSearch` | O(n·m) | O(1) |
| 2 | KMP Search | M1 | `stringalgorithms.KMP` | O(n + m) | O(m) |
| 3 | Z-Function Search | M1 | `stringalgorithms.ZFunction` | O(n + m) | O(n + m) |
| 4 | Rabin-Karp Search | M1 | `stringalgorithms.RabinKarp` | O(n + m) avg | O(1) |
| 5 | Aho-Corasick Multi-Pattern | M1 | `stringalgorithms.AhoCorasick` | O(n + Σm + matches) | O(Σm·σ) |
| 6 | Suffix Array (Prefix Doubling) | M2 | `suffix.SuffixArray` | O(n log² n) | O(n) |
| 7 | SA-IS Linear Suffix Array | M2 | `suffix.SAIS` | O(n) linear | O(n) |
| 8 | LCP / Kasai | M2 | `suffix.Kasai` | O(n) | O(n) |
| 9 | Suffix Automaton (SAM) | M2 | `suffix.SuffixAutomaton` | O(n) build, O(m) search | O(n·σ) |
| 10 | Levenshtein Distance | M3 | `dp.Levenshtein` | O(n·m) | O(n·m) |
| 11 | Damerau-Levenshtein | M3 | `dp.DamerauLevenshtein` | O(n·m) | O(n·m) |
| 12 | Weighted Edit Distance | M3 | `dp.WeightedEditDistance` | O(n·m) | O(n·m) |
| 13 | Needleman-Wunsch Alignment | M3 | `dp.NeedlemanWunsch` | O(n·m) | O(n·m) |
| 14 | Smith-Waterman Alignment | M3 | `dp.SmithWaterman` | O(n·m) | O(n·m) |
| 15 | Matrix-Chain Multiplication | M3 | `dp.MatrixChainMultiplication` | O(n³) | O(n²) |
| 16 | Optimal BST | M3 | `dp.OptimalBST` | O(n³) | O(n²) |
| 17 | Bitmask TSP | M3 | `dp.BitmaskTSP` | O(2ⁿ·n²) | O(2ⁿ·n) |
| 18 | Hamiltonian Path | M3 | `dp.HamiltonianPath` | O(2ⁿ·n²) | O(2ⁿ·n) |
| 19 | SOS DP (Sum Over Subsets) | M3 | `dp.SOSDP` | O(n·2ⁿ) | O(2ⁿ) |
| 20 | Ford-Fulkerson Max Flow | M4 | `flow.FordFulkerson` | O(E·max_f) | O(V²) |
| 21 | Edmonds-Karp Capacity Analysis | M4 | `flow.EdmondsKarp` | O(V·E²) | O(V²) |
| 22 | Dinic's Algorithm | M4 | `flow.Dinic` | O(V²·E) | O(V + E) |
| 23 | Maximum Bipartite Matching | M4 | `flow.BipartiteMatching` | O(V·E) | O(V + E) |
| 24 | König's Theorem Conflict Analysis | M4 | `flow.KonigTheorem` | O(V·E) | O(V + E) |
| 25 | 3-SAT DPLL Solver | M5 | `np.SAT3SAT` | O(2ⁿ) worst | O(n + m) |
| 26 | 3-SAT -> CLIQUE Reduction | M5 | `np.SATToClique` | O(k²) reduction | O(k²) |
| 27 | CLIQUE -> IS -> Vertex Cover | M5 | `np.CliqueToIndependentSetToVertexCover` | O(V²) reduction | O(V²) |
| 28 | Vertex-Cover 2-Approximation | M5 | `np.VertexCoverApproximation` | O(V + E) | O(V + E) |
| 29 | Randomized QuickSort | M6 | `parallel.RandomizedQuickSort` | O(n log n) exp | O(log n) |
| 30 | Reservoir Sampling | M6 | `parallel.ReservoirSampling` | O(n) single pass | O(k) |
| 31 | Miller-Rabin Primality Test | M6 | `parallel.MillerRabin` | O(k log³ n) | O(log n) |
| 32 | Blelloch Parallel Scan | M6 | `parallel.BlellochScan` | Work O(n), Span O(log n) | O(n) |
| 33 | Parallel Tree Reduce | M6 | `parallel.ParallelReduce` | Work O(n), Span O(log n) | O(log n) |
| 34 | Brent's Theorem Scheduling | M6 | `parallel.BrentsTheorem` | O(S) analysis | O(S) |
