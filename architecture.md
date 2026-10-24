# Spellseek — System Architecture

## 1. Overview

Spellseek is a dictionary-based spell-correction project that combines multiple data structures to provide spelling suggestions and autocomplete.

## 2. Architecture Components

### User Interface

The browser interface is built using HTML, CSS, and JavaScript. It accepts user input and displays spelling suggestions.

### Dictionary Module

The dictionary provides the vocabulary used to identify valid words and generate correction candidates.

### Exact Lookup

Map-based data structures support efficient checking of whether a word exists in the dictionary.

### BK-Tree Search

The BK-tree organizes words using edit distance. It helps locate candidate words that are similar to the user's input.

### Trie-Based Autocomplete

The Trie organizes words by their character prefixes and supports prefix-based suggestions.

### Suggestion Ranking

Candidate words are ranked using edit distance, word frequency, and alphabetical order.

## 3. Processing Flow

1. The user enters a word.
2. The application processes the input.
3. The dictionary is checked for an exact match.
4. If a correction is required, approximate search finds candidate words.
5. Candidate suggestions are ranked.
6. Results are displayed to the user.

Autocomplete uses the Trie to find words matching the entered prefix.

## 4. Technologies

* Java
* JavaScript
* HTML
* CSS
* BK-tree
* Trie
* HashMap

## 5. Conclusion

The architecture separates dictionary lookup, approximate searching, autocomplete, and presentation into distinct responsibilities, making the project easier to understand and maintain.
