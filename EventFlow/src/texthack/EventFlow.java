package texthack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import texthack.stringalgorithms.AhoCorasick;
import texthack.stringalgorithms.KMP;
import texthack.stringalgorithms.NaiveSearch;
import texthack.stringalgorithms.RabinKarp;
import texthack.stringalgorithms.ZFunction;
import texthack.suffix.Kasai;
import texthack.suffix.SAIS;
import texthack.suffix.SuffixArray;
import texthack.suffix.SuffixAutomaton;
import texthack.dp.BitmaskTSP;
import texthack.dp.DamerauLevenshtein;
import texthack.dp.HamiltonianPath;
import texthack.dp.Levenshtein;
import texthack.dp.MatrixChainMultiplication;
import texthack.dp.NeedlemanWunsch;
import texthack.dp.OptimalBST;
import texthack.dp.SOSDP;
import texthack.dp.SmithWaterman;
import texthack.dp.WeightedEditDistance;
import texthack.flow.BipartiteMatching;
import texthack.flow.Dinic;
import texthack.flow.EdmondsKarp;
import texthack.flow.FordFulkerson;
import texthack.flow.KonigTheorem;
import texthack.np.CliqueToIndependentSetToVertexCover;
import texthack.np.SAT3SAT;
import texthack.np.SATToClique;
import texthack.np.VertexCoverApproximation;
import texthack.parallel.BlellochScan;
import texthack.parallel.BrentsTheorem;
import texthack.parallel.MillerRabin;
import texthack.parallel.ParallelReduce;
import texthack.parallel.RandomizedQuickSort;
import texthack.parallel.ReservoirSampling;

/**
 * EventFlow - Large-Scale Event Management Platform.
 * Advanced Algorithms Engine covering all 6 syllabus modules:
 *
 *   MODULE 1 (CO1) - String Algorithms:
 *     - Naive Search, KMP, Z-Function, Rabin-Karp, Aho-Corasick
 *   MODULE 2 (CO2) - Suffix Structures & Document Similarity:
 *     - Suffix Array (Prefix Doubling), SA-IS (Linear Time O(n)), LCP / Kasai, Suffix Automaton
 *   MODULE 3 (CO3) - Advanced Dynamic Programming:
 *     - Levenshtein, Damerau-Levenshtein, Weighted Edit Distance, Needleman-Wunsch,
 *       Smith-Waterman, Matrix-Chain Multiplication, Optimal BST, Bitmask TSP,
 *       Hamiltonian Path, SOS DP
 *   MODULE 4 (CO4) - Network Flow:
 *     - Ford-Fulkerson, Edmonds-Karp, Dinic's Algorithm, Bipartite Matching, König's Theorem
 *   MODULE 5 (CO5) - NP-Completeness and Approximation:
 *     - 3-SAT (DPLL), 3-SAT -> CLIQUE, CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER,
 *       Vertex-Cover 2-Approximation
 *   MODULE 6 (CO6) - Randomized and Parallel Algorithms:
 *     - Randomized QuickSort, Reservoir Sampling, Miller-Rabin Primality,
 *       Blelloch Parallel Scan, Parallel Reduce (ForkJoin), Brent's Theorem
 */
public class EventFlow {

    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        loadCorpus();

        boolean running = true;

        while (running) {

            printMenu();
            System.out.print("Enter your choice (0 to exit) : ");

            int choice = readInt();

            switch (choice) {
                // MODULE 1 (CO1)
                case 1: runNaiveSearch(); break;
                case 2: runKMP(); break;
                case 3: runZFunction(); break;
                case 4: runRabinKarp(); break;
                case 5: runAhoCorasick(); break;

                // MODULE 2 (CO2)
                case 6: runSuffixArray(); break;
                case 7: runSAIS(); break;
                case 8: runKasai(); break;
                case 9: runSuffixAutomaton(); break;

                // MODULE 3 (CO3)
                case 10: runLevenshtein(); break;
                case 11: runDamerauLevenshtein(); break;
                case 12: runWeightedEditDistance(); break;
                case 13: runNeedlemanWunsch(); break;
                case 14: runSmithWaterman(); break;
                case 15: runMatrixChain(); break;
                case 16: runOptimalBST(); break;
                case 17: runBitmaskTSP(); break;
                case 18: runHamiltonianPath(); break;
                case 19: runSOSDP(); break;

                // MODULE 4 (CO4)
                case 20: runFordFulkerson(); break;
                case 21: runEdmondsKarp(); break;
                case 22: runDinic(); break;
                case 23: runBipartiteMatching(); break;
                case 24: runKonigTheorem(); break;

                // MODULE 5 (CO5)
                case 25: runSAT3SAT(); break;
                case 26: runSATToClique(); break;
                case 27: runCliqueToIndependentSetToVertexCover(); break;
                case 28: runVertexCoverApproximation(); break;

                // MODULE 6 (CO6)
                case 29: runRandomizedQuickSort(); break;
                case 30: runReservoirSampling(); break;
                case 31: runMillerRabin(); break;
                case 32: runBlellochScan(); break;
                case 33: runParallelReduce(); break;
                case 34: runBrentsTheorem(); break;

                case 0:
                    running = false;
                    System.out.println("\nExiting EventFlow. Thank you for using the platform!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please enter a valid number (0 to 34).");
            }
        }
    }

    // ------------------------------------------------------------------
    // Corpus + Menu
    // ------------------------------------------------------------------

    private static void loadCorpus() {

        String[] potentialPaths = {
            "src/datasets",
            "datasets",
            "bin/datasets",
            "EventFlow/src/datasets",
            "../src/datasets",
            "../datasets"
        };

        File folder = null;
        for (String path : potentialPaths) {
            File f = new File(path);
            if (f.exists() && f.isDirectory()) {
                folder = f;
                break;
            }
        }

        if (folder == null) {
            System.out.println("Dataset folder not found!");
            System.out.println("Expected : src/datasets");
            System.out.println("Working directory : " + new File(".").getAbsolutePath());
            return;
        }

        Corpus.loadFiles(folder);
    }

    private static void printMenu() {

        System.out.println();
        System.out.println("=================================================================");
        System.out.println("       EVENTFLOW - Large-Scale Event Management Platform");
        System.out.println("          Complete Advanced Algorithm Engine (M1 - M6)");
        System.out.println("=================================================================");
        System.out.println("Articles / Documents Loaded : " + Corpus.repository.size());
        System.out.println();
        System.out.println("--- MODULE 1 (CO1): String Algorithms ---");
        System.out.println("  1. Naive Search (Event names & codes)");
        System.out.println("  2. KMP Search (Registration codes & activities)");
        System.out.println("  3. Z-Function Search (Repeated event text)");
        System.out.println("  4. Rabin-Karp Search (Participant & booking codes)");
        System.out.println("  5. Aho-Corasick Multi-Pattern (Event keywords)");
        System.out.println();
        System.out.println("--- MODULE 2 (CO2): Suffix Structures & Document Similarity ---");
        System.out.println("  6. Suffix Array (Prefix Doubling - O(n log^2 n))");
        System.out.println("  7. SA-IS (Linear Time O(n) Suffix Array Indexing)");
        System.out.println("  8. LCP / Kasai (Common event descriptions)");
        System.out.println("  9. Suffix Automaton (Substring Search & Longest Common Substring)");
        System.out.println();
        System.out.println("--- MODULE 3 (CO3): Advanced Dynamic Programming ---");
        System.out.println(" 10. Levenshtein Distance (Participant name correction)");
        System.out.println(" 11. Damerau-Levenshtein (Typing transposition correction)");
        System.out.println(" 12. Weighted Edit Distance (Custom operation costs)");
        System.out.println(" 13. Needleman-Wunsch (Global schedule comparison)");
        System.out.println(" 14. Smith-Waterman (Shared activity block alignment)");
        System.out.println(" 15. Matrix Chain Multiplication (Optimal event data operations)");
        System.out.println(" 16. Optimal BST (Frequently accessed event info)");
        System.out.println(" 17. Bitmask TSP (Cheapest venue tour / activity sequence)");
        System.out.println(" 18. Hamiltonian Path (All-activity schedule existence)");
        System.out.println(" 19. SOS DP (Sum Over Subsets / Category combinations)");
        System.out.println();
        System.out.println("--- MODULE 4 (CO4): Network Flow ---");
        System.out.println(" 20. Ford-Fulkerson (Participant flow through facilities)");
        System.out.println(" 21. Edmonds-Karp (Venue-capacity & bottleneck analysis)");
        System.out.println(" 22. Dinic's Algorithm (Large-scale event crowd transit networks)");
        System.out.println(" 23. Bipartite Matching (Volunteer & staff allocation)");
        System.out.println(" 24. König's Theorem (Assignment conflict analysis & Min Vertex Cover)");
        System.out.println();
        System.out.println("--- MODULE 5 (CO5): NP-Completeness and Approximation ---");
        System.out.println(" 25. SAT / 3-SAT DPLL (Event scheduling constraints solver)");
        System.out.println(" 26. 3-SAT -> CLIQUE (Polynomial-time constraint transformation)");
        System.out.println(" 27. CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER (Activity conflicts)");
        System.out.println(" 28. Vertex-Cover 2-Approximation (Resource conflict coverage)");
        System.out.println();
        System.out.println("--- MODULE 6 (CO6): Randomized and Parallel Algorithms ---");
        System.out.println(" 29. Randomized QuickSort (Rank events or participants)");
        System.out.println(" 30. Reservoir Sampling (Continuous registration streams)");
        System.out.println(" 31. Miller-Rabin (Cryptographic ticketing primality test)");
        System.out.println(" 32. Blelloch Parallel Scan (Cumulative registration counts)");
        System.out.println(" 33. Parallel Reduce (ForkJoin event analytics statistics)");
        System.out.println(" 34. Brent's Theorem (Parallel event-data processing speedup)");
        System.out.println();
        System.out.println("  0. Exit");
        System.out.println();
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=================================================================");
        System.out.println(title);
        System.out.println("=================================================================");
        System.out.println();
    }

    private static void complexity(String time, String space) {
        System.out.println();
        System.out.println("Time Complexity  : " + time);
        System.out.println("Space Complexity : " + space);
    }

    // ------------------------------------------------------------------
    // Small Input Helpers
    // ------------------------------------------------------------------

    private static int readInt() {
        while (true) {
            String line = input.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number : ");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }

    private static Article chooseArticle() {

        if (Corpus.repository.size() == 0) {
            System.out.println("No documents loaded.");
            return null;
        }

        System.out.println("Available documents:");
        for (Article article : Corpus.repository) {
            System.out.println("  " + article.id + ". " + article.fileName
                    + " (" + article.content.length() + " characters)");
        }
        System.out.print("Choose document id (or press enter for doc 1) : ");

        String raw = input.nextLine().trim();
        int id = 1;
        if (!raw.isEmpty()) {
            try {
                id = Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                id = 1;
            }
        }

        Article chosen = Corpus.getArticle(id);

        if (chosen == null) {
            System.out.println("No such document, using document 1.");
            chosen = Corpus.repository.get(0);
        }
        return chosen;
    }

    private static String preview(String text, int limit) {
        String clean = text.replace('\n', ' ').replace('\r', ' ').trim();
        if (clean.length() <= limit) {
            return clean;
        }
        return clean.substring(0, limit) + " ...";
    }

    private static int[] readIntArray(String prompt) {

        System.out.print(prompt);
        String line = input.nextLine().trim();

        if (line.length() == 0) {
            return new int[0];
        }

        String[] parts = line.split("\\s+");
        int[] values = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            values[i] = Integer.parseInt(parts[i]);
        }
        return values;
    }

    // ==================================================================
    // MODULE 1 (CO1) - STRING ALGORITHMS
    // ==================================================================

    private static void runNaiveSearch() {

        header("NAIVE STRING SEARCH (M1 / CO1)");

        Article article = chooseArticle();
        if (article == null) return;

        String pattern = readLine("Enter pattern : ");

        int index = NaiveSearch.search(article.content, pattern);
        int count = NaiveSearch.countOccurrences(article.content, pattern);

        System.out.println();
        System.out.println("Text    : " + preview(article.content, 60));
        System.out.println("Pattern : " + pattern);
        System.out.println();

        if (index == -1) {
            System.out.println("Result  : Pattern not found");
        } else {
            System.out.println("Result  : Pattern found at index " + index);
            System.out.println("Total occurrences : " + count);
        }

        complexity("O(n * m)", "O(1)");
    }

    private static void runKMP() {

        header("KMP STRING SEARCH (M1 / CO1)");

        Article article = chooseArticle();
        if (article == null) return;

        String pattern = readLine("Enter pattern : ");

        int[] lps = KMP.buildLPS(pattern);
        int index = KMP.search(article.content, pattern);
        int[] all = KMP.searchAll(article.content, pattern);

        System.out.println();
        System.out.println("Text    : " + preview(article.content, 60));
        System.out.println("Pattern : " + pattern);

        System.out.print("LPS     : ");
        for (int value : lps) {
            System.out.print(value + " ");
        }
        System.out.println();
        System.out.println();

        if (index == -1) {
            System.out.println("Result  : Pattern not found");
        } else {
            System.out.println("Result  : Pattern found at index " + index);
            System.out.print("All indices : ");
            for (int value : all) {
                System.out.print(value + " ");
            }
            System.out.println();
        }

        complexity("O(n + m)", "O(m)");
    }

    private static void runZFunction() {

        header("Z-FUNCTION SEARCH (M1 / CO1)");

        Article article = chooseArticle();
        if (article == null) return;

        String pattern = readLine("Enter pattern : ");

        int index = ZFunction.search(article.content, pattern);
        int[] all = ZFunction.searchAll(article.content, pattern);

        System.out.println();
        System.out.println("Text    : " + preview(article.content, 60));
        System.out.println("Pattern : " + pattern);
        System.out.println();

        if (index == -1) {
            System.out.println("Result  : Pattern not found");
        } else {
            System.out.println("Result  : Pattern found at index " + index);
            System.out.print("All indices : ");
            for (int value : all) {
                System.out.print(value + " ");
            }
            System.out.println();
        }

        complexity("O(n + m)", "O(n + m)");
    }

    private static void runRabinKarp() {

        header("RABIN-KARP SEARCH (M1 / CO1)");

        Article article = chooseArticle();
        if (article == null) return;

        String pattern = readLine("Enter pattern (e.g. registration/booking code) : ");

        int index = RabinKarp.search(article.content, pattern);
        int[] all = RabinKarp.searchAll(article.content, pattern);

        System.out.println();
        System.out.println("Text    : " + preview(article.content, 60));
        System.out.println("Pattern : " + pattern);
        System.out.println();

        if (index == -1) {
            System.out.println("Result  : Pattern not found");
        } else {
            System.out.println("Result  : Pattern found at index " + index);
            System.out.print("All indices : ");
            for (int value : all) {
                System.out.print(value + " ");
            }
            System.out.println();
        }

        complexity("O(n + m) average, O(n * m) worst", "O(1)");
    }

    private static void runAhoCorasick() {

        header("AHO-CORASICK MULTI-PATTERN SEARCH (M1 / CO1)");

        Article article = chooseArticle();
        if (article == null) return;

        System.out.println("Enter keywords separated by spaces (or press enter for default event keywords):");
        System.out.print("Keywords : ");
        String line = input.nextLine().trim();

        String[] patterns;
        if (line.isEmpty()) {
            patterns = new String[]{"event", "hall", "keynote", "workshop", "speaker"};
            System.out.println("Using default keywords: event, hall, keynote, workshop, speaker");
        } else {
            patterns = line.split("\\s+");
        }

        AhoCorasick ac = new AhoCorasick();
        for (int i = 0; i < patterns.length; i++) {
            ac.addPattern(patterns[i], i);
        }
        ac.buildFailureLinks();

        int totalMatches = ac.search(article.content);
        int[] occurrences = new int[patterns.length];
        for (int i = 0; i < totalMatches; i++) {
            occurrences[ac.getMatchPatternId(i)]++;
        }

        System.out.println();
        System.out.println("Pattern Occurrences (Total Matches: " + totalMatches + "):");
        for (int i = 0; i < patterns.length; i++) {
            System.out.printf("  %-15s : %d occurrences\n", patterns[i], occurrences[i]);
        }

        complexity("O(n + sum(m) + matches)", "O(sum(m) * Sigma)");
    }

    // ==================================================================
    // MODULE 2 (CO2) - SUFFIX STRUCTURES & DOCUMENT SIMILARITY
    // ==================================================================

    private static void runSuffixArray() {

        header("SUFFIX ARRAY - PREFIX DOUBLING (M2 / CO2)");

        Article article = chooseArticle();
        if (article == null) return;

        int[] sa = SuffixArray.buildSuffixArray(article.content);

        System.out.println();
        System.out.println("First 10 Suffixes in Lexicographical Order:");
        int show = Math.min(10, sa.length);
        for (int i = 0; i < show; i++) {
            System.out.printf("  sa[%2d] = %4d -> \"%s\"\n", i, sa[i], preview(article.content.substring(sa[i]), 35));
        }

        complexity("O(n log^2 n)", "O(n)");
    }

    private static void runSAIS() {

        header("SA-IS - LINEAR TIME O(n) SUFFIX ARRAY (M2 / CO2)");

        Article article = chooseArticle();
        if (article == null) return;

        System.out.println("Building Suffix Array using SA-IS (Nong-Zhang-Chan Algorithm)...");
        int[] sa = SAIS.buildSuffixArray(article.content);

        System.out.println("\nSA-IS Generated Suffix Array (First 10 entries):");
        int show = Math.min(10, sa.length);
        for (int i = 0; i < show; i++) {
            System.out.printf("  sa[%2d] = %4d -> \"%s\"\n", i, sa[i], preview(article.content.substring(sa[i]), 35));
        }

        complexity("O(n) strictly linear", "O(n)");
    }

    private static void runKasai() {

        header("LCP ARRAY / KASAI'S ALGORITHM (M2 / CO2)");

        Article article = chooseArticle();
        if (article == null) return;

        int[] sa = SuffixArray.buildSuffixArray(article.content);
        int[] lcp = Kasai.buildLCP(article.content, sa);

        System.out.println();
        System.out.println("First 10 Suffix Array & LCP values:");
        int show = Math.min(10, sa.length);
        for (int i = 0; i < show; i++) {
            System.out.printf("  sa[%2d] = %4d | LCP = %2d -> \"%s\"\n",
                    i, sa[i], lcp[i], preview(article.content.substring(sa[i]), 35));
        }

        complexity("O(n)", "O(n)");
    }

    private static void runSuffixAutomaton() {

        header("SUFFIX AUTOMATON (SAM) (M2 / CO2)");

        Article article = chooseArticle();
        if (article == null) return;

        System.out.println("Building Suffix Automaton for document text...");
        SuffixAutomaton sam = new SuffixAutomaton(article.content);
        System.out.println("Automaton built with " + sam.getStateCount() + " states in linear time.");

        System.out.println();
        String query = readLine("Enter substring to search : ");
        boolean exists = sam.contains(query);
        int occurrences = sam.countOccurrences(query);

        System.out.println("\nSearch Results:");
        System.out.println("  Exists in document : " + (exists ? "YES" : "NO"));
        System.out.println("  Occurrence count   : " + occurrences);

        System.out.println();
        String otherText = readLine("Enter another text to find Longest Common Substring (LCS) : ");
        if (!otherText.isEmpty()) {
            String lcs = sam.longestCommonSubstring(otherText);
            System.out.println("  Longest Common Substring: \"" + lcs + "\" (length " + lcs.length() + ")");
        }

        complexity("O(n) construction, O(m) search, O(|T2|) LCS", "O(n * Sigma)");
    }

    // ==================================================================
    // MODULE 3 (CO3) - ADVANCED DYNAMIC PROGRAMMING
    // ==================================================================

    private static void runLevenshtein() {

        header("LEVENSHTEIN EDIT DISTANCE (M3 / CO3)");

        String s1 = readLine("Enter string 1 (e.g. participant name) : ");
        String s2 = readLine("Enter string 2 (e.g. corrected name)   : ");

        int dist = Levenshtein.distance(s1, s2);
        System.out.println("\nLevenshtein Distance: " + dist);

        complexity("O(n * m)", "O(n * m)");
    }

    private static void runDamerauLevenshtein() {

        header("DAMERAU-LEVENSHTEIN DISTANCE (M3 / CO3)");

        String s1 = readLine("Enter string 1 (with potential adjacent swaps) : ");
        String s2 = readLine("Enter string 2                                : ");

        int dist = DamerauLevenshtein.distance(s1, s2);
        System.out.println("\nDamerau-Levenshtein Distance: " + dist);

        complexity("O(n * m)", "O(n * m)");
    }

    private static void runWeightedEditDistance() {

        header("WEIGHTED EDIT DISTANCE (M3 / CO3)");

        String s1 = readLine("Enter string 1 : ");
        String s2 = readLine("Enter string 2 : ");

        int dist = WeightedEditDistance.distance(s1, s2);
        System.out.println("\nWeighted Edit Distance: " + dist);
        System.out.println("(Insert Cost: " + WeightedEditDistance.INSERT_COST
                + ", Delete Cost: " + WeightedEditDistance.DELETE_COST
                + ", Substitution Cost: " + WeightedEditDistance.SUBSTITUTION_COST + ")");

        complexity("O(n * m)", "O(n * m)");
    }

    private static void runNeedlemanWunsch() {

        header("NEEDLEMAN-WUNSCH GLOBAL SEQUENCE ALIGNMENT (M3 / CO3)");

        String s1 = readLine("Enter sequence 1 (e.g. Schedule A codes) : ");
        String s2 = readLine("Enter sequence 2 (e.g. Schedule B codes) : ");

        int score = NeedlemanWunsch.align(s1, s2, 1, -1, -2);
        System.out.println("\nGlobal Alignment Score: " + score);

        complexity("O(n * m)", "O(n * m)");
    }

    private static void runSmithWaterman() {

        header("SMITH-WATERMAN LOCAL SEQUENCE ALIGNMENT (M3 / CO3)");

        String s1 = readLine("Enter sequence 1 : ");
        String s2 = readLine("Enter sequence 2 : ");

        int score = SmithWaterman.align(s1, s2, 2, -1, -1);
        System.out.println("\nLocal Alignment Maximum Score: " + score);

        complexity("O(n * m)", "O(n * m)");
    }

    private static void runMatrixChain() {

        header("MATRIX-CHAIN MULTIPLICATION (M3 / CO3)");

        System.out.println("Enter dimension array (e.g. '10 30 5 60' for matrices 10x30, 30x5, 5x60):");
        int[] dims = readIntArray("Dimensions : ");

        if (dims.length < 2) {
            dims = new int[]{10, 30, 5, 60};
            System.out.println("Using default dimensions: 10 30 5 60");
        }

        int cost = MatrixChainMultiplication.minimumCost(dims);
        System.out.println("\nMinimum Scalar Multiplications Required: " + cost);

        complexity("O(n^3)", "O(n^2)");
    }

    private static void runOptimalBST() {

        header("OPTIMAL BINARY SEARCH TREE (M3 / CO3)");

        System.out.println("Enter access frequencies of sorted event keys (e.g. '34 8 50'):");
        int[] freq = readIntArray("Frequencies : ");

        if (freq.length == 0) {
            freq = new int[]{34, 8, 50};
            System.out.println("Using default frequencies: 34 8 50");
        }

        int[] keys = new int[freq.length];
        for (int i = 0; i < freq.length; i++) {
            keys[i] = i + 1;
        }

        int cost = OptimalBST.minimumCost(keys, freq);
        System.out.println("\nMinimum Expected Search Cost: " + cost);

        complexity("O(n^3)", "O(n^2)");
    }

    private static void runBitmaskTSP() {

        header("BITMASK DP - TRAVELLING SALESMAN (M3 / CO3)");

        System.out.println("Enter number of venues (2 to 10) or 0 for preloaded 4-venue matrix:");
        System.out.print("Number of venues : ");
        int n = readInt();

        int[][] distance;
        if (n <= 1) {
            distance = new int[][]{
                {0, 10, 15, 20},
                {10, 0, 35, 25},
                {15, 35, 0, 30},
                {20, 25, 30, 0}
            };
            System.out.println("Using preloaded 4-venue distance matrix.");
        } else {
            distance = new int[n][n];
            for (int i = 0; i < n; i++) {
                distance[i] = readIntArray("Row " + i + " : ");
            }
        }

        int cost = BitmaskTSP.solve(distance);
        System.out.println("\nMinimum Tour Cost (start & end at venue 0): " + cost);

        complexity("O(2^n * n^2)", "O(2^n * n)");
    }

    private static void runHamiltonianPath() {

        header("HAMILTONIAN PATH (M3 / CO3)");

        System.out.println("Enter number of activities (1 to 10) or 0 for preloaded 4-activity graph:");
        System.out.print("Number of activities : ");
        int n = readInt();

        int[][] graph;
        if (n <= 0) {
            graph = new int[][]{
                {0, 1, 1, 0},
                {1, 0, 1, 1},
                {1, 1, 0, 1},
                {0, 1, 1, 0}
            };
            System.out.println("Using preloaded 4-node connected activity graph.");
        } else {
            graph = new int[n][n];
            for (int i = 0; i < n; i++) {
                graph[i] = readIntArray("Row " + i + " : ");
            }
        }

        boolean exists = HamiltonianPath.exists(graph);
        System.out.println("\nHamiltonian Path Exists: " + (exists ? "YES" : "NO"));

        complexity("O(2^n * n^2)", "O(2^n * n)");
    }

    private static void runSOSDP() {

        header("SUM OVER SUBSETS (SOS DP) (M3 / CO3)");

        System.out.print("Number of bits n (1 to 10) or 0 for default 3 bits : ");
        int bits = readInt();

        if (bits <= 0) bits = 3;

        int size = 1 << bits;
        System.out.println("Enter " + size + " values (or press enter for default values):");
        int[] values = readIntArray("Values : ");

        if (values.length != size) {
            values = new int[size];
            for (int i = 0; i < size; i++) values[i] = i + 1;
            System.out.println("Using default values: 1 to " + size);
        }

        int[] dp = SOSDP.calculate(values);
        System.out.println("\nmask | dp[mask] = sum over all submasks");
        for (int mask = 0; mask < Math.min(8, dp.length); mask++) {
            System.out.printf("  %2d | %d\n", mask, dp[mask]);
        }
        if (dp.length > 8) {
            System.out.println("  ... (" + dp.length + " masks computed)");
        }

        complexity("O(n * 2^n)", "O(2^n)");
    }

    // ==================================================================
    // MODULE 4 (CO4) - NETWORK FLOW
    // ==================================================================

    private static void runFordFulkerson() {

        header("FORD-FULKERSON PARTICIPANT FLOW (M4 / CO4)");

        System.out.println("Facility Flow Network (6 checkpoints):");
        System.out.println("  0: Main Gate -> 1: Security Screening A -> 3: Registration Desk 1 -> 5: Auditorium");
        System.out.println("  0: Main Gate -> 2: Security Screening B -> 4: Registration Desk 2 -> 5: Auditorium");

        FordFulkerson network = new FordFulkerson(6);
        network.addEdge(0, 1, 16);
        network.addEdge(0, 2, 13);
        network.addEdge(1, 2, 10);
        network.addEdge(1, 3, 12);
        network.addEdge(2, 1, 4);
        network.addEdge(2, 4, 14);
        network.addEdge(3, 2, 9);
        network.addEdge(3, 5, 20);
        network.addEdge(4, 3, 7);
        network.addEdge(4, 5, 4);

        int maxRate = network.maxFlow(0, 5);
        System.out.println("\nMaximum Participant Flow Throughput: " + maxRate + " attendees/minute");

        boolean[] inS = network.minCut(0);
        System.out.println("\nMin-Cut Bottleneck Passages (Constricting Crowd Capacity):");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (inS[i] && !inS[j] && network.getCapacityMatrix()[i][j] > 0) {
                    System.out.printf("  Bottleneck: Checkpoint %d -> Checkpoint %d (Capacity: %d)\n",
                            i, j, network.getCapacityMatrix()[i][j]);
                }
            }
        }

        complexity("O(E * max_flow)", "O(V^2)");
    }

    private static void runEdmondsKarp() {

        header("EDMONDS-KARP VENUE-CAPACITY ANALYSIS (M4 / CO4)");

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
        System.out.println("Maximum Venue Admittance Rate: " + maxCapacity + " visitors/min");
        System.out.println();
        venue.printSaturatedPassages();

        complexity("O(V * E^2) strongly polynomial", "O(V^2)");
    }

    private static void runDinic() {

        header("DINIC'S ALGORITHM - LARGE FLOW NETWORKS (M4 / CO4)");

        Dinic net = new Dinic(8);
        net.addEdge(0, 1, 100);
        net.addEdge(0, 2, 80);
        net.addEdge(0, 3, 60);
        net.addEdge(1, 4, 70);
        net.addEdge(2, 4, 50);
        net.addEdge(3, 4, 40);
        net.addEdge(1, 5, 40);
        net.addEdge(2, 5, 40);
        net.addEdge(3, 6, 30);
        net.addEdge(4, 7, 120);
        net.addEdge(5, 7, 60);
        net.addEdge(6, 7, 40);

        int totalCrowd = net.maxFlow(0, 7);
        System.out.println("Large Event Multi-Zone Network Max Evacuation / Flow Capacity:");
        System.out.println("  " + totalCrowd + " persons/minute from Transit Hub to Main Arena");

        complexity("O(V^2 * E) general, O(E * sqrt(V)) unit networks", "O(V + E)");
    }

    private static void runBipartiteMatching() {

        header("BIPARTITE MATCHING - STAFF & VOLUNTEER ALLOCATION (M4 / CO4)");

        String[] staff = {"Alice", "Bob", "Charlie", "Diana", "Ethan"};
        String[] activities = {"Keynote Audio", "AI Workshop", "Registration Desk", "Hackathon Lab", "VIP Lounge"};

        BipartiteMatching bm = new BipartiteMatching(5, 5);
        bm.addEligibility(0, 0);
        bm.addEligibility(0, 4);
        bm.addEligibility(1, 1);
        bm.addEligibility(1, 3);
        bm.addEligibility(2, 0);
        bm.addEligibility(2, 1);
        bm.addEligibility(3, 2);
        bm.addEligibility(4, 1);
        bm.addEligibility(4, 4);

        int matches = bm.maxMatching();
        System.out.println("Maximum Assigned Volunteer Pairs: " + matches + " / 5 activities\n");

        int[] assign = bm.getAssignments();
        for (int v = 0; v < activities.length; v++) {
            if (assign[v] != -1) {
                System.out.printf("  Activity '%-18s' <= Assigned to: %s\n", activities[v], staff[assign[v]]);
            } else {
                System.out.printf("  Activity '%-18s' <= [UNFILLED]\n", activities[v]);
            }
        }

        complexity("O(V * E)", "O(V + E)");
    }

    private static void runKonigTheorem() {

        header("KÖNIG'S THEOREM - ASSIGNMENT CONFLICT ANALYSIS (M4 / CO4)");

        String[] staff = {"Staff 0", "Staff 1", "Staff 2", "Staff 3"};
        String[] activities = {"Task 0", "Task 1", "Task 2", "Task 3"};

        BipartiteMatching bm = new BipartiteMatching(4, 4);
        bm.addEligibility(0, 0);
        bm.addEligibility(0, 1);
        bm.addEligibility(1, 1);
        bm.addEligibility(2, 1);
        bm.addEligibility(2, 2);
        bm.addEligibility(3, 2);

        KonigTheorem.KonigResult res = KonigTheorem.analyzeConflicts(bm);

        System.out.println("König's Theorem Invariance:");
        System.out.println("  |Max Matching| = " + res.matchingSize);
        int mvcSize = res.minVertexCoverStaff.size() + res.minVertexCoverActivities.size();
        System.out.println("  |Min Vertex Cover| = " + mvcSize + " (Verified: " + (res.matchingSize == mvcSize) + ")");

        System.out.println("\nMinimum Vertex Cover Nodes (Root Bottlenecks Constraining Matching):");
        for (int s : res.minVertexCoverStaff) System.out.println("  * " + staff[s] + " [Staff]");
        for (int a : res.minVertexCoverActivities) System.out.println("  * " + activities[a] + " [Activity]");

        System.out.println("\nMaximum Independent Set (Mutually Conflict-Free Subset):");
        for (int s : res.maxIndependentSetStaff) System.out.println("  * " + staff[s] + " [Staff]");
        for (int a : res.maxIndependentSetActivities) System.out.println("  * " + activities[a] + " [Activity]");

        complexity("O(V * E)", "O(V + E)");
    }

    // ==================================================================
    // MODULE 5 (CO5) - NP-COMPLETENESS AND APPROXIMATION
    // ==================================================================

    private static void runSAT3SAT() {

        header("3-SAT DPLL EVENT SCHEDULING SOLVER (M5 / CO5)");

        SAT3SAT solver = new SAT3SAT(4);
        solver.addClause(1, 2, 3);
        solver.addClause(-1, -2, 3);
        solver.addClause(-3, 4, 1);
        solver.addClause(-1, 4, 2);

        System.out.println("Event Scheduling Constraints (3-CNF Formula):");
        for (SAT3SAT.Clause c : solver.getClauses()) {
            System.out.println("  " + c);
        }

        int[] sol = solver.solve();
        if (sol != null) {
            System.out.println("\nResult: SATISFIABLE (Valid Schedule Exists):");
            for (int i = 1; i <= 4; i++) {
                System.out.printf("  x%d = %s\n", i, (sol[i] == 1 ? "TRUE (Scheduled)" : "FALSE (Not Scheduled)"));
            }
        } else {
            System.out.println("\nResult: UNSATISFIABLE (Conflicting constraints).");
        }

        complexity("O(2^n) worst-case", "O(n + m)");
    }

    private static void runSATToClique() {

        header("POLYNOMIAL REDUCTION: 3-SAT -> CLIQUE (M5 / CO5)");

        SAT3SAT sat = new SAT3SAT(3);
        sat.addClause(1, 2, 3);
        sat.addClause(-1, -2, 3);
        sat.addClause(1, -3, 2);

        System.out.println("Input 3-SAT Formula (k = 3 clauses):");
        for (SAT3SAT.Clause c : sat.getClauses()) {
            System.out.println("  " + c);
        }

        SATToClique.ReductionGraph rg = SATToClique.reduce(sat);
        System.out.printf("\nConstructed Graph G: |V| = %d vertices (3 per clause), Target k = %d\n",
                rg.nodes.size(), rg.k);

        List<Integer> clique = SATToClique.findCliqueOfSizeK(rg);
        if (clique != null) {
            System.out.println("\nFound k-Clique in Reduction Graph (Size = " + clique.size() + "):");
            for (int id : clique) {
                System.out.println("  " + rg.nodes.get(id));
            }
            System.out.println("\nConclusion: 3-SAT instance is SATISFIABLE via CLIQUE reduction!");
        }

        complexity("Reduction Time: O(k^2)", "Space: O(k^2)");
    }

    private static void runCliqueToIndependentSetToVertexCover() {

        header("CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER (M5 / CO5)");

        String[] sessions = {
            "Keynote (Hall A)",
            "AI Demo (Hall A)",
            "Robotics Lab (Hall B)",
            "Startup Pitch (Hall B)",
            "Networking Lunch",
            "Hackathon Coding"
        };

        CliqueToIndependentSetToVertexCover.Graph g = new CliqueToIndependentSetToVertexCover.Graph(6, sessions);
        g.addEdge(0, 1);
        g.addEdge(2, 3);
        g.addEdge(0, 4);
        g.addEdge(1, 4);
        g.addEdge(2, 4);
        g.addEdge(3, 4);

        System.out.println("Activity Conflict Graph (6 sessions, shared room/time edges):");
        List<Integer> maxIndSet = CliqueToIndependentSetToVertexCover.findMaxIndependentSet(g);
        System.out.println("\n1. Maximum Independent Set (Max simultaneous conflict-free activities, size " + maxIndSet.size() + "):");
        for (int v : maxIndSet) {
            System.out.println("   * " + sessions[v]);
        }

        List<Integer> minVC = CliqueToIndependentSetToVertexCover.independentSetToVertexCover(g, maxIndSet);
        System.out.println("\n2. Minimum Vertex Cover via V \\ IndependentSet (size " + minVC.size() + "):");
        for (int v : minVC) {
            System.out.println("   * " + sessions[v]);
        }
        System.out.println("   Verified Valid Vertex Cover: " + CliqueToIndependentSetToVertexCover.isVertexCover(g, minVC));

        complexity("Reduction Time: O(V^2)", "Space: O(V^2)");
    }

    private static void runVertexCoverApproximation() {

        header("VERTEX-COVER 2-APPROXIMATION (M5 / CO5)");

        String[] resources = {
            "Stage A Projector",
            "Stage B Audio Deck",
            "Keynote Mic 1",
            "Keynote Mic 2",
            "VR Headset Station",
            "Livestream Encoder",
            "Main Lighting Rig"
        };

        List<VertexCoverApproximation.Edge> conflictEdges = new ArrayList<>();
        conflictEdges.add(new VertexCoverApproximation.Edge(0, 1));
        conflictEdges.add(new VertexCoverApproximation.Edge(1, 2));
        conflictEdges.add(new VertexCoverApproximation.Edge(2, 3));
        conflictEdges.add(new VertexCoverApproximation.Edge(3, 4));
        conflictEdges.add(new VertexCoverApproximation.Edge(4, 5));
        conflictEdges.add(new VertexCoverApproximation.Edge(5, 6));
        conflictEdges.add(new VertexCoverApproximation.Edge(1, 5));

        VertexCoverApproximation.ApproxResult res = VertexCoverApproximation.solve2Approx(resources.length, conflictEdges);

        System.out.println("Maximal Matching Disjoint Edges Chosen (|M|): " + res.selectedMatchingEdges.size());
        for (VertexCoverApproximation.Edge e : res.selectedMatchingEdges) {
            System.out.printf("  Selected Edge: %s <---> %s\n", resources[e.u], resources[e.v]);
        }

        System.out.println("\n2-Approximation Vertex Cover Size (|C| = 2*|M|): " + res.cover.size());
        for (int v : res.cover) {
            System.out.println("  * " + resources[v]);
        }

        System.out.println("\nApproximation Theoretical Guarantee: |C| <= 2 * OPT");

        complexity("O(V + E) linear time approximation", "O(V + E)");
    }

    // ==================================================================
    // MODULE 6 (CO6) - RANDOMIZED AND PARALLEL ALGORITHMS
    // ==================================================================

    private static void runRandomizedQuickSort() {

        header("RANDOMIZED QUICKSORT PARTICIPANT RANKING (M6 / CO6)");

        RandomizedQuickSort.Participant[] attendees = {
            new RandomizedQuickSort.Participant("EVT-101", "Emma Watson", 78),
            new RandomizedQuickSort.Participant("EVT-102", "Liam Smith", 95),
            new RandomizedQuickSort.Participant("EVT-103", "Sophia Chen", 62),
            new RandomizedQuickSort.Participant("EVT-104", "Noah Brown", 88),
            new RandomizedQuickSort.Participant("EVT-105", "Olivia Davis", 91),
            new RandomizedQuickSort.Participant("EVT-106", "Lucas Miller", 73),
            new RandomizedQuickSort.Participant("EVT-107", "Ava Wilson", 84),
            new RandomizedQuickSort.Participant("EVT-108", "Ethan Moore", 99)
        };

        System.out.println("Ranking Attendee Records by Priority Score using Randomized QuickSort...");
        RandomizedQuickSort.sort(attendees);

        System.out.println("\nRanked Attendees (Highest Score First):");
        for (int i = 0; i < attendees.length; i++) {
            System.out.printf("  Rank %d: %s\n", (i + 1), attendees[i]);
        }
        System.out.println("\nTotal Comparisons: " + RandomizedQuickSort.getComparisonCount());

        complexity("O(n log n) expected", "O(log n)");
    }

    private static void runReservoirSampling() {

        header("RESERVOIR SAMPLING STREAMING REGISTRATIONS (M6 / CO6)");

        System.out.print("Enter sample size k (default 4) : ");
        String line = input.nextLine().trim();
        int k = 4;
        if (!line.isEmpty()) {
            try { k = Integer.parseInt(line); } catch (Exception ignored) {}
        }

        ReservoirSampling<String> sampler = new ReservoirSampling<>(k);
        String[] liveRegistrations = {
            "REG-001: Sarah Connor",
            "REG-002: Bruce Wayne",
            "REG-003: Clark Kent",
            "REG-004: Diana Prince",
            "REG-005: Peter Parker",
            "REG-006: Tony Stark",
            "REG-007: Natasha Romanoff",
            "REG-008: Barry Allen",
            "REG-009: Arthur Curry",
            "REG-010: Wanda Maximoff"
        };

        System.out.printf("Ingesting live registration stream of %d attendees into reservoir of size k = %d...\n\n",
                liveRegistrations.length, k);

        for (String reg : liveRegistrations) {
            sampler.ingest(reg);
        }

        System.out.println("Final Uniform Sample of Attendees (Probability k/n for each):");
        for (String attendee : sampler.getSample()) {
            System.out.println("  * " + attendee);
        }

        complexity("O(n) single sequential pass", "O(k) memory");
    }

    private static void runMillerRabin() {

        header("MILLER-RABIN NUMERICAL PRIMALITY TEST (M6 / CO6)");

        System.out.println("Enter ticket token number to test (or press enter for default large token 1000000007):");
        System.out.print("Token : ");
        String line = input.nextLine().trim();
        long token = 1000000007L;
        if (!line.isEmpty()) {
            try { token = Long.parseLong(line); } catch (Exception ignored) {}
        }

        boolean isPrime = MillerRabin.isProbablePrime(token, 20);
        System.out.printf("\nTicket Token %d -> %s\n", token, (isPrime ? "PRIME (Cryptographically Secure)" : "COMPOSITE"));

        complexity("O(k * log^3 n) with error <= 4^(-k)", "O(log n)");
    }

    private static void runBlellochScan() {

        header("BLELLOCH PARALLEL PREFIX SCAN (M6 / CO6)");

        int[] arrivals = {45, 60, 35, 80, 110, 95, 70, 120};

        int[] exclusive = BlellochScan.exclusiveScan(arrivals);
        int[] inclusive = BlellochScan.inclusiveScan(arrivals);

        System.out.println("Blelloch Work-Efficient Parallel Prefix Scan across 8 Parallel Gates:");
        System.out.println("Gate | Arrivals | Cumulative Prior (Exclusive) | Running Total (Inclusive)");
        System.out.println("-----+----------+------------------------------+--------------------------");
        for (int i = 0; i < arrivals.length; i++) {
            System.out.printf(" %2d  |   %3d    |             %4d             |           %4d\n",
                    (i + 1), arrivals[i], exclusive[i], inclusive[i]);
        }

        complexity("Work: O(n), Span / Parallel Depth: O(log n)", "O(n)");
    }

    private static void runParallelReduce() {

        header("PARALLEL REDUCE EVENT STATISTICS (M6 / CO6)");

        int n = 10000;
        long[] attendance = new long[n];
        for (int i = 0; i < n; i++) {
            attendance[i] = (i * 73L + 127L) % 500 + 50;
        }

        System.out.printf("Aggregating %d event session attendance records using ForkJoin parallel reduction...\n", n);
        long sum = ParallelReduce.reduce(attendance, ParallelReduce.Operator.SUM);
        long max = ParallelReduce.reduce(attendance, ParallelReduce.Operator.MAX);
        long min = ParallelReduce.reduce(attendance, ParallelReduce.Operator.MIN);
        double avg = ParallelReduce.average(attendance);

        System.out.println("\nParallel Reduce Analytics Results:");
        System.out.printf("  Total Event Attendance (SUM) : %,d\n", sum);
        System.out.printf("  Peak Hall Attendance   (MAX) : %d\n", max);
        System.out.printf("  Lowest Turnout         (MIN) : %d\n", min);
        System.out.printf("  Average Attendance Per Hall  : %.2f\n", avg);

        complexity("Work: O(n), Parallel Span: O(log n)", "O(log n) stack");
    }

    private static void runBrentsTheorem() {

        header("BRENT'S THEOREM PARALLEL SPEEDUP ANALYSIS (M6 / CO6)");

        int[] levels = {128, 256, 512, 512, 256, 128, 32, 1};
        int work = 0;
        for (int w : levels) work += w;
        int span = levels.length;

        System.out.printf("Pipeline Work W = %d ops, Span S = %d stages\n\n", work, span);
        int[] cores = {1, 2, 4, 8, 16, 32};
        for (int p : cores) {
            BrentsTheorem.ScheduleAnalysis a = BrentsTheorem.analyze(levels, p);
            System.out.println("  " + a);
        }

        complexity("O(S) analysis", "O(S)");
    }
}
