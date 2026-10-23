
# Spellseek — Time and Space Complexity

## 1. Introduction

Spellseek is a spelling-correction engine that uses multiple data structures and string-processing algorithms to identify spelling mistakes and suggest corrections.

The Java implementation uses:

- **HashMap** to store dictionary words and their frequencies.
- **Trie** to support prefix-based autocomplete.
- **BK-tree** to find words that are similar to a misspelled word.
- **Edit distance** to measure the difference between words.
- **Frequency-based ranking** to help order spelling suggestions.

The HTML, CSS, and JavaScript interface provides browser-based access to spelling checks, autocomplete, and data-structure visualization.

This document describes the expected time and space complexity of these operations. Actual performance depends on dictionary size, word lengths, tree structure, and implementation details.

---

## 2. Notation Used

| Symbol | Meaning |
|---|---|
| `N` | Number of words in the dictionary |
| `D` | Total number of characters in all dictionary words |
| `L` | Length of the input word or prefix |
| `M` | Length of another word being compared |
| `V` | Number of BK-tree nodes visited during a search |
| `C` | Number of candidate suggestions collected |
| `K` | Number of suggestions returned |
| `T` | Total number of characters in the input sentence |

For a word of length `L`, edit-distance calculations may also depend on the length `M` of each candidate word.

---

## 3. HashMap Complexity

Spellseek uses a HashMap to associate dictionary words with their frequency information.

### Operations

| Operation | Expected Time Complexity | Worst-Case Consideration |
|---|---|---|
| Insert a word | `O(1)` | `O(N)` under pathological hash collisions |
| Look up a word | `O(1)` | `O(N)` under pathological hash collisions |
| Update an existing word | `O(1)` | Depends on hash collisions |
| Store all dictionary entries | `O(N)` space | `O(N)` entries |

These bounds describe the HashMap operations themselves. Processing or normalizing a word adds time proportional to its length, typically `O(L)`.

### Example

When a user enters `computer`, Spellseek normalizes the input and checks whether the word exists in its dictionary.

The expected dictionary lookup time is constant with respect to the number of stored words, excluding the time needed to process the input string.

**Advantage:** HashMap provides efficient exact-word lookup and frequency retrieval.

---

## 4. Trie Complexity

A Trie stores words using character-by-character paths. Words with common prefixes share part of the same path.

Spellseek uses a Trie for prefix-based autocomplete.

### Operations

| Operation | Time Complexity |
|---|---|
| Insert a word | `O(L)` expected with constant-time child access |
| Search for a word | `O(L)` expected with constant-time child access |
| Find the node for a prefix | `O(L)` expected with constant-time child access |
| Enumerate autocomplete matches | Depends on the size of the matching subtree |

The Trie operation bounds assume that each character transition can be accessed in constant expected time. If child lookup uses a different representation, its cost may change.

### Autocomplete complexity

For a prefix such as `pro`, Spellseek first follows the prefix path. It then explores the matching subtree to collect words.

Let `P` represent the number of Trie nodes explored and `R` represent the number of matching words collected.

A useful general bound is:

`O(L + P)`

If the implementation sorts all collected matches by frequency or another ranking rule, sorting can add:

`O(R log R)`

The actual cost depends on whether the implementation explores the entire matching subtree or stops after collecting a limited number of results.

### Space complexity

The Trie requires space proportional to the total number of stored characters:

`O(D)`

The actual memory requirement also includes node objects, child references, and stored word information.

**Advantage:** Trie makes prefix searches efficient without comparing the prefix against every dictionary word individually.

---

## 5. BK-tree Complexity

A BK-tree is a metric-tree data structure used for approximate string matching. It organizes words according to their edit distances from other words.

Spellseek searches the BK-tree to find candidate corrections within a permitted edit-distance range.

### Search complexity

Let:

- `N` be the number of words stored in the BK-tree.
- `L` be the input word length.
- `V` be the number of nodes visited during a search.

If each distance calculation compares strings of approximately length `L`, a simplified search-cost estimate is:

`O(V × L²)`

This estimate uses the standard dynamic-programming edit-distance cost for similarly sized words.

The number of visited nodes depends on the tree structure, query word, and permitted search radius.

| Tree behavior | Approximate Search Cost |
|---|---|
| Few nodes visited | `O(V × L²)`, where `V` is small |
| Many nodes visited | Increases with the number of visited nodes |
| Worst case | Can approach `O(N × L²)` for similarly sized words |

These are general bounds, not guaranteed timings for every query. A BK-tree can perform well in practice, but its search is not guaranteed to be logarithmic.

### Space complexity

The BK-tree stores dictionary words and its tree relationships. Its word-storage requirement is proportional to the total stored characters:

`O(D)`

Additional node and child-reference overhead depends on the implementation.

**Advantage:** BK-tree can reduce the number of dictionary words that must be examined during approximate matching.

---

## 6. Edit-Distance Complexity

Edit distance measures how different two strings are. The standard Levenshtein distance considers insertions, deletions, and substitutions.

Spellseek also uses a transposition-aware distance calculation to handle mistakes in which adjacent letters are swapped.

### Standard dynamic-programming edit distance

For two words of lengths `L` and `M`:

| Resource | Complexity |
|---|---|
| Time | `O(L × M)` |
| Space with a full dynamic-programming table | `O(L × M)` |

For similarly sized words of length `L`, these become:

- Time: `O(L²)`
- Space: `O(L²)`

The exact space bound depends on the implementation of the distance function.

### Transposition-aware distance

Spellseek uses an optimal-string-alignment-style distance calculation to account for adjacent character transpositions.

For example, the typo `recieve` can be compared with `receive`, where adjacent letters are in the wrong order.

For the usual dynamic-programming implementation, the time and space bounds remain:

- Time: `O(L × M)`
- Space: `O(L × M)`

**Important:** Transposition-aware distance is used to evaluate and rank candidate words. The BK-tree search itself uses its own distance metric, so the candidate-search radius must be broad enough to include relevant transposition candidates.

---

## 7. Suggestion Generation and Ranking

Spellseek does not return every word in the dictionary. It searches for possible candidates, evaluates their distance, and ranks the valid suggestions.

The Java implementation limits the final list to a maximum of five suggestions.

### Main steps

1. Normalize the input word.
2. Determine the maximum permitted correction distance based on word length.
3. Search the BK-tree for candidate words.
4. Calculate the transposition-aware distance for candidates.
5. Keep candidates within the permitted distance.
6. Rank candidates by edit distance, frequency, and alphabetical order.
7. Return up to five suggestions.

### Complexity

Let `C` be the number of candidates collected.

| Operation | Time Complexity |
|---|---|
| BK-tree candidate search | Depends on the number of visited nodes |
| Recalculate distance for candidates | Approximately `O(C × L²)` for similarly sized words |
| Sort candidates | `O(C log C)` |
| Return at most five suggestions | `O(1)` for the final list size, excluding work required to generate it |

The combined approximate cost for a similarly sized input and candidates can be expressed as:

`O(V × L² + C × L² + C log C)`

This is a simplified bound. It assumes that the input and candidate lengths are approximately `L` and that the implementation sorts the collected candidates.

The sorting step can be relatively inexpensive when only a small number of candidates are found. However, the number of candidates is not guaranteed to remain constant.

**Advantage:** The ranking system considers both spelling similarity and word frequency instead of choosing a correction solely by dictionary order.

---

## 8. Dictionary Loading Complexity

When Spellseek starts, it loads its built-in starter vocabulary and then attempts to load the specified dictionary file.

Let `D` be the total number of characters in the loaded dictionary.

### Time complexity

Reading and processing the dictionary requires approximately:

`O(D)`

This assumes ordinary input processing and expected constant-time dictionary insertion. Building the Trie and BK-tree also processes the words and their characters; BK-tree insertion performance depends on the shape of the tree and its distance calculations.

Therefore, the complete loading time can be higher than a simple linear file read, particularly for a large dictionary or an unfavorable BK-tree structure.

### Space complexity

The dictionary is stored in several structures, including the HashMap, Trie, and BK-tree.

The overall storage requirement is proportional to the dictionary contents:

`O(D)`

This is a simplified bound that excludes constant-factor overhead from storing the same words or related information in multiple structures.

**Advantage:** The indexes are built during loading so that subsequent lookups and autocomplete operations can use the appropriate data structure.

---

## 9. Sentence Spell Checking Complexity

When a user submits a sentence, Spellseek identifies words and checks them individually.

Let:

- `T` be the total number of characters in the sentence.
- `Q` be the number of words requiring correction suggestions.

### Time complexity

Tokenizing and processing the sentence requires approximately:

`O(T)`

Exact dictionary lookups are expected to take constant HashMap time per word, excluding normalization.

For words that need suggestions, additional BK-tree searches, distance calculations, and ranking are performed.

A simplified overall expression is:

`O(T + total cost of searches for the Q words)`

The cost depends on the length of each word, the number of BK-tree nodes visited, and the number of candidates ranked.

### Space complexity

Additional memory is required for the extracted words, proposed sentence, and suggestion results.

This depends on sentence length and the number of candidate suggestions generated. The dictionary itself remains the main long-lived memory requirement.

---

## 10. Java and Browser Interface Comparison

The Java CLI and HTML/JavaScript interface provide different ways to interact with Spellseek.

| Feature | Main Data Structure or Operation | Complexity |
|---|---|---|
| Check an exact word | HashMap lookup | Expected `O(1)` lookup, plus input processing |
| Find autocomplete matches | Trie prefix search | `O(L + P)`, plus sorting if performed |
| Generate corrections | BK-tree and edit distance | Depends on visited nodes and candidate count |
| Rank corrections | Candidate sorting | `O(C log C)` |
| Check a sentence | Tokenization and repeated word checks | `O(T)` plus correction-search costs |
| Load dictionary | File processing and index construction | Depends on input size and index-building behavior |
| Visualize a data structure | Traversal and rendering | Depends on the number of displayed nodes |

The browser interface may use different internal details or a fallback vocabulary when the dictionary file cannot be loaded. The table describes the general algorithms and should not be interpreted as a guarantee that every browser feature has exactly the same implementation as the Java version.

Browser memory usage also includes the DOM, CSS, JavaScript objects, and any cached dictionary data.

---

## 11. Overall Complexity Summary

| Component | Typical Time Complexity | Space Complexity |
|---|---|---|
| HashMap exact lookup | Expected `O(1)` after normalization | `O(N)` entries |
| Trie insertion or exact lookup | `O(L)` under constant-time child access | `O(D)` |
| Trie autocomplete | `O(L + P)`, plus optional sorting | `O(D)` stored structure, plus result memory |
| Edit distance | `O(L × M)` | `O(L × M)` with a full table |
| BK-tree approximate search | Depends on visited nodes and distance calculations | `O(D)` stored structure |
| Candidate ranking | `O(C log C)` | Depends on the candidate list and sorting implementation |
| Dictionary loading | File processing plus index construction | `O(D)` overall stored data |
| Sentence checking | `O(T)` plus per-word checking and correction costs | Depends on sentence and result sizes |

Here, `N` is the number of words, `D` is the total number of dictionary characters, `L` and `M` are string lengths, `P` is the number of Trie nodes explored, `C` is the number of candidate suggestions, and `T` is the sentence length.

---

## 12. Performance Considerations

Spellseek's performance is influenced by several factors:

1. **Dictionary size:** A larger dictionary requires more memory and more time to load.
2. **Word length:** Edit-distance calculations become more expensive as the compared strings grow.
3. **BK-tree structure:** A well-pruned search may examine fewer words, while an unfavorable structure may require visiting many nodes.
4. **Candidate count:** A query producing many possible corrections requires more distance calculations and ranking work.
5. **Autocomplete results:** Exploring a large prefix subtree can take longer than exploring a small one.
6. **Browser resources:** Rendering visualizations and maintaining interface state introduce costs beyond the core spelling algorithms.

The complexity bounds describe algorithmic growth rather than exact execution time. Actual performance should be measured using representative dictionaries, short and long words, common typos, and prefixes with different numbers of matches.

---

## 13. Conclusion

Spellseek combines complementary data structures to support efficient spelling correction.

- **HashMap** provides expected constant-time exact lookup.
- **Trie** supports character-by-character prefix searching.
- **BK-tree** narrows the candidates considered for approximate matching.
- **Edit distance** measures spelling differences.
- **Frequency-based ranking** helps order the final suggestions.

The main benefit of this design is that Spellseek does not need to compare every input word with every dictionary word for every operation. HashMap and Trie provide efficient exact and prefix operations, while BK-tree can reduce approximate-search work in practice.

However, BK-tree performance depends on its structure and query characteristics. Its worst-case behavior can approach a full scan, and edit-distance calculations still have a cost proportional to the lengths of the compared strings.

This combination provides a practical foundation for a dictionary-based spelling-correction application with both a Java command-line interface and a browser interface.
