SPELLSEEK - INTELLIGENT SPELL CORRECTION ENGINE
================================================

This folder contains two versions of Spellseek:
1. A browser UI (HTML, CSS, JavaScript) with Trie + BK-tree spelling suggestions.
2. A Java command-line program (Spellseek.java).

PROJECT STRUCTURE
-----------------
index.html          Browser UI entry point
css/style.css       Stylesheet linked by index.html
js/trie.js          Exact lookup, prefix autocomplete, Trie visualization
js/bktree.js        BK-tree and Levenshtein edit distance
js/spellseek.js     Browser spell-checking engine
js/app.js           UI event handlers and dictionary loading

dictionary.txt      Word list and optional frequency values
Spellseek.java      Java command-line application
run-java.bat        Compile and start the Java version on Windows
test_output.txt     Example output from the Java command-line application

RUN THE BROWSER VERSION IN VS CODE
----------------------------------
1. Open the extracted Spellseek_Project folder in Visual Studio Code.
2. Install the VS Code extension named "Live Server" if it is not already installed.
3. Right-click index.html and select "Open with Live Server".
4. The page should load dictionary.txt, then build the Trie and BK-tree indexes.
5. Try a misspelling such as "progrmming", type a prefix such as "pro", or add a word in Dictionary Workbench.

Important: Do not open index.html by double-clicking it if you want the full dictionary.
Modern browsers block fetch() of local files in file:// pages. Live Server serves the folder locally;
all word processing still happens in your browser and no external API is used. If opened directly,
the page falls back to a small starter vocabulary and displays a notice.

BROWSER DICTIONARY NOTES
-----------------------
- The dictionary is read from dictionary.txt on startup.
- Words added through Dictionary Workbench are stored in that browser's local storage, not written
  back into dictionary.txt (browser security prevents a page from silently editing local files).
- The maximum edit-distance control accepts values from 1 through 5.
- Search suggestions prioritize lower edit distance, then higher frequency, then alphabetical order.

RUN THE JAVA COMMAND-LINE VERSION
---------------------------------
Requires Java 8 or newer. On Windows, double-click run-java.bat, or open a VS Code terminal in this folder and run:

    javac Spellseek.java
    java Spellseek

To load a dictionary at a different path:

    java Spellseek /path/to/your-dictionary.txt

DICTIONARY FORMAT
-----------------
One word per line, optionally followed by a positive integer frequency:

    hello 200
    spelling 100
    world 150

Lines beginning with # and blank lines are ignored. The Java version includes starter words and can
run without dictionary.txt. The browser version also has a small fallback vocabulary if dictionary.txt
cannot be fetched.

FEATURES
--------
Browser version:
- Spell-checking with ranked suggestions
- Prefix autocomplete
- Trie path and BK-tree search visualizers
- Add custom words and frequencies (stored in browser local storage)

Java command-line version:
- Option 1: Check a single word and list ranked spelling suggestions
- Option 2: Check a sentence and print a proposed corrected sentence
- Option 3: Add a custom word and save it to dictionary.txt for future runs
- Option 4: Autocomplete a prefix and show matching words by frequency
- Option 5: Display dictionary and index statistics
- Option 0: Exit the program
- Adjacent letter transpositions count as one edit (for example, recieve -> receive)

CORE DATA STRUCTURES
--------------------
HashMap / JavaScript Map: stores dictionary words and frequencies.
Trie: exact lookup and prefix autocomplete.
BK-tree: approximate lookup, pruning branches using Levenshtein distance.
The BK-tree uses Levenshtein distance for candidate lookup. Suggestions are ranked with an adjacent-transposition-aware edit distance, so common swapped-letter typos can be corrected.

LIMITATIONS
-----------
Suggestion quality depends on the dictionary and its frequencies. Proper nouns and specialist terms
may need to be added. Sentence correction should be reviewed by the user before accepting it.
