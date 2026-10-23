# Spellseek — Intelligent Spell Correction Engine

Spellseek is a dictionary-based spell correction system that identifies misspelled words and suggests possible corrections. It uses data structures and string-matching algorithms to make word searching more efficient.

## Features

- Single-word spell checking
- Sentence-level spelling correction
- Suggestions ranked by edit distance and word frequency
- Prefix-based autocomplete
- BK-Tree visualization
- Trie visualization
- Custom word addition
- Java command-line implementation
- Browser-based interface

## Technologies Used

- Java
- HTML
- CSS
- JavaScript
- Data Structures and Algorithms

## Data Structures

### BK-Tree
The Burkhard-Keller Tree supports approximate word searching using edit distance. Its distance-based structure can help eliminate irrelevant branches during searches.

### Trie
The Trie supports prefix-based searching and autocomplete.

### HashMap and Map
These structures support efficient word lookup and frequency retrieval.

### Edit Distance
Edit distance measures the number of character operations required to transform one word into another.

## Project Structure

```text
Spellseek_Project/
├── Spellseek.java
├── README.txt
├── Spellseek_Java_Explanation.md
├── dictionary.txt
├── index.html
├── css/
│   └── style.css
├── js/
│   ├── app.js
│   ├── bktree.js
│   ├── spellseek.js
│   └── trie.js
├── run-java.bat
└── test_output.txt
```

## Running the Java Version

1. Open the project folder in a terminal.
2. Compile the program:

   ```bash
   javac Spellseek.java
   ```

3. Run the application:

   ```bash
   java Spellseek
   ```

Keep `dictionary.txt` in the project folder alongside `Spellseek.java`.

## Running the Browser Version

1. Open the project folder in Visual Studio Code.
2. Start a local server using the Live Server extension.
3. Open `index.html` through Live Server.
4. Wait for the dictionary to load.
5. Enter a word or sentence to test the spell correction features.

## Suggestion Ranking

Suggestions are ranked using:

1. Lowest edit distance.
2. Highest word frequency when distances are equal.
3. Alphabetical order as a tie-breaker.

## Limitations

- Search performance depends on the structure of the BK-Tree.
- Similar spelling does not always indicate the intended word.
- Suggestions depend on the available dictionary.
- Context-sensitive corrections cover selected patterns and do not provide complete grammar checking.

## Purpose

Spellseek demonstrates how data structures and algorithms can be applied to a practical spelling correction problem.
