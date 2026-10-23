
# Spellseek User Guide

## 1. Introduction

Spellseek is an intelligent spelling-correction application with two versions:

- **Browser version:** An interactive interface built using HTML, CSS, and JavaScript.
- **Java version:** A command-line application built using Java 8 or newer.

Spellseek helps users check words, find possible spelling mistakes, view ranked suggestions, autocomplete word prefixes, and explore the data structures used by the spelling engine.

The project uses a dictionary, a Trie, a BK-tree, edit-distance calculations, and word-frequency ranking.

## 2. Project Requirements

### Browser version

- A modern web browser, such as Chrome, Edge, or Firefox.
- The Spellseek project files.
- A local development server, recommended for loading the complete dictionary.
- An editor such as Visual Studio Code, if you want to modify the project.

### Java version

- Java Development Kit (JDK) 8 or newer.
- The `Spellseek.java` source file.
- The `dictionary.txt` file for the full dictionary, if available.

The Java version does not require external libraries.

## 3. Project Structure

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
    ├── ARCHITECTURE.md
    ├── ALGORITHMS.md
    └── USER_GUIDE.md
```

Other repository files, such as `README.txt`, `LICENSE`, `.gitignore`, `CONTRIBUTING.md`, and `CHANGELOG.md`, may also be present.

### Important files

| File | Purpose |
|---|---|
| `index.html` | Main browser interface. |
| `css/style.css` | Controls the website's appearance. |
| `js/trie.js` | Trie data structure and related operations. |
| `js/bktree.js` | BK-tree and edit-distance functionality. |
| `js/spellseek.js` | Browser-side spelling-correction engine. |
| `js/app.js` | Connects the interface to the JavaScript engine. |
| `dictionary.txt` | Dictionary words and optional frequency values. |
| `Spellseek.java` | Java command-line application. |
| `run-java.bat` | Windows launch script. |
| `test_output.txt` | Example output from the Java version. |

## 4. Running the Browser Version

The browser version is the easiest way to interact with Spellseek visually.

### Step 1: Open the project

1. Extract the Spellseek project ZIP file, if necessary.
2. Open Visual Studio Code.
3. Select **File → Open Folder**.
4. Choose the main Spellseek project folder.

Make sure `index.html`, `dictionary.txt`, `css`, and `js` are in the expected locations.

### Step 2: Start a local server

The recommended method is to use the **Live Server** extension in Visual Studio Code.

1. Install the Live Server extension if it is not already installed.
2. Open `index.html` in Visual Studio Code.
3. Right-click the file.
4. Select **Open with Live Server**.

Your browser should open the Spellseek interface.

**Important:** Avoid opening `index.html` directly by double-clicking it if you want the full dictionary. Browsers may block local dictionary loading through a `file://` URL. If that happens, Spellseek uses a smaller fallback vocabulary and displays a notice.

### Step 3: Wait for the dictionary to load

The application loads `dictionary.txt` and builds its indexes.

If the dictionary cannot be fetched, check that:

- `dictionary.txt` is in the project root folder.
- The local server is running from the correct folder.
- The browser is showing the expected local project URL.
- The browser console does not report a missing file or JavaScript error.

## 5. Using the Browser Interface

The browser interface contains three main tabs:

1. Spell Checker
2. Data Structure Visualizer
3. Dictionary Workbench

### 5.1 Spell Checker

The Spell Checker tab lets you enter a word and view spelling suggestions.

**How to use it:**

1. Locate the input field labelled **Type a word to spell-check / autocomplete**.
2. Enter a word, such as `progrmming`.
3. Observe the Ranked Suggestions panel.
4. Review the proposed corrections.

The suggestions are ranked using edit distance and word-frequency information.

If the entered word is recognized by the dictionary, the application handles it as a known word. If it is not recognized, the engine searches for possible alternatives.

### 5.2 Maximum Edit Distance

The **Max Edit Distance Threshold (BK-Tree)** setting controls how far candidate words may differ from the entered word.

The browser interface accepts values from **1 through 5**.

- A smaller threshold usually returns closer spelling matches.
- A larger threshold can find more alternatives.
- A larger threshold may also increase search work and produce less relevant suggestions.

For ordinary spelling mistakes, start with the default value of `2`. Increase it if you need to explore more distant candidates.

### 5.3 Sentence Spell Checker

The Sentence Spell Checker checks a full sentence and proposes possible spelling corrections.

**How to use it:**

1. Locate the **Your sentence** text area.
2. Enter a sentence, for example:

   `I recieved teh progrmming assignment.`

3. Click **Check Sentence**.
4. Review the proposed correction and any identified word replacements.

The result depends on the dictionary and suggestion ranking. Always review the proposed sentence before using it.

Spellseek focuses on spelling assistance. It does not guarantee complete grammar correction or understand every word's meaning in context.

### 5.4 Prefix Search

Prefix Search finds dictionary words that begin with the characters you enter.

**How to use it:**

1. Locate the **Word prefix** field.
2. Enter a prefix, such as `prog`.
3. Click **Find Prefix**.
4. Review the matching dictionary words.

For example, a prefix such as `prog` may match words including `program`, `programming`, and `progress`, depending on the dictionary.

Prefix autocomplete is different from spelling correction: it finds words that start with the specified characters rather than words that are merely similar.

## 6. Data Structure Visualizer

The Data Structure Visualizer tab provides areas for exploring the structures used by Spellseek.

### 6.1 Trie Path Lookup

A Trie stores words character by character.

Words with common prefixes share nodes. For example, `car`, `care`, and `card` share the initial path for `c` and `a`.

A Trie supports:

- Exact word lookup.
- Prefix autocomplete.
- Exploring paths through a word's characters.

A complete word must end at a node marked as a word. A character path alone does not guarantee that the path represents a complete dictionary entry.

### 6.2 BK-tree Metric Bounds

A BK-tree helps find words that are close to an input word according to edit distance.

During search, the algorithm compares the query with stored words and uses distance-based branch bounds to avoid exploring some branches.

The visualizer helps explain how approximate search uses distances to narrow the search.

The exact search result depends on the dictionary, tree structure, and edit-distance threshold.

## 7. Dictionary Workbench

Dictionary Workbench lets you add custom words and frequency values to the browser application's dictionary.

### How to add a word

1. Open the **Dictionary Workbench** tab.
2. Enter a word in the **Word** field.
3. Enter a positive frequency value in the **Frequency Weight** field.
4. Click **Insert Word**.
5. Read the status message to check whether the operation was accepted.

Frequency values are used as ranking information. They do not measure spelling correctness directly.

### Where are custom words saved?

The browser version stores custom words in that browser's local storage.

This means:

- Custom words can remain available after reloading the page in the same browser profile.
- They are not automatically written back to `dictionary.txt`.
- They are not automatically transferred to another browser or computer.
- They are not automatically shared with the Java command-line application.

Keep a separate backup of any custom vocabulary you need to preserve.

## 8. Running the Java Version

The Java version provides a text-based menu for checking words, correcting sentences, managing dictionary entries, and viewing statistics.

### Option A: Use the Windows launch script

If `run-java.bat` is included:

1. Ensure Java is installed.
2. Open the project folder.
3. Double-click `run-java.bat`.

If Windows reports that Java is not recognized, install a JDK and ensure Java is available in the system PATH.

### Option B: Run through a terminal

Open a terminal in the folder containing `Spellseek.java` and enter:

```bash
javac Spellseek.java
java Spellseek
```

The first command compiles the Java source. The second starts the application.

The commands assume the source file is named `Spellseek.java` and that a compatible JDK is installed.

### Load a dictionary from another path

You can supply a dictionary path as a command-line argument:

```bash
java Spellseek /path/to/your-dictionary.txt
```

Replace `/path/to/your-dictionary.txt` with the actual path to your dictionary file.

On Windows, a path may look like:

```bash
java Spellseek "C:\Spellseek\data\dictionary.txt"
```

The specified path must point to an existing readable dictionary file.

## 9. Java Menu Options

When the Java application starts, it displays a menu.

| Option | Function |
|---|---|
| `1` | Check a word and list ranked suggestions. |
| `2` | Check a sentence and propose corrections. |
| `3` | Add a word to the dictionary. |
| `4` | Autocomplete a prefix. |
| `5` | Display dictionary and index statistics. |
| `0` | Exit the application. |

### Option 1: Check a word

1. Enter `1`.
2. Enter the word when prompted.
3. Review the result.

If the word is not in the dictionary, Spellseek lists possible alternatives with information such as edit distance and frequency.

The Java implementation displays up to five spelling suggestions.

### Option 2: Check a sentence

1. Enter `2`.
2. Type a sentence.
3. Review the proposed sentence.
4. Check any word replacements identified for review.

A suggested replacement is not guaranteed to be correct in context.

### Option 3: Add a word

1. Enter `3`.
2. Follow the prompts to enter the word and any requested frequency information.
3. Review the confirmation message.

The Java version is designed to save added words to `dictionary.txt` for future runs. Check that the file is writable and keep a backup before making large dictionary changes.

### Option 4: Autocomplete a prefix

1. Enter `4`.
2. Enter the beginning of a word, such as `pro`.
3. Review the matching words.

Autocomplete results are ordered using frequency information, with alphabetical ordering used to break frequency ties.

### Option 5: Show dictionary statistics

Enter `5` to view dictionary and index information.

This can help you confirm that dictionary entries were loaded and that the application has built its indexes.

### Option 0: Exit

Enter `0` to close the Java application.

## 10. Understanding Spelling Suggestions

Spellseek uses multiple algorithms to generate and rank suggestions.

### Edit distance

Edit distance measures the number of character operations needed to change one word into another. Standard Levenshtein distance considers insertions, deletions, and substitutions.

The BK-tree uses Levenshtein distance to search for candidate words. The Java implementation also considers adjacent-letter transpositions during suggestion ranking.

For example, a common transposition mistake is:

```text
recieve -> receive
```

The engine may suggest the intended word when it exists in the dictionary and falls within the candidate search behavior.

### Ranking

The documented suggestion-ranking priority is:

1. Smaller edit distance first.
2. Higher frequency when distances tie.
3. Alphabetical order when the earlier criteria tie.

A candidate with a small edit distance is generally more relevant, but the top suggestion is not guaranteed to be correct.

### Dictionary limitations

The dictionary determines which words can be recognized and suggested. Names, new terms, technical vocabulary, and uncommon spellings may be missing.

If a valid word is repeatedly flagged, consider adding it to the appropriate dictionary using the relevant application's supported workflow.

## 11. Dictionary File Format

The dictionary file is named `dictionary.txt`.

Each valid line contains a word and may include a positive integer frequency.

Example:

```text
hello 200
spelling 100
world 150
programming 120
```

Rules:

- The first field contains the word.
- The optional second field contains its frequency.
- Blank lines are ignored.
- Lines beginning with `#` are ignored.
- If a usable frequency is missing, the Java implementation uses a default frequency of `1`.

When editing the dictionary manually, preserve the existing format and encoding. Back up the file before making substantial changes.

## 12. Troubleshooting

### The browser displays only a small dictionary

**Possible cause:** The browser could not fetch `dictionary.txt`.

**Try this:**

1. Start the page using Live Server.
2. Confirm `dictionary.txt` is in the project root.
3. Reload the browser page.
4. Check for a missing-file error in the browser developer tools.

### The browser page opens, but the buttons do not work

**Possible cause:** One or more JavaScript files failed to load or reported an error.

**Try this:**

1. Confirm the `js` folder contains `trie.js`, `bktree.js`, `spellseek.js`, and `app.js`.
2. Check that the file paths and capitalization match the script references in `index.html`.
3. Open the browser developer console.
4. Resolve any reported missing files or JavaScript errors.

### Java is not recognized

**Possible cause:** The JDK is not installed or Java is not available in the system PATH.

**Try this:**

1. Install a JDK version 8 or newer.
2. Open a new terminal.
3. Run:

```bash
java -version
javac -version
```

Both commands should display version information.

### Java cannot find `Spellseek.java`

**Possible cause:** The terminal is in the wrong folder or the file has a different name.

**Try this:**

1. Open the folder containing the Java source file.
2. Confirm the file is named `Spellseek.java`.
3. Run `javac Spellseek.java` again.

### Java cannot find the dictionary

**Possible cause:** The program is running from a different working directory or the specified path is incorrect.

**Try this:**

1. Confirm `dictionary.txt` is in the current working directory.
2. Alternatively, supply the correct path as a command-line argument.
3. Check that the file can be read.

The Java application includes starter words and can continue when the dictionary file is unavailable.

### A valid word is marked as incorrect

**Possible cause:** The word may not exist in the loaded dictionary or may use an unsupported form.

**Try this:**

1. Check the spelling.
2. Confirm the expected dictionary was loaded.
3. Add the word using the application's supported custom-word workflow.

### The top suggestion is wrong

**Possible cause:** The intended word may not be present, the candidate distances may be similar, or the frequency values may favor another word.

**Try this:**

1. Review the other suggestions.
2. Check the edit-distance threshold in the browser.
3. Confirm the dictionary contains the intended word.
4. Add missing vocabulary where appropriate.

### A browser custom word is missing from Java

**Expected behavior:** Browser custom words are stored in local storage and are not automatically synchronized with the Java dictionary.

Add the word separately to the Java version if you need it there.

## 13. Privacy and Offline Use

Spellseek's browser-side spelling processing is designed to run locally in the browser, without an external spelling-correction API.

The browser may need a local server to load the dictionary, but that server can run on your own computer. The Java version runs locally as well.

Custom words stored by the browser remain in that browser's local storage unless the user or browser environment clears them. They are not automatically transferred to the Java application.

## 14. Recommended Usage

For best results:

- Load the full dictionary when possible.
- Start with the default edit-distance threshold.
- Review suggestions before accepting corrections.
- Add missing technical words or names when appropriate.
- Keep a backup before modifying `dictionary.txt`.
- Test the browser and Java versions separately.
- Remember that spelling suggestions are assistance, not a guarantee of grammatical or contextual correctness.

## 15. Quick Reference

| Task | Browser version | Java version |
|---|---|---|
| Check a word | Enter it in the main input field. | Choose menu option `1`. |
| Check a sentence | Enter it and click **Check Sentence**. | Choose menu option `2`. |
| Find prefix matches | Use Prefix Search and click **Find Prefix**. | Choose menu option `4`. |
| Explore data structures | Open Data Structure Visualizer. | View statistics with option `5`. |
| Add a custom word | Use Dictionary Workbench. | Choose menu option `3`. |
| View dictionary statistics | Use the available browser interface features. | Choose menu option `5`. |
| Exit | Close the browser tab. | Choose menu option `0`. |

---

*Spellseek is a spelling-assistance tool. Always review suggested corrections, especially in formal, technical, or context-sensitive writing.*
