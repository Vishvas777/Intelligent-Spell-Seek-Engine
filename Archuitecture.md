
# Spellseek Architecture

## 1. Overview

Spellseek is an intelligent spelling-correction project with two implementations:

1. **Browser application:** An HTML, CSS, and JavaScript interface for checking words and sentences, viewing spelling suggestions, exploring data structures, searching prefixes, and managing custom words.
2. **Java command-line application:** A standalone Java program that provides spelling correction, sentence checking, prefix autocomplete, dictionary management, and dictionary statistics.

Both implementations use dictionary data and data structures to support word lookup and spelling suggestions. They are separate applications and do not share runtime state automatically.

## 2. Project Structure

The main project files are organized as follows:

```text
Spellseek/
├── index.html
├── dictionary.txt
├── Spellseek.java
├── run-java.bat
├── test_output.txt
├── css/
│   └── style.css
├── js/
│   ├── trie.js
│   ├── bktree.js
│   ├── spellseek.js
│   └── app.js
└── docs/
    └── ARCHITECTURE.md
```

Repository documentation and supporting files, such as `README.txt`, `LICENSE`, `CONTRIBUTING.md`, `.gitignore`, and `CHANGELOG.md`, may also be present.

### File responsibilitie

| File | Responsibility |
|---|---|
| `index.html` | Defines the browser interface and loads the JavaScript modules. |
| `css/style.css` | Defines the visual appearance and layout. |
| `js/trie.js` | Provides Trie-based exact lookup, prefix autocomplete, and Trie visualization support. |
| `js/bktree.js` | Provides BK-tree search and Levenshtein edit-distance functionality. |
| `js/spellseek.js` | Implements the browser-side spelling-correction engine. |
| `js/app.js` | Handles browser events, dictionary loading, and interactions with the interface. |
| `dictionary.txt` | Supplies dictionary words and optional frequency values. |
| `Spellseek.java` | Implements the independent Java command-line application. |
| `run-java.bat` | Provides a Windows launch option for the Java version. |
| `test_output.txt` | Contains example output from the Java application. |

## 3. High-Level Architecture

Spellseek has two separate execution paths.

```text
                    SPELLSEEK
                        |
             +----------+----------+
             |                     |
      Browser Application    Java Application
             |                     |
       index.html             Spellseek.java
             |                     |
       css/style.css          Dictionary loading
             |                     |
       js/app.js              HashMap + Trie
             |                     |
       js/spellseek.js          BK-tree
             |                     |
       +-----+------+        Suggestions and
       |            |        command-line menu
      Trie        BK-tree
       |            |
       +-----+------+
             |
     Dictionary words
     and frequencies
```

The diagram represents the overall responsibilities described by the project files. The browser and Java versions implement their own data structures and processing; they are not connected through an API or a shared database.

## 4. Dictionary Data

The dictionary is the source of words used for spelling validation and suggestion generation.

### Dictionary format

Each non-comment line contains a word and may contain a numeric frequency:

```text
hello 200
spelling 100
world 150
programming 120
```

- The first field is the word.
- The optional second field represents its frequency.
- Blank lines and lines beginning with `#` are ignored.
- If a usable frequency is not supplied, the Java implementation defaults to a frequency of `1`.

Frequency values help rank suggestions and autocomplete results. They do not, by themselves, determine whether a word is spelled correctly.

### Dictionary loading

**Browser version**

The browser application loads `dictionary.txt` when it starts and builds its indexes. If the file cannot be fetched, the application uses a small fallback vocabulary and displays a notice.

The project should be opened through a local development server, such as VS Code Live Server, to allow browser dictionary loading. Directly opening `index.html` with a `file://` URL may prevent the dictionary from loading.

**Java version**

The Java application starts with built-in starter words and then attempts to load the dictionary file. By default, it looks for `dictionary.txt` in the current working directory. An alternative dictionary path can be supplied as a command-line argument.

The Java implementation can therefore start with its built-in vocabulary when the external dictionary is unavailable.

## 5. Core Data Structures

### 5.1 HashMap

The Java implementation maintains a `HashMap` containing dictionary entries and their frequencies.

Its primary responsibilities are:

- Keeping track of known words.
- Retrieving word entries efficiently by normalized word.
- Storing frequency information used during suggestion ranking.
- Supporting dictionary statistics and duplicate handling.

The browser implementation uses JavaScript data structures for its own dictionary and frequency information.

### 5.2 Trie

A Trie stores words as paths of characters from a shared root.

For example, the words `car`, `care`, and `card` share the initial path for `c` and `a`.

The Trie supports:

- Exact word lookup.
- Prefix autocomplete.
- Exploring the character path for a word.
- Visualizing Trie lookup in the browser interface.

**Why it is useful:** Words with common prefixes share nodes, making a Trie suitable for prefix-based search.

### 5.3 BK-tree

A Burkhard–Keller tree, or BK-tree, supports approximate matching using a distance function between words.

Each node represents a dictionary word. Child branches are associated with distances from that node, allowing the search to avoid exploring some branches that cannot satisfy the requested distance threshold.

The BK-tree is used to find candidate words that are similar to an input word.

The browser version also provides a BK-tree visualization to help users explore the search process.

### 5.4 Edit Distance

Edit distance estimates how different two words are by counting character-edit operations.

Typical operations include:

- Insertion: adding a character.
- Deletion: removing a character.
- Substitution: replacing one character with another.

Spellseek uses Levenshtein distance for BK-tree candidate lookup. The project also accounts for adjacent-letter transpositions when ranking spelling suggestions, so common mistakes such as `recieve` can be matched to `receive`.

The search distance threshold controls how far from the input word a candidate may be. A larger threshold can find more candidates but may also increase search work and return less relevant matches.

## 6. Spelling-Correction Workflow

The general spelling-correction process is:

1. **Receive input:** The user enters a word or sentence.
2. **Normalize the word:** The implementation trims and normalizes the word for lookup.
3. **Check exact spelling:** The Trie or dictionary index checks whether the word exists.
4. **Search for candidates:** If the word is not found, the BK-tree searches for nearby dictionary words within the applicable edit-distance threshold.
5. **Calculate ranking information:** Candidate words are evaluated using edit distance and their frequency values.
6. **Rank suggestions:** Candidates with smaller edit distances are preferred. Higher-frequency words are preferred when distances tie, followed by alphabetical order.
7. **Display results:** The application shows the suggestions to the user.

The precise internal sequence and implementation details can differ between the Java and browser versions.

### Suggestion ranking

The intended ranking priority is:

1. Lower edit distance.
2. Higher dictionary frequency.
3. Alphabetical order as a tie-breaker.

This ranking improves the usefulness of suggestions but cannot guarantee that the first suggestion is always correct. Dictionary coverage and frequency quality affect the result.

## 7. Browser Application Architecture

### 7.1 Presentation layer

`index.html` defines the application's visible sections and controls. These include:

- Spell Checker.
- Sentence Spell Checker.
- Prefix Search.
- Trie Path Lookup.
- BK-tree Metric Bounds.
- Dictionary Workbench.

`css/style.css` controls presentation, layout, and visual styling.

### 7.2 Application controller

`js/app.js` connects the interface to the browser-side spelling engine. It handles user interactions and dictionary loading, then updates the relevant interface elements with results or status messages.

### 7.3 Spelling engine

`js/spellseek.js` contains the browser-side spelling-correction functionality. It works with the Trie, BK-tree, and dictionary data to validate words and produce suggestions.

### 7.4 Data-structure modules

`js/trie.js` and `js/bktree.js` separate the main data structures and related operations from the interface code.

The HTML file loads these scripts in dependency order:

```html
<script src="js/trie.js"></script>
<script src="js/bktree.js"></script>
<script src="js/spellseek.js"></script>
<script src="js/app.js"></script>
```

Keep this order consistent with the modules' dependencies unless the code is deliberately changed to use another loading strategy.

### 7.5 Custom dictionary words

Words added through Dictionary Workbench are stored in the browser's local storage. They are not automatically written back to `dictionary.txt`.

This separation follows browser security restrictions: a normal web page cannot silently modify a local file on the user's computer.

Custom words are local to the browser storage used by the application and are not automatically synchronized with the Java application.

## 8. Java Command-Line Architecture

`Spellseek.java` is a standalone application requiring Java 8 or newer and no external libraries.

Its main responsibilities include:

- Loading built-in starter words.
- Loading dictionary entries from a file when available.
- Maintaining dictionary entries and frequencies.
- Building and using a Trie and BK-tree.
- Checking individual words.
- Suggesting corrections for sentences.
- Adding words to the dictionary.
- Providing prefix autocomplete.
- Displaying dictionary and index statistics.

The command-line menu provides these operations:

| Option | Operation |
|---|---|
| `1` | Check a single word and show suggestions. |
| `2` | Check a sentence and propose corrections. |
| `3` | Add a word to the dictionary. |
| `4` | Autocomplete a prefix. |
| `5` | Display dictionary statistics. |
| `0` | Exit the program. |

The Java version uses its own dictionary and indexes. Changes made in the browser's local storage do not automatically appear in the Java application.

## 9. Sentence Correction

The sentence-checking feature identifies words that may not exist in the dictionary and proposes possible replacements.

The application can display a proposed corrected sentence and identify uncertain word replacements for review.

Sentence correction is not the same as full grammar checking or semantic understanding. A valid word can still be incorrect in context, and proper nouns or specialist terms may not exist in the dictionary.

Users should review proposed sentence corrections before accepting them.

## 10. Data Flow

### Browser flow

```text
Open index.html through a local server
                  |
                  v
        Load dictionary.txt
                  |
                  v
       Build dictionary indexes
                  |
                  v
         User enters a word
                  |
                  v
        Exact lookup in Trie
                  |
           +------+------+
           |             |
          Found       Not found
           |             |
     Show correct     BK-tree search
        spelling          |
                          v
                  Rank candidates
                          |
                          v
                 Display suggestions
```

### Java flow

```text
Start Spellseek.java
          |
          v
Load starter vocabulary
          |
          v
Load dictionary file if available
          |
          v
Build/update dictionary indexes
          |
          v
Display command-line menu
          |
          v
Process selected operation
          |
          v
Display result or update dictionary
```

These flows describe the overall behavior. Individual methods may combine or reorder steps internally.

## 11. Complexity Considerations

Let:

- \(N\) be the number of dictionary words.
- \(L\) be the length of a word.
- \(D\) be the maximum permitted edit distance.

General considerations:

- **HashMap lookup:** Usually close to constant time on average for a word-key lookup, excluding string processing and hash computation.
- **Trie lookup:** Typically proportional to the number of characters examined, approximately \(O(L)\) for a word when the character traversal is implemented conventionally.
- **Trie prefix search:** The prefix traversal is approximately \(O(L)\), followed by the cost of collecting matching words.
- **BK-tree search:** Performance depends on the dictionary, word lengths, distance threshold, and tree structure. Some searches prune many branches; others may examine many nodes.
- **Edit-distance calculation:** Standard dynamic-programming Levenshtein distance for two words of lengths \(L_1\) and \(L_2\) takes \(O(L_1L_2)\) time in its usual full-table form.

These are general algorithmic estimates, not benchmark results for a particular Spellseek run. Actual performance depends on the implementation and input data.

## 12. Limitations

Spellseek's results depend on dictionary coverage, word frequencies, normalization rules, and the edit-distance threshold.

Potential limitations include:

- Proper nouns and specialist vocabulary may be missing.
- Similar words can have different meanings.
- A frequent candidate is not necessarily correct in the sentence.
- Increasing the search threshold may produce more irrelevant suggestions.
- Browser custom words and Java dictionary updates are stored separately.
- The browser's local dictionary changes are not automatically exported to the dictionary file.
- Sentence correction requires human review.

## 13. Testing and Maintenance

When modifying Spellseek, test the affected implementation and verify that existing features continue to work.

### Java checks

- Compile `Spellseek.java`.
- Check a correctly spelled word.
- Check a misspelled word and review its suggestions.
- Test sentence correction.
- Test prefix autocomplete.
- Add a custom word and verify the intended persistence behavior.
- Check dictionary statistics.

### Browser checks

- Open the page through a local development server.
- Confirm that the dictionary loads.
- Test exact word lookup and spelling suggestions.
- Test sentence correction and prefix autocomplete.
- Test the Trie and BK-tree visualizers.
- Add a custom word and check its browser-local persistence.
- Check the browser console for errors.

### When changing shared behavior

If the suggestion ranking, word normalization, dictionary format, or distance threshold changes, test both implementations where applicable. Similar features do not necessarily mean the underlying code is shared.

## 14. Future Improvements

Possible future improvements include:

- Automated tests for spelling and prefix lookup.
- More consistent behavior between Java and browser versions.
- Improved dictionary import and export.
- Better handling of names and specialist terms.
- Performance measurements on large dictionaries.
- Improved accessibility and interface feedback.
- More detailed visual explanations of Trie and BK-tree searches.

These are possible future enhancements, not claims that the features have already been implemented.

---

*This document describes the current high-level architecture of Spellseek. Update it whenever changes to the code, dictionary format, module responsibilities, or runtime behavior make any section inaccurate.*
