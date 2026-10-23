
# Contributing to Spellseek

Thank you for your interest in contributing to Spellseek!

Spellseek is a spelling-correction project that combines a Java-based spell checker with an HTML, CSS, and JavaScript web interface. It uses data structures and string-distance techniques to help identify misspelled words and suggest possible corrections.

Contributions that improve correctness, usability, performance, documentation, or testing are welcome.

## Table of Contents

- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Coding Guidelines](#coding-guidelines)
- [Testing Your Changes](#testing-your-changes)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Features](#suggesting-features)
- [Submitting Changes](#submitting-changes)
- [Dictionary Contributions](#dictionary-contributions)
- [Code of Conduct](#code-of-conduct)
- [License](#license)

## Project Structure

The project may contain the following files and directories:

```text
Spellseek/
├── Spellseek.java
├── index.html
├── dictionary.txt
├── test_output.txt
├── css/
│   └── style.css
├── js/
│   ├── trie.js
│   ├── bktree.js
│   ├── spellseek.js
│   └── app.js
├── README.md
├── CONTRIBUTING.md
├── LICENSE
└── .gitignore
```

The exact filenames may differ depending on the current version of the project.

### Main Components

- **Java spell checker:** Implements the command-line spelling-correction functionality.
- **HTML interface:** Provides the structure of the web application.
- **CSS stylesheet:** Controls the appearance and layout of the interface.
- **JavaScript modules:** Implement browser-side spelling correction, data structures, and interface interactions.
- **Dictionary:** Supplies words and, where supported, word-frequency information for spelling suggestions.

Keep the Java and browser implementations consistent when changing shared spelling-correction behavior.

## Getting Started

### 1. Get the Project

Clone the repository:

```bash
git clone <repository-url>
```

Replace `<repository-url>` with the actual URL of the Spellseek GitHub repository.

Alternatively, download the repository ZIP file and extract it.

### 2. Open the Project

Open the extracted project folder in your preferred editor.

For Java development, use an editor or IDE that supports your installed Java Development Kit (JDK).

For web development, use a modern browser and an editor such as Visual Studio Code.

### 3. Check the Existing Implementation

Before making changes:

- Read the project README.
- Review the relevant Java or JavaScript source files.
- Check how the dictionary is loaded.
- Understand the existing data structures and spelling-correction workflow.
- Run the current application before modifying it, if possible.

Follow the existing project structure rather than introducing unnecessary files or dependencies.

## How to Contribute

You can contribute in several ways:

- Fix spelling-correction bugs.
- Improve suggestion accuracy.
- Improve Trie or BK-tree implementations.
- Optimize edit-distance calculations.
- Improve dictionary handling.
- Fix HTML, CSS, or JavaScript interface issues.
- Improve accessibility and responsive design.
- Add useful test cases.
- Improve documentation and setup instructions.

Please focus each contribution on a clear, understandable change.

## Coding Guidelines

### Java

- Use meaningful class, method, and variable names.
- Keep methods focused on a single responsibility where practical.
- Handle invalid input and unexpected file conditions appropriately.
- Avoid unnecessary dependencies.
- Preserve compatibility with the Java version used by the project.
- Avoid changing existing public behavior without explaining the reason.

### JavaScript

- Use clear and descriptive variable and function names.
- Keep spelling logic separate from interface code where practical.
- Check that dictionary data is available before using it.
- Handle empty input and unexpected input safely.
- Avoid adding external libraries unless they provide a clear benefit.
- Preserve compatibility with the existing HTML elements and JavaScript module interfaces.

### HTML and CSS

- Use semantic HTML elements where appropriate.
- Keep the interface readable and usable on different screen sizes.
- Maintain consistent styling with the existing application.
- Ensure buttons, forms, and tabs continue to work.
- Avoid breaking existing element IDs or selectors used by JavaScript.

### General

- Write readable code and add comments where they clarify non-obvious logic.
- Avoid unrelated changes in the same contribution.
- Do not commit generated build files, editor settings, temporary files, or private credentials.
- Update documentation when setup steps or user-facing behavior change.

## Testing Your Changes

Before submitting a contribution, test the affected parts of the project.

### Java Spell Checker

Check that:

- The program compiles successfully.
- Correctly spelled words are handled appropriately.
- Misspelled words produce sensible suggestions.
- Empty input and unsupported input are handled safely.
- Dictionary loading works with the expected file format.
- Existing command-line functionality continues to work.

Compile and run the Java program using the instructions in the project README. For a simple standalone source file, the commands may resemble:

```bash
javac Spellseek.java
java Spellseek
```

These commands apply only if the source file and its entry point support standalone compilation and execution.

### HTML and JavaScript Interface

Check that:

- The web page opens correctly.
- All expected CSS and JavaScript files load.
- The spelling checker accepts user input.
- Suggestions are displayed correctly.
- Tabs and other interface controls work.
- The dictionary loads correctly in the browser.
- The browser console does not show new errors.

If the browser blocks local file access, run the project through a local development server instead.

### Regression Testing

After making changes, repeat relevant tests to make sure existing functionality has not been broken.

If you add a bug fix, include a reproducible example of the original problem and verify that it is resolved.

## Reporting Bugs

When reporting a bug, include:

1. A clear description of the problem.
2. Steps to reproduce it.
3. The expected behavior.
4. The actual behavior.
5. The relevant error message, if available.
6. Your operating system, Java version, and browser when relevant.

For spelling-correction issues, provide the input word and the suggestions returned by the application.

Do not include passwords, API keys, or other private information in bug reports.

## Suggesting Features

Feature suggestions should explain:

- The problem the feature would solve.
- How the feature would work.
- Why it would be useful.
- Whether it affects the Java version, browser version, or both.

Examples include improved suggestion ranking, additional dictionary support, better visualizations, and clearer error messages.

Discuss large changes before investing significant time in implementation.

## Submitting Changes

A typical contribution workflow is:

1. Fork the repository, if required.
2. Create a branch for your change.
3. Implement the change.
4. Test the affected functionality.
5. Review your changes for unrelated edits or temporary files.
6. Commit your work with a clear message.
7. Push your branch to your fork.
8. Open a pull request against the original repository.

Example commands:

```bash
git checkout -b fix/spelling-suggestions
git add .
git commit -m "Fix spelling suggestion ranking"
git push origin fix/spelling-suggestions
```

Before using `git add .`, check `git status` and ensure that you are not committing unwanted files.

In your pull request, explain:

- What changed.
- Why the change was necessary.
- How you tested it.
- Any limitations or follow-up work.

Keep pull requests focused and provide screenshots for significant user-interface changes when helpful.

## Dictionary Contributions

The dictionary is an important part of Spellseek's behavior.

When changing dictionary data:

- Preserve the expected file format.
- Avoid adding duplicate entries unintentionally.
- Check spelling and encoding.
- Preserve frequency values or other fields if the application expects them.
- Test that both relevant implementations can read the updated data.
- Verify the source and license of externally obtained word lists before redistributing them.

Do not replace the project's dictionary with an incompatible format without updating the corresponding loading logic and documentation.

## Code of Conduct

All contributors are expected to communicate respectfully and constructively.

- Be respectful of different skill levels and viewpoints.
- Provide helpful and specific feedback.
- Focus criticism on code and ideas, not individuals.
- Avoid harassment, insults, and discriminatory behavior.
- Help maintain a welcoming environment for learning and collaboration.

## License

By contributing, you agree that your contributions may be distributed under the project's MIT License, provided you have the legal right to submit those contributions.

Ensure that you have permission to contribute any code, documentation, or dictionary data that you submit.

If you are unsure whether third-party material can be included, clarify its licensing before submitting it.

## Thank You

Thank you for helping improve Spellseek!

Every useful bug report, test case, documentation improvement, and code contribution helps make the project more reliable and easier to use.
