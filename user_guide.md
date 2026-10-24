# Spellseek — User Guide

## 1. Introduction

Spellseek helps users check spelling and find possible corrections using a dictionary-based algorithm.

## 2. Requirements

* A modern web browser for the browser version.
* A local web server, such as VS Code Live Server, to serve the browser application and dictionary file.
* A Java Development Kit (JDK) for the Java command-line version.

## 3. Running the Browser Version

1. Download or clone the project repository.
2. Open the project folder in your development environment.
3. Start a local web server.
4. Open the application's HTML page through the local server.
5. Enter a word and review the available results.

## 4. Running the Java Version

Open a terminal in the directory containing `Spellseek.java`.

Compile the program:

```bash
javac Spellseek.java
```

Run it:

```bash
java Spellseek
```

Follow the prompts displayed by the command-line application.

## 5. Understanding Results

### Exact Match

The entered word is recognized if it exists in the loaded dictionary.

### Suggested Corrections

If the word is not recognized, the application may display similar dictionary words.

### Autocomplete

Where supported by the interface, entering a prefix can display words that begin with those characters.

## 6. Troubleshooting

### Dictionary Does Not Load

Ensure that the dictionary file is present at the expected path and that the application is served from the correct project directory.

### Browser Shows an Error

Check the browser console and verify that the local server is running.

### Java Compilation Fails

Verify that the JDK is installed and that the terminal is opened in the directory containing the source file.

## 7. Conclusion

Following these steps helps users launch Spellseek and explore its spelling suggestions and autocomplete features.
