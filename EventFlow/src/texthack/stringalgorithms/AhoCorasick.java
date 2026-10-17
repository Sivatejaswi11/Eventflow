package texthack.stringalgorithms;

/**
 * MODULE 2 / CO2 - Aho-Corasick multiple pattern matching (intuition level).
 *
 * Structure:
 *   1. All patterns are inserted into a trie.
 *   2. Each node gets a FAILURE LINK pointing to the longest proper suffix
 *      of the current prefix that is also a prefix of some pattern.
 *   3. While scanning the text we never restart: on a mismatch we follow
 *      failure links, exactly like KMP but for many patterns at once.
 *
 * EventFlow use: scan one event document for a whole list of keywords
 * (registration, coding, presentation, robotics ...) in a single pass.
 *
 * Time  : O(total pattern length) to build + O(text length + matches) to scan
 * Space : O(total pattern length * alphabet)
 *
 * No java.util collections are used - the trie, the queue and the match list
 * are plain arrays.
 */
public class AhoCorasick {

    private static final int ALPHABET = 128;   // ASCII

    private int[][] children;     // pure trie: children[node][character], -1 = none
    private int[][] goTo;         // automaton transition (never -1 after build)
    private int[] failure;        // failure link of each node
    private int[] patternId;      // id of the pattern ending at this node, else -1
    private int[] patternLength;  // length of that pattern
    private int[] outputLink;     // nearest ancestor-by-failure that is a word end

    private int nodeCount;
    private boolean linksBuilt;

    // results of the last search
    private int[] matchPatternId;
    private int[] matchEndIndex;
    private int matchCount;

    public AhoCorasick() {
        this(2000);
    }

    public AhoCorasick(int maxNodes) {
        children = new int[maxNodes][ALPHABET];
        goTo = new int[maxNodes][ALPHABET];
        failure = new int[maxNodes];
        patternId = new int[maxNodes];
        patternLength = new int[maxNodes];
        outputLink = new int[maxNodes];

        for (int node = 0; node < maxNodes; node++) {
            for (int c = 0; c < ALPHABET; c++) {
                children[node][c] = -1;
                goTo[node][c] = 0;
            }
            patternId[node] = -1;
            outputLink[node] = -1;
        }
        nodeCount = 1;      // node 0 is the root
        linksBuilt = false;
    }

    /** Inserts one pattern into the trie. */
    public void addPattern(String pattern, int id) {

        int current = 0;

        for (int i = 0; i < pattern.length(); i++) {

            int c = pattern.charAt(i);
            if (c < 0 || c >= ALPHABET) {
                c = 0;      // characters outside ASCII are folded to 0
            }

            if (children[current][c] == -1) {
                children[current][c] = nodeCount;
                nodeCount++;
            }
            current = children[current][c];
        }

        patternId[current] = id;
        patternLength[current] = pattern.length();
        linksBuilt = false;
    }

    /** Builds failure links and output links with a BFS over the trie. */
    public void buildFailureLinks() {

        int[] queue = new int[nodeCount];
        int head = 0;
        int tail = 0;

        // depth 1: failure link points to the root
        for (int c = 0; c < ALPHABET; c++) {
            int child = children[0][c];
            if (child == -1) {
                goTo[0][c] = 0;              // missing edge at the root: stay
            } else {
                goTo[0][c] = child;
                failure[child] = 0;
                queue[tail++] = child;
            }
        }

        while (head < tail) {

            int current = queue[head++];

            // output link = nearest word-ending node reachable by failure links
            int fallback = failure[current];
            if (patternId[fallback] != -1) {
                outputLink[current] = fallback;
            } else {
                outputLink[current] = outputLink[fallback];
            }

            for (int c = 0; c < ALPHABET; c++) {

                int child = children[current][c];

                if (child == -1) {
                    goTo[current][c] = goTo[failure[current]][c];
                } else {
                    goTo[current][c] = child;
                    failure[child] = goTo[failure[current]][c];
                    queue[tail++] = child;
                }
            }
        }
        linksBuilt = true;
    }

    /**
     * Scans the text once. Returns the number of matches found.
     * Use getMatchPatternId(i) / getMatchEndIndex(i) to read them.
     */
    public int search(String text) {

        if (!linksBuilt) {
            buildFailureLinks();
        }

        matchPatternId = new int[text.length() + 1];
        matchEndIndex = new int[text.length() + 1];
        matchCount = 0;

        int current = 0;

        for (int i = 0; i < text.length(); i++) {

            int c = text.charAt(i);
            if (c < 0 || c >= ALPHABET) {
                c = 0;
            }

            current = goTo[current][c];

            // report the pattern ending exactly here
            if (patternId[current] != -1) {
                store(patternId[current], i);
            }

            // report shorter patterns that also end here
            int extra = outputLink[current];
            while (extra != -1) {
                store(patternId[extra], i);
                extra = outputLink[extra];
            }
        }
        return matchCount;
    }

    private void store(int id, int endIndex) {

        if (matchCount == matchPatternId.length) {
            int[] biggerId = new int[matchCount * 2 + 1];
            int[] biggerEnd = new int[matchCount * 2 + 1];
            for (int i = 0; i < matchCount; i++) {
                biggerId[i] = matchPatternId[i];
                biggerEnd[i] = matchEndIndex[i];
            }
            matchPatternId = biggerId;
            matchEndIndex = biggerEnd;
        }
        matchPatternId[matchCount] = id;
        matchEndIndex[matchCount] = endIndex;
        matchCount++;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public int getMatchPatternId(int index) {
        return matchPatternId[index];
    }

    /** Index of the LAST character of the match inside the text. */
    public int getMatchEndIndex(int index) {
        return matchEndIndex[index];
    }

    public static void main(String[] args) {

        String[] keywords = { "coding", "registration", "presentation" };

        AhoCorasick automaton = new AhoCorasick();
        for (int i = 0; i < keywords.length; i++) {
            automaton.addPattern(keywords[i], i);
        }
        automaton.buildFailureLinks();

        String text = "registration, coding and final presentation";
        int total = automaton.search(text);

        System.out.println("Matches: " + total);
        for (int i = 0; i < total; i++) {
            int id = automaton.getMatchPatternId(i);
            int end = automaton.getMatchEndIndex(i);
            System.out.println(keywords[id]
                    + " ends at " + end
                    + " (starts at " + (end - keywords[id].length() + 1) + ")");
        }
    }
}
