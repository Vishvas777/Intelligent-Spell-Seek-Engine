# Spellseek — Algorithm Explanation

## 1. Overview

Spellseek is a dictionary-based spell-correction engine. It checks words against a vocabulary and suggests possible corrections for misspelled words.

The project uses Java for the command-line version and HTML, CSS, and JavaScript for the browser version.

## 2. Main Data Structures

## BK-Tree
The BK-tree helps find words that are similar to a misspelled input. It uses edit distance to organize words and avoid comparing every dictionary word unnecessarily.

### Trie
The Trie stores words by their character prefixes. It supports prefix-based autocomplete.

### HashMap / Map
These structures support quick word lookups and access to word-frequency information.

## 3. Edit Distance

Edit distance measures how many character changes are needed to transform one word into another.

Common operations include:
- Insertion: adding a character
- Deletion: removing a character
- Substitution: replacing a character

For example, transforming `cat` into `cut` requires one substitution, so their edit distance is 1.

## 4. Suggestion Ranking

Spellseek ranks candidate corrections using these rules:

1. Prefer candidates with a lower edit distance.
2. When distances are equal, prefer words with a higher frequency.
3. If both distance and frequency are equal, use alphabetical order.

## 5. Processing Workflow

1. Load the dictionary.
2. Normalize and validate the input.
3. Check whether the word exists.
4. If necessary, search for approximate matches using the BK-tree.
5. Rank the candidate suggestions.
6. Display the best suggestions to the user.

The Trie separately supports autocomplete when the user enters a word prefix.

## 6. Limitations

- Suggestions depend on the words available in the dictionary.
- An unfamiliar but correctly spelled word may be flagged.
- Context-based corrections cover only a limited set of common word confusions.
- Approximate matching can suggest an unintended word when several candidates are similarly close.

## 7. Conclusion

Spellseek combines a BK-tree, a Trie, edit-distance calculations, and word-frequency ranking to provide spelling suggestions and prefix autocomplete.
