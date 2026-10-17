package texthack.suffix;

/**
 * MODULE 2 / CO2 - Suffix Automaton (SAM).
 *
 * A Suffix Automaton is the minimal Deterministic Finite Automaton (DFA)
 * that accepts all suffixes of a string.
 *
 * Properties:
 *   - For a text of length n, it contains at most 2n - 1 states and at most 3n - 4 transitions.
 *   - Built online in strictly linear O(n) time and O(n * Sigma) space.
 *   - Each state corresponds to an equivalence class of substrings having the same 'endpos' set.
 *
 * Capabilities:
 *   - Substring Existence: Checks if pattern P exists in text T in O(|P|) time.
 *   - Substring Occurrences: Counts total occurrences in O(|P|) time.
 *   - Longest Common Substring: Finds the longest shared text across event descriptions in O(|T1| + |T2|).
 *
 * EventFlow use: Instantaneous O(m) substring matching across large-scale event brochures,
 * counting attendee mentions, and identifying shared schedule blocks across multiple venues.
 *
 * Time  : O(n) construction, O(m) search
 * Space : O(n * Sigma)
 */
public class SuffixAutomaton {

    public static class State {
        public int len;                  // length of the longest string in this equivalence class
        public int link;                 // suffix link to smaller equivalence class
        public int[] next = new int[128];// transitions for ASCII characters
        public int endposSize = 0;       // number of occurrences
        public boolean isClone = false;

        public State(int len, int link) {
            this.len = len;
            this.link = link;
            for (int i = 0; i < 128; i++) {
                next[i] = -1;
            }
        }
    }

    private State[] states;
    private int size;
    private int last;

    public SuffixAutomaton(String text) {
        int maxStates = 2 * text.length() + 2;
        states = new State[maxStates];
        states[0] = new State(0, -1);
        size = 1;
        last = 0;

        for (int i = 0; i < text.length(); i++) {
            extend(text.charAt(i));
        }

        computeOccurrences(text.length());
    }

    /** Appends character c to the automaton online. */
    public void extend(char c) {
        int ch = (int) c;
        if (ch >= 128) ch = ch % 128; // clamp to 7-bit ASCII range

        int cur = size++;
        states[cur] = new State(states[last].len + 1, -1);
        states[cur].endposSize = 1;

        int p = last;
        while (p != -1 && states[p].next[ch] == -1) {
            states[p].next[ch] = cur;
            p = states[p].link;
        }

        if (p == -1) {
            states[cur].link = 0;
        } else {
            int q = states[p].next[ch];
            if (states[p].len + 1 == states[q].len) {
                states[cur].link = q;
            } else {
                int clone = size++;
                states[clone] = new State(states[p].len + 1, states[q].link);
                for (int i = 0; i < 128; i++) {
                    states[clone].next[i] = states[q].next[i];
                }
                states[clone].isClone = true;

                while (p != -1 && states[p].next[ch] == q) {
                    states[p].next[ch] = clone;
                    p = states[p].link;
                }
                states[q].link = clone;
                states[cur].link = clone;
            }
        }
        last = cur;
    }

    /** Precomputes occurrence counts for each state by walking in reverse topological order. */
    private void computeOccurrences(int textLength) {
        int[] count = new int[textLength + 1];
        for (int i = 0; i < size; i++) {
            count[states[i].len]++;
        }
        for (int i = 1; i <= textLength; i++) {
            count[i] += count[i - 1];
        }

        int[] order = new int[size];
        for (int i = 0; i < size; i++) {
            order[--count[states[i].len]] = i;
        }

        for (int i = size - 1; i > 0; i--) {
            int u = order[i];
            int parent = states[u].link;
            if (parent != -1) {
                states[parent].endposSize += states[u].endposSize;
            }
        }
    }

    /** Returns true if pattern appears as a substring in the indexed text. Time: O(|pattern|). */
    public boolean contains(String pattern) {
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            int ch = pattern.charAt(i);
            if (ch >= 128) ch = ch % 128;
            state = states[state].next[ch];
            if (state == -1) return false;
        }
        return true;
    }

    /** Counts how many times pattern appears in the indexed text. Time: O(|pattern|). */
    public int countOccurrences(String pattern) {
        int state = 0;
        for (int i = 0; i < pattern.length(); i++) {
            int ch = pattern.charAt(i);
            if (ch >= 128) ch = ch % 128;
            state = states[state].next[ch];
            if (state == -1) return 0;
        }
        return states[state].endposSize;
    }

    /** Finds the Longest Common Substring between the indexed text and otherText in O(|otherText|). */
    public String longestCommonSubstring(String otherText) {
        int state = 0;
        int currentLen = 0;
        int maxLen = 0;
        int bestEndIndex = -1;

        for (int i = 0; i < otherText.length(); i++) {
            int ch = otherText.charAt(i);
            if (ch >= 128) ch = ch % 128;

            while (state != -1 && states[state].next[ch] == -1) {
                state = states[state].link;
                if (state != -1) {
                    currentLen = states[state].len;
                }
            }

            if (state == -1) {
                state = 0;
                currentLen = 0;
            } else {
                state = states[state].next[ch];
                currentLen++;
            }

            if (currentLen > maxLen) {
                maxLen = currentLen;
                bestEndIndex = i;
            }
        }

        if (maxLen == 0 || bestEndIndex == -1) {
            return "";
        }
        return otherText.substring(bestEndIndex - maxLen + 1, bestEndIndex + 1);
    }

    public int getStateCount() {
        return size;
    }

    /** Standalone testing demonstration for Suffix Automaton. */
    public static void main(String[] args) {
        String eventDescription = "Registration for the annual EventFlow hackathon starts at Hall A. Welcome to EventFlow!";
        System.out.println("Text: \"" + eventDescription + "\"");

        SuffixAutomaton sam = new SuffixAutomaton(eventDescription);
        System.out.println("Suffix Automaton built with " + sam.getStateCount() + " states.");

        String[] queries = {"EventFlow", "hackathon", "Hall A", "Workshop", "starts"};
        for (String q : queries) {
            boolean found = sam.contains(q);
            int count = sam.countOccurrences(q);
            System.out.printf("  Query \"%s\": found = %b, occurrences = %d\n", q, found, count);
        }

        String comparison = "Welcome to the EventFlow annual keynote conference!";
        String lcs = sam.longestCommonSubstring(comparison);
        System.out.println("\nLCS with \"" + comparison + "\":");
        System.out.println("  \"" + lcs + "\" (length " + lcs.length() + ")");
    }
}
