# EventFlow — Learning Guide (CO1–CO3)

Simple explanations you can rewrite in your own words for the exam.

---

## 1. Project structure

```
EventFlow/                  <- run the program from HERE
└── src/
    ├── datasets/           <- the corpus: plain .txt event documents
    └── texthack/           <- all Java code
        ├── Article.java        one document
        ├── Corpus.java         loads documents
        ├── EventFlow.java      menu / main program
        ├── stringalgorithms/   Module 2 searching
        ├── suffix/             Module 2 indexing
        └── dp/                 Module 3 dynamic programming
```

Three layers: **data** (Article, Corpus) → **algorithms** (three packages) → **interface** (EventFlow).
Each layer only knows about the one below it. Algorithms never read files, and Corpus never runs an algorithm. That separation is what the "do not dump everything into one giant class" rule is about.

---

## 2. How Java packages work

A package is a folder, and the folder path must match the package name.

`src/texthack/dp/Levenshtein.java` starts with `package texthack.dp;` and its full name is `texthack.dp.Levenshtein`.

To use a class from another package you `import` it:

```java
import texthack.dp.Levenshtein;   // now you can write Levenshtein.distance(...)
```

Classes in the *same* package (Article and Corpus) need no import.
`src` is the **source root** — the folder that package names are counted from. That is why every IDE setup step involves marking `src` as the source root.

---

## 3. How Corpus loads files

```java
public static ArrayList<Article> repository = new ArrayList<>();
```

`repository` is `static`, so there is one shared list for the whole program. Any class can read `Corpus.repository`.

`loadFiles(File folder)` is **recursive**:

1. Ask the folder for its children with `listFiles()`.
2. If a child is a directory → call `loadFiles` on it (go one level deeper).
3. If a child ends in `.txt` → read it with `Files.readString()` and add a new `Article` to the repository, giving it the next id.
4. If `listFiles()` returns null (no permission / not a folder) → just return.

The recursion is the reason sub-folders inside `datasets` would also work.

**Why it can fail:** the path `"src/datasets"` is *relative to the working directory*. Java resolves it against wherever you launched the program, not where the .java file lives. Run from the project root or fix the working directory in the IDE.

---

## 4. How EventFlow calls algorithms

```
main()
 └── loadCorpus()          reads src/datasets once
 └── while (running)
      ├── printMenu()
      ├── readInt()        the user's choice
      └── switch(choice) → runKMP(), runLevenshtein(), ...
```

Each `runXxx()` method does the same four things:

1. print a header,
2. collect input (a document via `chooseArticle()`, or text via `readLine()`),
3. call the algorithm class — one line, e.g. `KMP.search(article.content, pattern)`,
4. print the result and the complexity.

All the algorithm methods are `static`, so no object is created: `Levenshtein.distance(a, b)`.
The exception is `AhoCorasick`, which is `new`-ed because it stores a trie between calls.

---

## 5. Naive string matching

**Main idea:** Line the pattern up at position 0 of the text and compare letter by letter. If something doesn't match, slide the pattern one step right and start comparing again from the beginning.

**Input:** text T (length n), pattern P (length m).

**Step-by-step:**

Step 1: for each starting position i from 0 to n−m,
Step 2: compare P[0] with T[i], P[1] with T[i+1], … while they are equal,
Step 3: if all m characters matched, return i,
Step 4: otherwise increase i by 1 and repeat.

**Example:** T = "abcabd", P = "abd".
i=0: a=a, b=b, c≠d → fail. i=1: b≠a → fail. i=2: c≠a → fail. i=3: a=a, b=b, d=d → found at 3.

**Time:** O(nm) — worst case is text "aaaaaa" with pattern "aaab".
**Space:** O(1).

**Where EventFlow uses it:** baseline event-name search — the version we compare the fast algorithms against.

---

## 6. KMP (Knuth–Morris–Pratt)

**Main idea:** Naive search throws away everything it learned on a mismatch. KMP remembers it. If the first 5 characters matched, we already know what those 5 text characters *are*, so we can slide the pattern forward by more than 1 and never move the text pointer backwards.

The amount we can slide comes from the **LPS array**: `lps[i]` = length of the longest proper prefix of `P[0..i]` that is also a suffix of `P[0..i]`.

**Input:** text T, pattern P.

**Step-by-step:**

Step 1: build LPS. Keep `length` = current matching prefix length, start `i = 1`.
  If `P[i] == P[length]` → `length++`, `lps[i] = length`, `i++`.
  Else if `length != 0` → `length = lps[length-1]` (fall back, don't move i).
  Else → `lps[i] = 0`, `i++`.
Step 2: search with two pointers, `i` in the text and `j` in the pattern.
Step 3: if `T[i] == P[j]` → advance both. If `j` reaches m, a match ended at `i-m`.
Step 4: on a mismatch: if `j != 0` set `j = lps[j-1]`; else `i++`.

**Example:** P = "ababaca" → LPS = `0 0 1 2 3 0 1`.
Mismatch after matching "ababa" (j = 5)? `lps[4] = 3`, so jump to j = 3 — the "aba" prefix is already aligned, no need to re-check it.

**Time:** O(n + m). **Space:** O(m).

**Where EventFlow uses it:** the main fast single-keyword search over long event documents (menu option 2 also prints the LPS array so you can trace it).

---

## 7. Z-function

**Main idea:** For a string S, `z[i]` = how many characters starting at position i match the beginning of S. To search, glue the pattern and text together with a separator that appears in neither: `P + '\u0001' + T`. Anywhere `z[i] == m`, the pattern occurs.

**Input:** a string (for `buildZ`), or text + pattern (for `search`).

**Step-by-step:**

Step 1: keep a window `[left, right]` — the rightmost segment known to match a prefix.
Step 2: for each i, if `i < right`, copy the mirror value: `z[i] = min(right - i, z[i - left])`. This is free information.
Step 3: extend `z[i]` by comparing characters one at a time.
Step 4: if `i + z[i] > right`, update `left = i`, `right = i + z[i]`.

**Example:** S = "aabxaabxcaabxaabxay".
`z[4] = 4` because "aabx" at position 4 matches the prefix "aabx".

**Time:** O(n + m). **Space:** O(n + m) — the Z-array plus the joined string.

**Where EventFlow uses it:** an alternative linear search, and useful for spotting repeated prefixes in event descriptions.

---

## 8. Rabin–Karp

**Main idea:** Turn every window of length m into a number (a hash) and compare numbers instead of characters. The trick is the **rolling hash**: the next window's hash is computed from the previous one in O(1) — drop the leaving character, shift, add the entering character.

Two different strings can share a hash (a *spurious hit*), so every hash match is verified character by character.

**Input:** text T, pattern P, a base (256) and a large prime modulus (10⁹+7).

**Step-by-step:**

Step 1: hash the pattern and the first window: `h = h * BASE + char`, all mod MOD.
Step 2: compute `highestPower = BASE^(m−1) mod MOD`.
Step 3: at each position compare hashes. If equal, verify the characters; if they really match, report i.
Step 4: roll forward: `h = (h − T[i]*highestPower) * BASE + T[i+m]`, all mod MOD (add MOD before taking mod so it never goes negative).

**Example:** searching "REG2026B" in "REG2026A REG2026B REG2026C" → found at index 9.

**Time:** O(n + m) average, O(nm) worst case (when hashes collide constantly).
**Space:** O(1).

**Where EventFlow uses it:** registration / booking codes — lots of equal-length windows to test quickly.

---

## 9. Aho–Corasick

**Main idea:** KMP for many patterns at once. Put all patterns in a **trie**, then give every node a **failure link** pointing to the longest proper suffix of the current prefix that is also a prefix of some pattern. Scan the text once, never going backwards.

**Input:** a set of patterns (each with an id), then a text.

**Step-by-step:**

Step 1: `addPattern` — insert each pattern into the trie, marking the last node with its id and length.
Step 2: `buildFailureLinks` — BFS from the root. Children of the root fail to the root. For any other node, `failure[child] = goTo[failure[parent]][c]`. Also set an **output link** to the nearest ancestor-by-failure that ends a pattern, so shorter patterns hidden inside longer ones still get reported.
Step 3: `search` — walk the text one character at a time following `goTo` edges. Report the pattern ending at the current node, then follow output links to report the shorter ones.

**Example:** patterns {he, she, his, hers} on text "ushers": at 'e' after "she" you report "she", and the output link also reports "he".

**Time:** O(total pattern length) to build + O(n + number of matches) to scan.
**Space:** O(total pattern length × alphabet).

**Where EventFlow uses it:** scanning one document for a whole keyword list (registration, coding, presentation…) in a single pass.

---

## 10. Suffix Array

**Main idea:** Write down every suffix of the text with its starting index, sort them alphabetically, and keep only the indices. That sorted list is the suffix array. Because it is sorted, any substring search becomes a binary search.

**Input:** a text of length n. **Output:** `int[]` of n starting indices.

**Step-by-step (prefix doubling):**

Step 1: rank each suffix by its first 1 character (just the character code).
Step 2: sort the suffixes by the pair `(rank[i], rank[i+gap])`. With gap = 1 that pair describes the first 2 characters.
Step 3: renumber the ranks from the new order (equal pairs get equal ranks).
Step 4: double the gap (1 → 2 → 4 → 8 …) and repeat until every suffix has a unique rank.

**Example:** "banana" → suffixes sorted: a(5), ana(3), anana(1), banana(0), na(4), nana(2) → SA = `[5, 3, 1, 0, 4, 2]`.

**Time:** O(n log²n) — log n rounds, each with an O(n log n) sort.
**Space:** O(n).

**Conceptual extras (preview level, in the file comments):**
- **SA-IS** classifies suffixes as S-type / L-type, sorts the "LMS" substrings recursively, and induces everything else. Same array in O(n), much harder to code.
- **Suffix tree**: a compressed trie of all suffixes; substring queries in O(m), but 10–20 bytes per character and Ukkonen's algorithm is hard.
- **Suffix automaton**: the smallest DFA accepting all suffixes, ≤ 2n−1 states, built online in O(n); great for counting distinct substrings. Array = sorted list, automaton = graph.

**Where EventFlow uses it:** indexing an event document so any activity name or venue can be located, and as the input to Kasai.

---

## 11. LCP (the array itself)

**Main idea:** `lcp[i]` = how many characters the suffix at `SA[i]` shares at the start with the suffix at `SA[i−1]` — that is, with its *neighbour in sorted order*. `lcp[0]` is 0 because the first suffix has no previous neighbour.

Sorted neighbours are the most similar suffixes, so a large LCP value means a long piece of text appears in two places. The **largest value in the LCP array is the length of the longest repeated substring**.

**Example ("banana"):** SA = `[5, 3, 1, 0, 4, 2]`, LCP = `[0, 1, 3, 0, 0, 2]`.
The 3 comes from "ana" being a prefix of both "ana" and "anana" → longest repeated substring = "ana".

---

## 12. Kasai's algorithm (how the LCP array is built fast)

**Main idea:** Computing each LCP from scratch is O(n²). Kasai processes suffixes in **text order** instead of sorted order. Moving from suffix i to suffix i+1 just drops one leading character, so the common prefix length `k` can fall by at most 1 each step. Since `k` only ever drops by 1 per step and rises at most n times overall, the total work is linear.

**Input:** the text and its suffix array.

**Step-by-step:**

Step 1: build `rank[]` — the inverse of SA, so `rank[suffix] = its position in SA`.
Step 2: set `k = 0`, then loop i from 0 to n−1 (text order).
Step 3: if `rank[i] == 0`, set `k = 0` and skip (no previous neighbour).
Step 4: let `j = SA[rank[i] - 1]` (the previous suffix in sorted order). While the characters at `i+k` and `j+k` are equal, `k++`.
Step 5: store `lcp[rank[i]] = k`, then do `k--` if `k > 0` — the key observation.

**Time:** O(n). **Space:** O(n).

**Where EventFlow uses it:** finding repeated or copied text inside and between event documents (menu option 7 also prints the longest repeated substring).

---

## 13. Levenshtein distance

**DP framework, stated once:**
- *State* — what one table cell means.
- *Base cases* — cells you can fill without any recurrence.
- *Recurrence* — how a cell is built from earlier cells.
- *Overlapping subproblems* — the same cell is needed by several later cells, so we store it instead of recomputing.
- *Optimal substructure* — the best answer for a big case is built from best answers of smaller cases.
- *Bottom-up* — fill the table in an order that guarantees the cells you need are already done.

**Main idea:** the fewest single-character edits (insert, delete, replace) that turn string A into string B.

**Input:** strings A (length n) and B (length m).

**Step-by-step (Wagner–Fischer):**

Step 1: State — `dp[i][j]` = distance between the first i characters of A and the first j of B.
Step 2: Base cases — `dp[i][0] = i` (delete everything), `dp[0][j] = j` (insert everything).
Step 3: Recurrence — if `A[i−1] == B[j−1]` then `dp[i][j] = dp[i−1][j−1]` (free).
  Otherwise `dp[i][j] = 1 + min(dp[i−1][j]` delete`, dp[i][j−1]` insert`, dp[i−1][j−1]` replace`)`.
Step 4: Answer — `dp[n][m]`, the bottom-right cell.

**Example:** kitten → sitting = 3 (k→s, e→i, add g).

**Time:** O(nm). **Space:** O(nm) (can be reduced to O(min(n,m)) with two rows, but the full table is kept here so `printTable` can show it).

**Where EventFlow uses it:** spelling correction of event and participant names — "Hackaton" → "Hackathon".

---

## 14. Damerau–Levenshtein distance

**Main idea:** exactly Levenshtein plus a fourth operation — **transposition**, swapping two adjacent characters, costing 1. Typing slips are usually swaps, so this matches real typos better.

**Input:** strings A and B.

**Step-by-step:** same table and same three cases as Levenshtein, plus one extra line:

Step 4: if `i > 1 && j > 1 && A[i−1] == B[j−2] && A[i−2] == B[j−1]`, then also consider `dp[i−2][j−2] + 1` and take the minimum.

**Example:** "form" → "from". Levenshtein says 2 (two replaces); Damerau–Levenshtein says 1 (one swap).

**Time:** O(nm). **Space:** O(nm).

Note: this is the **restricted / optimal-string-alignment** version, which the course uses. It never edits the same substring twice, which is why `ca` → `abc` gives 3, not 2.

**Where EventFlow uses it:** correcting adjacent-key typing slips in participant data.

---

## 15. Weighted edit distance

**Main idea:** the three operations no longer cost 1 each. Here insert = 1, delete = 1, **substitution = 2**. Since a substitution (2) is never cheaper than a delete plus an insert (1+1 = 2), the algorithm stops preferring replacements.

**Input:** strings A and B, plus the three cost constants.

**Step-by-step:**

Step 1: Base cases — `dp[i][0] = i * DELETE_COST`, `dp[0][j] = j * INSERT_COST`.
Step 2: if the characters are equal → `dp[i][j] = dp[i−1][j−1]`, cost 0.
Step 3: otherwise take the min of `dp[i−1][j] + DELETE_COST`, `dp[i][j−1] + INSERT_COST`, `dp[i−1][j−1] + SUBSTITUTION_COST`.

**Example:** kitten → sitting. Plain Levenshtein = 3; weighted = 5, because the two substitutions now cost 2 each.

**Time:** O(nm). **Space:** O(nm).

**Where EventFlow uses it:** registration codes, where a *wrong* character is a more serious error than a missing or extra one.

---

## 16. Needleman–Wunsch (global alignment)

**Main idea:** instead of counting edits, we **score** an alignment and maximise it. *Global* means the whole of A is aligned against the whole of B, padding with gaps where needed.

**Input:** sequences A and B; match = 1, mismatch = −1, gap = −2.

**Step-by-step:**

Step 1: State — `dp[i][j]` = best score aligning A[0..i−1] with B[0..j−1].
Step 2: Base cases — `dp[i][0] = i * gap`, `dp[0][j] = j * gap` (leading gaps must be paid for).
Step 3: Recurrence — `dp[i][j] = max(dp[i−1][j−1] + (match or mismatch), dp[i−1][j] + gap, dp[i][j−1] + gap)`.
Step 4: Answer — `dp[n][m]`, the bottom-right corner. The score can be negative.

**Example:** GATTACA vs GCATGCU with these scores = −1.

**Time:** O(nm). **Space:** O(nm).

**Where EventFlow uses it:** comparing two complete event schedules end to end.

---

## 17. Smith–Waterman (local alignment)

**Main idea:** find the best matching *region*, not a full alignment. One change from Needleman–Wunsch: a **0** in the max. A running score is never allowed to go negative — it resets, so a new alignment can begin anywhere.

**Input:** sequences A and B; match = 1, mismatch = −1, gap = −2.

**Step-by-step:**

Step 1: Base cases — the whole first row and first column are 0 (a local alignment can start anywhere for free).
Step 2: Recurrence — `dp[i][j] = max(0, diagonal + (match or mismatch), up + gap, left + gap)`.
Step 3: Answer — the **maximum value anywhere in the table**, not the corner. Track it while filling.

**Example:** GGTTGACTA vs TGTTACGG scores 3 — the shared "GTT" region — even though the sequences differ overall.

**Time:** O(nm). **Space:** O(nm).

**Where EventFlow uses it:** finding a shared block of activities inside two otherwise different schedules.

Exam line: **global = whole vs whole, answer in the corner; local = best region, answer is the table maximum, extra 0 in the recurrence.**

---

## 18. Matrix-chain multiplication

**Main idea:** Matrix multiplication is associative — `(AB)C` and `A(BC)` give the same matrix — but the *cost* differs enormously. Find the cheapest bracketing. Multiplying a p×q by a q×r matrix costs p·q·r scalar multiplications.

**Input:** `dimensions[]` of length n+1, where matrix i is `dimensions[i−1] × dimensions[i]`.

**Step-by-step (interval DP):**

Step 1: State — `dp[i][j]` = cheapest way to multiply matrices i through j.
Step 2: Base case — `dp[i][i] = 0`, a single matrix needs no work.
Step 3: Recurrence — try every split point k between i and j:
  `dp[i][j] = min over k of ( dp[i][k] + dp[k+1][j] + d[i−1]*d[k]*d[j] )`.
Step 4: Fill by **increasing chain length** (2, 3, 4 …), so both halves are already solved. Answer is `dp[1][n]`.

**Example:** dimensions {10, 30, 5, 60} → A=10×30, B=30×5, C=5×60.
`(AB)C` = 10·30·5 + 10·5·60 = 1500 + 3000 = **4500**.
`A(BC)` = 30·5·60 + 10·30·60 = 9000 + 18000 = 27000.

**Time:** O(n³) — O(n²) intervals × O(n) split points. **Space:** O(n²).

**Where EventFlow uses it:** choosing the cheapest order for a chain of data-processing / report-generation operations.

---

## 19. Optimal Binary Search Tree

**Main idea:** given sorted keys and how often each is searched, build the BST with the smallest *expected* search cost. Frequently searched keys should sit near the root. A key at depth d (root = 1) costs `frequency × d`.

**Input:** sorted `keys[]` and matching `frequency[]`.

**Step-by-step (interval DP):**

Step 1: State — `dp[i][j]` = best cost for keys i..j.
Step 2: Base case — `dp[i][i] = frequency[i]`.
Step 3: Recurrence — try each key r in i..j as the root:
  `dp[i][j] = min over r of ( dp[i][r−1] + dp[r+1][j] + sum(frequency[i..j]) )`.
Step 4: Why add the sum? Choosing a root pushes every key in the interval one level deeper, costing one extra comparison per search — which is exactly the total frequency of the interval.

**Example:** keys {10, 12, 20}, frequencies {34, 8, 50} → minimum cost **142** (20 as the root).

**Time:** O(n³). **Space:** O(n²).
Knuth's optimisation narrows the root search range to get O(n²), but understanding comes first.

**Where EventFlow uses it:** arranging frequently searched event information (popular events, top venues) so common lookups are fastest.

---

## 20. Bitmask DP — Travelling Salesman

**Main idea:** a **mask** is an integer used as a set. Bit i of the mask is 1 when city i has been visited. With n ≤ ~20, all 2ⁿ subsets fit in a table, so we can brute-force intelligently instead of trying n! orders.

Bit operations to memorise:
`1 << i` — the set containing only i. `mask & (1 << i)` — is i in the set? `mask | (1 << i)` — add i.

**Input:** an n×n distance matrix.

**Step-by-step:**

Step 1: State — `dp[mask][current]` = cheapest way to start at city 0, visit exactly the cities in `mask`, and now be standing at `current`.
Step 2: Base case — `dp[1][0] = 0` (only city 0 visited, standing there). Everything else = infinity.
Step 3: Recurrence — for each reachable `dp[mask][current]`, for each `next` not in the mask:
  `dp[mask | (1<<next)][next] = min(that, dp[mask][current] + distance[current][next])`.
Step 4: Answer — with `fullMask = (1<<n) − 1`, take `min over last of ( dp[fullMask][last] + distance[last][0] )` to close the tour.

**Example:** the 4-city matrix in the file gives **80** (0 → 1 → 3 → 2 → 0).

**Time:** O(2ⁿ · n²) — 2ⁿ masks × n current cities × n next cities. **Space:** O(2ⁿ · n).

**Where EventFlow uses it:** cheapest route between venues, or the best order for a small set of activities.

---

## 21. Hamiltonian path

**Main idea:** does a path exist that visits **every** vertex exactly once? (A Hamiltonian *cycle* would also have to return to the start; a path does not.) Same bitmask table as TSP, but storing `true/false` instead of a cost.

**Input:** an adjacency matrix where `graph[i][j] == 1` means an edge from i to j.

**Step-by-step:**

Step 1: State — `dp[mask][last]` = true if some path visits exactly the vertices in `mask` and ends at `last`.
Step 2: Base case — `dp[1 << v][v] = true` for every v, because a path may start anywhere.
Step 3: Recurrence — if `dp[mask][last]` is true, `next` is not in the mask, and `graph[last][next] == 1`, then set `dp[mask | (1<<next)][next] = true`.
Step 4: Answer — true if `dp[fullMask][v]` is true for any v.

**Example:** the 4-vertex graph in the file → true (0 → 1 → 2 → 3 is valid).

**Time:** O(2ⁿ · n²). **Space:** O(2ⁿ · n).

Worth saying in a viva: this problem is NP-complete, so 2ⁿ is the price of an exact answer; bitmask DP just makes n ≈ 20 feasible instead of n ≈ 10.

**Where EventFlow uses it:** checking whether a schedule exists that covers every activity exactly once, given the allowed transitions.

---

## 22. SOS DP (Sum Over Subsets)

**Main idea:** given a value for every mask, compute for each mask the **sum of the values of all its submasks**. The obvious way is O(3ⁿ). SOS DP does it in O(n·2ⁿ) by handling one bit at a time.

**Input:** `values[]` of length 2ⁿ.

**Step-by-step:**

Step 1: start with `dp[mask] = values[mask]`.
Step 2: for each bit b from 0 to n−1,
Step 3: for each mask, if mask contains bit b, do `dp[mask] += dp[mask ^ (1<<b)]` (add the version of the mask with b removed).
Step 4: after the last bit, `dp[mask]` is the full sum over all submasks.

**What `dp[mask]` means mid-way:** after processing bits 0..b−1, `dp[mask]` is the sum over the submasks that differ from `mask` only in those first b bits. Each pass "merges in" one more dimension — that is why every bit contributes exactly once and no submask is double counted.

**Example:** n = 2, values = {1, 2, 3, 4}.
dp[0] = 1, dp[1] = 1+2 = 3, dp[2] = 1+3 = 4, dp[3] = 1+2+3+4 = **10**.

**Time:** O(n · 2ⁿ). **Space:** O(2ⁿ).

**Where EventFlow uses it:** for every combination of activity categories, the total participants interested in any subset of those categories.

---

## Bonus: when DP is the wrong tool

Expect this as a short-answer question. DP is a bad fit when:

- **No overlapping subproblems** — if each subproblem appears once, the table is pure overhead; use plain recursion or divide and conquer (merge sort doesn't need DP).
- **A greedy rule is provably correct** — simpler and faster (activity selection by earliest finish time, Huffman coding).
- **The state space is too large** — bitmask DP at n = 40 would need 2⁴⁰ states; no memory will hold it.
- **Bitmask compression still leaves an exponential table** — compressing the state doesn't change the exponent, only the constant.
- **No optimal-substructure property** — if the best big solution isn't built from best small ones, the recurrence is simply wrong. Longest *simple* path in a general graph is the classic example.


---

## 23. SA-IS (Suffix Array Induced Sorting)

**Main idea:** Build the Suffix Array in strictly linear O(n) time. Classifies suffixes into S-type (lexicographically smaller than their right neighbor) and L-type (larger). By finding Leftmost S-type (LMS) characters, sorting LMS substrings recursively, and bucket-sorting the remaining suffixes, SA-IS induces the full suffix array order in O(n) time.

**Input:** Text string of length n.

**Step-by-step:**
Step 1: Classify characters backwards: suffix[n-1] is S-type. If text[i] < text[i+1], suffix[i] is S-type; if text[i] > text[i+1], L-type; if equal, inherits type[i+1].
Step 2: LMS characters are S-type characters whose left neighbor is L-type.
Step 3: Induce sort LMS substrings into bucket tails.
Step 4: Name LMS substrings. If all names are unique, the LMS order is resolved; if duplicate names exist, recurse on the summary string of names.
Step 5: Induce all L-type suffixes from left-to-right into bucket heads, and all S-type suffixes from right-to-left into bucket tails.

**Time:** O(n). **Space:** O(n).

**Where EventFlow uses it:** Fast linear-time indexing of massive event archives, schedules, and participant records where O(n log² n) would be too slow.

---

## 24. Suffix Automaton (SAM)

**Main idea:** The minimal Deterministic Finite Automaton (DFA) that accepts all suffixes of a string. Contains at most 2n - 1 states and 3n - 4 transitions, built online in linear time O(n).

**Input:** Text string of length n.

**Step-by-step:**
Step 1: Each state represents an equivalence class of substrings having identical `endpos` sets (ending positions).
Step 2: Built character-by-character by creating a new state, updating suffix links (`link`), and cloning transition nodes when length constraints are violated.
Step 3: Querying whether a pattern P of length m is a substring takes O(m) time by following automaton transitions.
Step 4: Finding the Longest Common Substring (LCS) between two texts runs in O(|T1| + |T2|).

**Time:** O(n) construction, O(m) substring check, O(|T2|) LCS. **Space:** O(n · Σ).

**Where EventFlow uses it:** Instantaneous substring searches across multi-venue event catalogs, counting keyword frequencies, and identifying shared schedule blocks between distinct tracks.

---

## 25. Ford-Fulkerson Algorithm (Network Flow)

**Main idea:** Given a directed flow network with edge capacities, compute the maximum participant flow from source s to sink t. Augments flow along residual paths found via Depth-First Search (DFS) until no augmenting path remains.

**Input:** Directed graph with edge capacities, source s, sink t.

**Step-by-step:**
Step 1: Initialize flow on all edges to 0. Residual capacity is `capacity[u][v] - flow[u][v]`.
Step 2: Use DFS to search for a directed path from s to t where all residual capacities are > 0.
Step 3: Find the bottleneck capacity c_min on the path.
Step 4: Augment flow along the path: add c_min to forward edges, subtract c_min from reverse edges.
Step 5: Repeat until no s-t path exists in the residual graph.
Step 6: Min-Cut theorem: Reachable vertices from s in the residual graph form set S; edges leaving S to Sink set T form the bottleneck minimum cut.

**Time:** O(E · max_flow). **Space:** O(V²).

**Where EventFlow uses it:** Modeling attendee flow through facility checkpoints (entry gates, security screening, registration desks, keynote halls) to prevent stampedes and identify constriction bottlenecks.

---

## 26. Edmonds-Karp Algorithm (Venue-Capacity Analysis)

**Main idea:** Implementation of Ford-Fulkerson using Breadth-First Search (BFS) to find shortest augmenting paths (fewest edges). Guarantees strongly polynomial convergence independent of edge capacities.

**Input:** Directed graph with venue passage capacities, source s, sink t.

**Step-by-step:**
Step 1: Run BFS from source s to sink t in the residual graph.
Step 2: If sink t is unreachable, terminate; max flow has been reached.
Step 3: Backtrack via parent pointers to find the bottleneck residual capacity on the BFS path.
Step 4: Push bottleneck flow along the path and update forward and reverse residual edges.
Step 5: Identify fully saturated corridors (where flow == capacity) as venue admittance choke points.

**Time:** O(V · E²). **Space:** O(V²).

**Where EventFlow uses it:** Venue-capacity planning, determining maximum safe admittance rate (visitors/min) and locating 100% saturated passageways.

---

## 27. Dinic's Algorithm (Large-Scale Event Flow Networks)

**Main idea:** Faster max-flow algorithm for large networks using Level Graphs and Blocking Flows. Divides execution into phases, where each phase pushes multiple augmenting paths simultaneously until no more flow can reach the sink at the current shortest distance.

**Input:** Directed graph with capacities, source s, sink t.

**Step-by-step:**
Step 1: Construct a Level Graph using BFS (`level[v] = level[u] + 1`). If `level[t] == -1`, terminate.
Step 2: Find a blocking flow using DFS along admissible edges (`level[v] == level[u] + 1`).
Step 3: Maintain work pointers `ptr[u]` so saturated and dead-end edges are never revisited during a phase.
Step 4: Repeat phases until sink is unreachable. The number of phases is at most V - 1.

**Time:** O(V² · E) general, O(E · √V) on unit networks. **Space:** O(V + E).

**Where EventFlow uses it:** High-throughput crowd routing across massive festival grounds, transit hubs, and multi-stage convention centers.

---

## 28. Maximum Bipartite Matching (Volunteer & Staff Allocation)

**Main idea:** Given a bipartite graph with staff members on the left (U) and event activities on the right (V), find the maximum number of 1-to-1 assignments such that each staff member is assigned to at most one activity they are eligible for.

**Input:** Bipartite graph with eligibility edges.

**Step-by-step:**
Step 1: Represent assignments using `matchActivity[v] = u` (-1 if unassigned).
Step 2: For each staff member u, search for an augmenting path using DFS.
Step 3: If an activity v is unassigned, assign it to u. If v is already assigned to u', recursively try to reassign u' to an alternate activity.
Step 4: If an augmenting path is found, flip matching edges along the path, increasing total matching size by 1.

**Time:** O(V · E). **Space:** O(V + E).

**Where EventFlow uses it:** Assigning certified volunteers and coordinators to stage presentations, workshops, and technical helpdesks according to skills and availability.

---

## 29. König's Theorem (Assignment Conflict Analysis)

**Main idea:** In any bipartite graph:
$$\text{Size of Maximum Matching} = \text{Size of Minimum Vertex Cover}$$
And the Maximum Independent Set is the complement:
$$|V| - |\text{Min Vertex Cover}|$$

**Input:** Bipartite graph and its maximum matching.

**Step-by-step:**
Step 1: Find maximum matching M.
Step 2: Orient edges: unmatched edges from Left to Right, matched edges from Right to Left.
Step 3: Run BFS from all unmatched Left vertices to find reachable set Z.
Step 4: Minimum Vertex Cover = (Left \ Z) ∪ (Right ∩ Z).
Step 5: Maximum Independent Set = (Left ∪ Right) \ MinVertexCover.

**Time:** O(V · E). **Space:** O(V + E).

**Where EventFlow uses it:** Pinpointing the exact core bottleneck set of staff and activities responsible for scheduling gridlock and resource conflicts.

---

## 30. SAT & 3-SAT (Event-Scheduling Constraints Solver)

**Main idea:** Represent complex scheduling rules (room capacities, speaker non-overlap, equipment prerequisites) as a Boolean formula in 3-Conjunctive Normal Form (3-CNF). The DPLL (Davis-Putnam-Logemann-Loveland) backtracking algorithm determines if the schedule is mathematically satisfiable.

**Input:** 3-CNF formula with n variables and k clauses (l₁ ∨ l₂ ∨ l₃).

**Step-by-step:**
Step 1: Unit Propagation — If a clause has only one unassigned literal, force it to TRUE.
Step 2: Conflict Check — If any clause has all literals FALSE, backtrack immediately.
Step 3: Branching — Pick an unassigned variable, assign TRUE and recurse. If it fails, assign FALSE and recurse.
Step 4: Satisfiable if all clauses evaluate to TRUE.

**Time:** O(2ⁿ) worst-case (NP-Complete). **Space:** O(n + m).

**Where EventFlow uses it:** Automated verification of master event timetables ensuring zero conflicting constraints.

---

## 31. 3-SAT -> CLIQUE Reduction

**Main idea:** Karp's polynomial-time reduction proving CLIQUE is NP-Complete. Transforms a 3-SAT formula with k clauses into a graph G that possesses a k-clique if and only if the formula is satisfiable.

**Input:** 3-SAT instance with k clauses.

**Step-by-step:**
Step 1: Create 3 vertices for each clause (one for each literal), giving |V| = 3k.
Step 2: Add an undirected edge between two vertices if:
  - They belong to different clauses.
  - The literals are not negations of each other (e.g. x₁ and ~x₁ cannot have an edge).
Step 3: A clique of size k in G must pick exactly one literal vertex per clause without contradiction, yielding a satisfying assignment.

**Time:** O(k²) reduction time. **Space:** O(k²).

**Where EventFlow uses it:** Transforming event scheduling rules into compatible activity clusters for conflict analysis.

---

## 32. CLIQUE -> INDEPENDENT-SET -> VERTEX-COVER

**Main idea:** The classic trio of Karp reductions establishing structural equivalence:
1. S is a Clique in G ⟺ S is an Independent Set in complement graph G_bar.
2. S is an Independent Set in H ⟺ V \ S is a Vertex Cover in H.

**Input:** Graph G = (V, E).

**Step-by-step:**
Step 1: Graph Complement: G_bar has edge (u, v) iff (u, v) is NOT in G.
Step 2: Independent Set S: no two vertices in S share an edge.
Step 3: Vertex Cover C: every edge in the graph touches at least one vertex in C.
Step 4: If S is the Maximum Independent Set, then V \ S is the Minimum Vertex Cover!

**Time:** O(V²) reduction time. **Space:** O(V²).

**Where EventFlow uses it:** On event conflict graphs:
- *Max Independent Set*: The maximum number of activities that can run simultaneously with zero conflicts.
- *Min Vertex Cover*: The minimum set of activities/supervisors needed to touch and monitor every potential conflict.

---

## 33. Vertex-Cover 2-Approximation

**Main idea:** Because Minimum Vertex Cover is NP-hard, finding the exact minimum is intractable for large events. A greedy maximal matching edge-picking algorithm achieves an answer guaranteed to be at most twice the optimal size (`|C| <= 2 * OPT`) in linear time O(V + E).

**Input:** Conflict graph G = (V, E).

**Step-by-step:**
Step 1: Set cover C = empty.
Step 2: While there is an uncovered edge (u, v):
  - Add both u and v to cover C.
  - Mark all edges incident to either u or v as covered.
Step 3: Selected edges form a matching M (disjoint endpoints).
Step 4: Since any valid vertex cover OPT must pick at least one endpoint from each edge in M, `|OPT| >= |M|`.
Step 5: The algorithm selects `2|M|` vertices, proving `|C| = 2|M| <= 2 * |OPT|`.

**Time:** O(V + E). **Space:** O(V + E).

**Where EventFlow uses it:** Rapidly deploying a compact, near-optimal set of monitoring stations and supervisors to cover all venue conflict points.

---

## 34. Randomized QuickSort

**Main idea:** Standard deterministic QuickSort degrades to O(n²) on already sorted or duplicate lists. Randomized QuickSort picks a pivot uniformly at random from [low, high], ensuring balanced partitioning with high probability regardless of input order.

**Input:** Array of participant records with priority scores.

**Step-by-step:**
Step 1: Pick random index in [low, high] and swap with high.
Step 2: Partition array around the random pivot score.
Step 3: Recursively sort left and right subarrays.
Step 4: Expected comparisons: 2n ln(n) = O(n log n).

**Time:** O(n log n) expected. **Space:** O(log n) stack.

**Where EventFlow uses it:** Ranking thousands of event participants by score, registration timestamp, or queue priority without risk of worst-case slowdowns.

---

## 35. Reservoir Sampling (Algorithm R)

**Main idea:** Select a uniform random sample of k items from a continuous, live data stream of unknown total length n in a single pass using only O(k) memory.

**Input:** Continuous data stream, sample size k.

**Step-by-step:**
Step 1: Place the first k items directly into the reservoir array.
Step 2: For each incoming i-th element (for i = k+1, k+2, ..., n):
  - Generate a random integer j in [0, i - 1].
  - If j < k, replace `reservoir[j]` with the new element; otherwise discard it.
Step 3: Mathematical induction proves that after n arrivals, each element has an identical probability of exactly `k / n` of being in the reservoir.

**Time:** O(n) single pass. **Space:** O(k) storage.

**Where EventFlow uses it:** Live lucky draw lotteries, uniform attendee survey sampling, and real-time quality inspections during event check-ins.

---

## 36. Miller-Rabin Primality Test

**Main idea:** Probabilistic primality test for large numbers based on Fermat's Little Theorem and properties of non-trivial square roots of 1 modulo a prime. Runs in polynomial time O(k log³ n) with error probability at most 4⁻ᵏ.

**Input:** Candidate integer n, number of rounds k.

**Step-by-step:**
Step 1: Factor n - 1 into 2ˢ · d where d is odd.
Step 2: For each round, choose a random base a in [2, n - 2].
Step 3: Compute x = aᵈ mod n using modular binary exponentiation.
Step 4: If x == 1 or x == n - 1, round passes (non-witness).
Step 5: Repeat squaring x = x² mod n up to s - 1 times. If any x == n - 1, round passes.
Step 6: If x never reaches n - 1, n is proven composite and a is a witness.
Step 7: If all k rounds pass, n is prime with error < 4⁻ᵏ.

**Time:** O(k · log³ n). **Space:** O(log n).

**Where EventFlow uses it:** Generating and validating large prime moduli for cryptographic ticketing tokens, anti-counterfeit QR codes, and secure distributed hash tables.

---

## 37. Blelloch Parallel Prefix Scan

**Main idea:** Work-efficient parallel scan algorithm that computes all running prefix sums in O(n) total operations (matching sequential work) and O(log n) parallel span/depth using an Up-Sweep reduce phase and a Down-Sweep phase.

**Input:** Array of integers of length n (padded to power of 2).

**Step-by-step:**
Step 1: Up-Sweep (Reduce Phase): Build partial sums in a balanced binary tree at stride 2ᵈ. Root contains the total sum.
Step 2: Set the root element (last position) to 0 (identity for addition).
Step 3: Down-Sweep Phase: Traverse down the tree from d = log₂(n) - 1 to 0:
  - `temp = leftChild; leftChild = rightChild; rightChild += temp;`
Step 4: Generates the exclusive prefix scan in parallel; adding original elements gives inclusive prefix sums.

**Time (Work):** O(n). **Span (Depth):** O(log n). **Space:** O(n).

**Where EventFlow uses it:** Real-time computation of cumulative attendee arrivals across multiple parallel gate turnstiles and live crowd forecasting.

---

## 38. Parallel Reduce (ForkJoin Framework)

**Main idea:** Aggregating event statistics (total attendees, peak gate queue, minimum ticket price) using a divide-and-conquer binary tree executed across multiple CPU cores via Java's `ForkJoinPool` / `RecursiveTask`.

**Input:** Array of event records, associative operator (SUM, MAX, MIN).

**Step-by-step:**
Step 1: If subarray size <= sequential threshold, compute sequentially.
Step 2: Otherwise, split into left and right halves.
Step 3: Fork left task to run asynchronously in parallel, compute right task on current thread.
Step 4: Join left task result and combine using the associative operator.

**Time (Work):** O(n). **Span (Depth):** O(log n). **Space:** O(log n).

**Where EventFlow uses it:** Real-time multi-core aggregation of venue attendance, average dwell time, and revenue across thousands of session logs.

---

## 39. Brent's Theorem for Parallel Processing

**Main idea:** Fundamental theorem of parallel scheduling. Given total work W (operations on 1 core) and critical path span S (execution time on infinite cores), the execution time T_p on p processors using a greedy level schedule satisfies:
$$T_p \le \frac{W - S}{p} + S < \frac{W}{p} + S$$

**Input:** Work per dependency level W_i, number of processors p.

**Step-by-step:**
Step 1: Total work W = sum(W_i); Span S = number of dependency levels.
Step 2: At level i, time taken on p processors is `ceil(W_i / p)`.
Step 3: Total time: $T_p = \sum_{i=1}^S \lceil W_i / p \rceil$.
Step 4: Calculate Speedup $S_p = W / T_p$ and Efficiency $E_p = S_p / p$.
Step 5: The optimal processor allocation balance point is $p^* \approx W / S$. Beyond this, adding cores yields diminishing returns.

**Time:** O(S). **Space:** O(S).

**Where EventFlow uses it:** Sizing compute cluster pools for batch badge generation, multi-camera facial recognition streams, and large-scale attendee verification pipelines.
