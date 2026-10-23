
# Spellseek Test Plan

## 1. Purpose

This document describes the testing plan for Spellseek, an intelligent spelling-correction project with two implementations:

- A Java command-line application.
- A browser interface built with HTML, CSS, and JavaScript.

The purpose of testing is to verify that the application loads dictionary data, checks words, generates spelling suggestions, supports prefix autocomplete, handles custom words, and presents results through the appropriate interface.

Testing also checks the integration of the main data structures: HashMap/JavaScript Map, Trie, and BK-tree.

**Testing status:** This document defines test cases and expected behavior. A test must be executed before it can be marked as passed.

## 2. Scope of Testing

### 2.1 Java command-line application

The Java tests cover:

- Application startup and dictionary loading.
- Exact word lookup and spelling suggestions.
- Sentence spelling correction.
- Adding custom words.
- Prefix autocomplete.
- Dictionary and index statistics.
- Dictionary file handling.
- Menu navigation and application exit.

### 2.2 Browser application

The browser tests cover:

- Loading the main interface.
- Loading the dictionary through a local server.
- Word spelling suggestions.
- Edit-distance threshold settings.
- Sentence checking.
- Prefix search.
- Trie Path Lookup and BK-Tree Metric Bounds visualizer areas.
- Adding custom words through Dictionary Workbench.
- Browser local-storage behavior.
- Error handling when dictionary loading fails.

### 2.3 Out of scope

The following are not guaranteed by the current project and are not treated as required features:

- Full grammar correction.
- Semantic understanding of every sentence.
- Automatic synchronization between the browser and Java dictionaries.
- Automatic writing of browser-added words to `dictionary.txt`.
- Guaranteed correction of every misspelling.
- Automated unit-test execution through a dedicated testing framework, unless one is added separately.

## 3. Test Environment

### 3.1 Hardware and software

| Item | Requirement |
|---|---|
| Operating system | Windows, Linux, or macOS where supported by the installed tools |
| Java | JDK 8 or newer |
| Browser | A modern browser such as Chrome, Edge, or Firefox |
| Editor | Visual Studio Code or another code editor |
| Browser server | Live Server or another local HTTP server |
| Project files | Java source, HTML, CSS, JavaScript modules, and dictionary file |

### 3.2 Project files to verify

Before testing, confirm that the relevant files exist.

```text
Spellseek/
├── index.html
├── dictionary.txt
├── Spellseek.java
├── run-java.bat
├── css/
│   └── style.css
├── js/
│   ├── trie.js
│   ├── bktree.js
│   ├── spellseek.js
│   └── app.js
└── docs/
    └── TEST_PLAN.md
```

Additional documentation and repository files may also be present.

### 3.3 Starting the Java application

Open a terminal in the project directory and run:

```bash
javac Spellseek.java
java Spellseek
```

To load a dictionary from a different path, run:

```bash
java Spellseek /path/to/your-dictionary.txt
```

Replace the example path with the actual dictionary path.

### 3.4 Starting the browser application

1. Open the project folder in Visual Studio Code.
2. Start Live Server from `index.html`.
3. Open the page in the browser.
4. Confirm that the interface appears.
5. Check that the full dictionary loads and that no missing-file or JavaScript errors are reported.

Opening `index.html` directly through a `file://` URL may prevent the browser from loading `dictionary.txt`. Use a local HTTP server for the full-dictionary test.

## 4. Test Approach

Testing is divided into the following categories:

| Test category | Purpose |
|---|---|
| Functional testing | Verify that each feature performs its intended task. |
| Input validation | Check empty, unusual, or invalid inputs. |
| Integration testing | Check that dictionary data, indexes, and interfaces work together. |
| Persistence testing | Check that supported custom-word changes survive a restart or page reload. |
| Error handling | Check behavior when a dictionary or required file is unavailable. |
| Regression testing | Recheck existing features after code changes. |
| Usability testing | Confirm that controls and results are understandable to users. |

Tests should be performed separately for the Java and browser implementations because they are independent applications.

## 5. Java Application Test Cases

### 5.1 Startup and dictionary loading

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| J-01 | Compile the Java source | Run `javac Spellseek.java` | Compilation completes without errors. |
| J-02 | Start the application | Run `java Spellseek` from the project directory | Spellseek starts and displays its menu. |
| J-03 | Load the default dictionary | Start with a readable `dictionary.txt` in the working directory | The application reports dictionary loading information and becomes ready. |
| J-04 | Start without the dictionary file | Temporarily use a working directory without `dictionary.txt` | The application reports that the file is unavailable and continues with its starter vocabulary. |
| J-05 | Load a custom dictionary path | Run the application with a valid dictionary path argument | The application attempts to load the specified file. |
| J-06 | Load a file containing blank lines and comments | Use a test dictionary with blank lines and lines beginning with `#` | Blank and comment lines are ignored. |

### 5.2 Word checking and spelling suggestions

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| J-07 | Check a known word | Select option `1` and enter `programming` | The word is recognized if it exists in the loaded dictionary. |
| J-08 | Check a misspelled word | Select option `1` and enter `progrmming` | The application reports that the word is not in the dictionary and lists possible suggestions when candidates are found. |
| J-09 | Check a transposed-letter typo | Enter `recieve` | The application searches for possible alternatives, including `receive` when available and within the search behavior. |
| J-10 | Check a word with different capitalization | Enter `Programming` | The Java implementation normalizes the input for lookup. |
| J-11 | Check an unknown word | Enter an unusual word that is absent from the dictionary | The application handles the input and reports no match or offers candidates, depending on the search results. |
| J-12 | Review suggestion count | Enter a misspelled word with multiple nearby candidates | No more than five suggestions are displayed by the Java implementation. |
| J-13 | Review suggestion ranking | Check a word with several candidates | Suggestions are ordered by edit distance, frequency, and alphabetical tie-breaking according to the implementation. |

**Note:** Suggestion results depend on the actual dictionary contents and frequency values. The exact ordering should be checked against the loaded data rather than assumed in advance.

### 5.3 Sentence checking

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| J-14 | Check a sentence with a typo | Select option `2` and enter `I like progrmming` | The application produces a proposed sentence and/or identifies possible spelling replacements. |
| J-15 | Check a sentence with multiple words | Enter `I recieved teh progrmming assignment` | The application processes the sentence and reports possible replacements for words it identifies as misspelled. |
| J-16 | Check a sentence with known words | Enter a sentence made up of dictionary words | The application processes the sentence without requiring spelling replacements for recognized words. |
| J-17 | Check punctuation and capitalization | Enter a sentence containing capital letters and punctuation | The application handles supported sentence text and displays its result. |
| J-18 | Review correction reliability | Check a sentence where a word has multiple possible replacements | The proposed sentence is treated as a suggestion and reviewed manually. |

Sentence correction should not be considered a grammar checker. A syntactically valid or contextually correct sentence is not guaranteed.

### 5.4 Adding words

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| J-19 | Add a new valid word | Select option `3` and enter a word not already in the dictionary | The application processes the word and reports the outcome. |
| J-20 | Check an added word in the current session | Add a word, then select option `1` and check it | The word should be recognized if insertion succeeds. |
| J-21 | Verify saved dictionary changes | Add a word, exit, and restart the application | The added word should be available if it was successfully saved to the dictionary file. |
| J-22 | Check a repeated word | Attempt to add a word that already exists | Verify that the application handles the duplicate according to its implementation without corrupting the dictionary. |
| J-23 | Check an invalid word | Try empty input or characters that do not form a supported word | The application should reject or safely handle unsupported input. |
| J-24 | Check file write failure | Test with a dictionary path that cannot be written to, where practical | The application should report or handle the write failure without falsely claiming that the word was saved. |

Run persistence tests using a temporary copy of the dictionary whenever possible. This prevents accidental modification of the main project dictionary.

### 5.5 Prefix autocomplete and statistics

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| J-25 | Search a common prefix | Select option `4` and enter `pro` | Matching dictionary words are listed. |
| J-26 | Search a longer prefix | Enter `program` | Results, if available, match the entered prefix. |
| J-27 | Search a prefix with no matches | Enter an unlikely prefix | The application handles the search without crashing and reports or displays no matches. |
| J-28 | View statistics | Select option `5` | Dictionary and index information is displayed. |
| J-29 | Exit normally | Select option `0` | The application exits normally. |
| J-30 | Use an invalid menu selection | Enter an unsupported menu option | The application handles the selection without terminating unexpectedly. |

## 6. Browser Application Test Cases

### 6.1 Page loading and interface

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| B-01 | Open the browser interface | Open `index.html` through Live Server | The Spellseek interface loads. |
| B-02 | Check the navigation tabs | Select Spell Checker, Data Structure Visualizer, and Dictionary Workbench | The corresponding interface sections can be opened. |
| B-03 | Check CSS loading | Inspect the page appearance | The stylesheet is applied without a missing-file error. |
| B-04 | Check JavaScript loading | Inspect the browser developer console | The required JavaScript files load without unhandled errors. |
| B-05 | Load the dictionary | Open the page through a local server with `dictionary.txt` present | The application attempts to load the dictionary and build its indexes. |
| B-06 | Test fallback behavior | Temporarily make the dictionary unavailable to the browser | The page uses its fallback vocabulary and displays the relevant notice, as implemented. |

### 6.2 Browser spelling checker

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| B-07 | Enter a known word | Type `programming` into the main input | The application handles the word as a dictionary lookup. |
| B-08 | Enter a misspelled word | Type `progrmming` | The Ranked Suggestions area displays possible alternatives when available. |
| B-09 | Change edit-distance threshold | Try values `1`, `2`, and `5` | The input accepts supported values from 1 through 5 and the search uses the selected threshold. |
| B-10 | Test the lower threshold boundary | Enter `1` | The control accepts the minimum supported value. |
| B-11 | Test the upper threshold boundary | Enter `5` | The control accepts the maximum supported value. |
| B-12 | Test an out-of-range value | Try `0` or `6` | Verify that the interface or application handles the invalid value safely. Do not assume browser input attributes alone guarantee validation. |
| B-13 | Test a transposition typo | Enter `recieve` | Check whether the candidate search and ranking return plausible suggestions based on the loaded dictionary and browser algorithm. |
| B-14 | Test an empty input | Clear the main word field | The interface handles the empty query without an unhandled error. |

### 6.3 Browser sentence checker

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| B-15 | Check a sentence with a typo | Enter `I recieved teh progrmming assignment.` and click **Check Sentence** | The result area displays the proposed correction or detected spelling issues. |
| B-16 | Check a sentence with known words | Enter a sentence containing known dictionary words | The sentence is processed without requiring replacements for recognized words. |
| B-17 | Check empty sentence input | Clear the text area and click **Check Sentence** | The interface handles empty input safely. |
| B-18 | Check sentence punctuation | Enter a sentence containing punctuation and capitalization | The result is displayed without an unhandled error. |

### 6.4 Prefix search and visualizers

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| B-19 | Search a common prefix | Enter `prog` and click **Find Prefix** | Matching dictionary words are displayed when available. |
| B-20 | Search a prefix with no matches | Enter an unlikely prefix and click **Find Prefix** | The interface handles the empty result safely. |
| B-21 | Check empty prefix input | Clear the prefix field and click **Find Prefix** | The application handles the input without an unhandled error. |
| B-22 | Open Trie Path Lookup | Open Data Structure Visualizer | The Trie visualization area is available. |
| B-23 | Open BK-Tree Metric Bounds | Open Data Structure Visualizer | The BK-tree visualization area is available. |
| B-24 | Verify visualization updates | Perform a supported lookup and observe the visualizer | Check whether the relevant visualization updates according to the existing implementation. |

### 6.5 Browser Dictionary Workbench

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| B-25 | Add a custom word | Enter a new word and a positive frequency, then click **Insert Word** | The interface processes the insertion and displays a status message. |
| B-26 | Check a custom word | Add a word, then search for it | The word should be recognized if insertion succeeds and the browser engine uses the updated dictionary. |
| B-27 | Reload the page | Add a custom word, reload, and check again | The word should remain available if local-storage persistence is working and the same browser profile is used. |
| B-28 | Try an invalid frequency | Enter a frequency below the allowed minimum or an invalid value | The interface or application should handle the invalid input safely. |
| B-29 | Check duplicate insertion | Add the same word more than once | Verify that duplicate handling does not corrupt the dictionary or indexes. |
| B-30 | Verify separation from the file | Add a browser custom word and inspect `dictionary.txt` | The browser workbench should not automatically rewrite `dictionary.txt`. |
| B-31 | Verify separation from Java | Add a word in the browser and check it in the Java application | The browser custom word should not be assumed to exist in Java unless separately added to the Java dictionary. |

## 7. Dictionary File Tests

Use a separate test dictionary for file-handling tests.

### Sample test dictionary

Create a file named `test-dictionary.txt` containing:

```text
# Spellseek test dictionary
hello 200
world 150
programming 120
spelling 100
receive 90

```

### Dictionary test cases

| Test ID | Test case | Test input/action | Expected result |
|---|---|---|---|
| D-01 | Load valid word-frequency lines | Use the sample dictionary | Valid entries are processed. |
| D-02 | Load blank lines | Include blank lines between entries | Blank lines are ignored. |
| D-03 | Load comments | Include a line beginning with `#` | The comment line is ignored. |
| D-04 | Load a missing file | Supply a nonexistent path | The Java application reports the missing file and continues with starter words. The browser uses its fallback behavior if fetching fails. |
| D-05 | Load a custom path | Supply the path to `test-dictionary.txt` | The Java application attempts to load the specified file. |
| D-06 | Check malformed lines | Include malformed entries in a disposable test file | Verify that malformed entries are handled according to the implementation without crashing the application. |
| D-07 | Check frequency values | Use entries with different frequencies | Frequency values influence suggestion ranking or autocomplete ordering where applicable. |

Do not use the main `dictionary.txt` for destructive test cases. Keep a backup before modifying dictionary contents.

## 8. Data Structure and Algorithm Checks

These checks help confirm that the structures work together correctly.

| Test ID | Component | Check | Expected result |
|---|---|---|---|
| A-01 | Dictionary map | Search for an existing word | The dictionary lookup returns the stored entry when present. |
| A-02 | Trie | Search for a complete dictionary word | The word is found when it exists in the Trie. |
| A-03 | Trie | Search for a prefix | Matching words can be found through the prefix-search functionality. |
| A-04 | Trie | Search for a prefix that is not a complete word | The application distinguishes a valid path from a complete word where applicable. |
| A-05 | BK-tree | Search for a word close to the query | Candidate words within the configured search behavior can be returned. |
| A-06 | Edit distance | Compare identical words | Their standard Levenshtein distance is zero. |
| A-07 | Edit distance | Compare words with one insertion, deletion, or substitution | The standard Levenshtein distance reflects one edit for each such operation. |
| A-08 | Transposition-aware ranking | Check a word with adjacent letters swapped | The Java suggestion-ranking logic can treat an adjacent transposition as one edit. Verify the browser implementation separately. |
| A-09 | Frequency ranking | Compare candidates with equal edit distance | Higher-frequency candidates should rank first, with alphabetical order used for ties. |
| A-10 | Index consistency | Add a word through a supported insertion workflow and search for it | The dictionary and relevant indexes remain consistent after a successful insertion. |

These are behavioral checks, not proof of formal correctness for every possible input.

## 9. Input Validation and Robustness

The following inputs should be tested in both applications wherever the feature accepts them:

- Empty input.
- Leading or trailing spaces.
- Uppercase and lowercase forms of the same word.
- A very short word.
- A long word.
- Punctuation surrounding a word.
- Apostrophes in supported words.
- A word not present in the dictionary.
- An empty sentence.
- A prefix with no matches.
- Repeated insertion of the same custom word.
- Invalid or missing dictionary files.
- Invalid menu selections in the Java version.
- Unsupported frequency or edit-distance values.

For each case, verify that the application behaves safely and provides a sensible result or message. Do not assume every input has a guaranteed correction.

## 10. Regression Testing

Run regression tests after modifying the dictionary, Java source, HTML, CSS, or JavaScript files.

Minimum regression checklist:

- [ ] Java source compiles successfully.
- [ ] Java application starts and displays its menu.
- [ ] Java word checking works.
- [ ] Java sentence checking works.
- [ ] Java custom-word insertion works.
- [ ] Java prefix autocomplete works.
- [ ] Java statistics are displayed.
- [ ] Java application exits normally.
- [ ] Browser interface loads.
- [ ] Browser dictionary loading works through a local server.
- [ ] Browser word suggestions appear.
- [ ] Browser sentence checking works.
- [ ] Browser prefix search works.
- [ ] Browser visualizer areas remain accessible.
- [ ] Browser custom-word insertion works.
- [ ] Browser local-storage persistence is checked.
- [ ] No unexpected browser console errors occur.

## 11. Recording Test Results

Record each test after executing it.

Use the following status values:

- **Not Run:** The test has not been executed.
- **Pass:** The observed result matches the expected result.
- **Fail:** The observed result does not match the expected result.
- **Blocked:** The test cannot be completed because a required dependency or environment is unavailable.

### Test execution record

| Test ID | Actual result | Status | Notes / defect reference |
|---|---|---|---|
| J-01 |  | Not Run |  |
| J-02 |  | Not Run |  |
| J-07 |  | Not Run |  |
| J-08 |  | Not Run |  |
| J-14 |  | Not Run |  |
| J-19 |  | Not Run |  |
| J-25 |  | Not Run |  |
| J-28 |  | Not Run |  |
| B-01 |  | Not Run |  |
| B-05 |  | Not Run |  |
| B-08 |  | Not Run |  |
| B-15 |  | Not Run |  |
| B-19 |  | Not Run |  |
| B-25 |  | Not Run |  |
| A-05 |  | Not Run |  |
| D-04 |  | Not Run |  |

Add rows for any additional tests executed. Do not mark a test as passed solely because the feature is documented.

## 12. Defect Reporting

When a test fails, record:

1. **Defect ID:** A unique identifier, such as `BUG-001`.
2. **Test ID:** The test that failed.
3. **Environment:** Operating system, Java version, or browser.
4. **Steps to reproduce:** The exact steps taken.
5. **Expected result:** What should have happened.
6. **Actual result:** What happened instead.
7. **Evidence:** Error message, screenshot, or terminal output where useful.
8. **Severity:** Low, Medium, or High.
9. **Status:** Open, In Progress, or Resolved.

After fixing a defect, repeat the failed test and the relevant regression tests.

## 13. Acceptance Criteria

The project is ready for a basic demonstration when:

- The Java application compiles and starts successfully.
- The Java menu options execute their intended functions.
- The browser interface loads through a local server.
- The browser can load the dictionary or clearly use its fallback behavior.
- Word checking and suggestion generation work for representative inputs.
- Sentence checking produces a reviewable result.
- Prefix autocomplete returns matching words where available.
- Custom-word insertion behaves as documented in each version.
- Dictionary and index statistics can be viewed in the Java version.
- The application handles common invalid or missing inputs without an unexpected crash.
- The main regression checklist has been completed and failures have been documented.

Acceptance should be based on observed test results, not on the existence of this test plan alone.

## 14. Limitations

Spellseek's results depend on dictionary coverage, frequency values, edit-distance search, and the implemented ranking logic.

The application may:

- Miss a valid word that is absent from the dictionary.
- Suggest a word that is not appropriate in the sentence's context.
- Rank a plausible alternative below another candidate.
- Produce different results between Java and browser implementations.
- Use fallback vocabulary if the browser cannot load the full dictionary.

Sentence suggestions should therefore be reviewed by a person before being accepted.

## 15. Conclusion

This test plan provides a repeatable way to evaluate Spellseek's Java command-line application and browser interface. It covers the main user-facing features, dictionary handling, data structures, input robustness, persistence, and regression testing.

Execute the relevant tests whenever the application or dictionary changes, record the actual results, and address failures before presenting the project as fully tested.
