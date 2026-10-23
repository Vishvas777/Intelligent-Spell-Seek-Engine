
# Changelog

All notable changes to Spellseek will be documented in this file.

This changelog follows the general principles of Keep a Changelog. Version numbers and dates will be added when official releases are made.

## [Unreleased]

### Added
- Added project documentation for contributors and maintainers.
- Added a `.gitignore` file to exclude compiled Java files, build output, IDE settings, logs, and temporary files.
- Added an MIT license file for the project, subject to the licensing terms of any third-party materials.

### Documentation
- Documented the project structure and contribution workflow.
- Added guidance for testing the Java spell checker and browser interface.
- Documented expectations for dictionary changes and bug reports.

### Maintenance
- Organized repository documentation to make the project easier to understand and maintain.
- Clarified that Java and browser implementations should be tested separately when their behavior or runtime differs.

## Existing Project Features

The following features belong to the existing Spellseek project. Their inclusion here describes the project's intended functionality; it does not claim that they were newly implemented in this changelog entry.

### Spelling Correction
- Java-based command-line spell checker.
- Browser-based spelling checker using HTML, CSS, and JavaScript.
- Dictionary-based word lookup and spelling suggestions.

### Data Structures and Algorithms
- Trie-based word storage and lookup.
- BK-tree-based approximate word search.
- Edit-distance-based comparison for identifying possible spelling corrections.

### Web Interface
- Spell Checker interface.
- Data Structure Visualizer.
- Dictionary Workbench.
- Separate HTML, CSS, and JavaScript files for interface and application logic.

### Dictionary
- Dictionary data stored separately from application source code.
- Support for word-frequency information where provided by the dictionary format and implementation.

## Version History

No numbered release history has been documented yet.

When a version is released, add an entry using the following structure:

### [1.0.0] - YYYY-MM-DD

#### Added
- List new features introduced in this release.

#### Changed
- List changes to existing behavior.

#### Fixed
- List bugs fixed in this release.

#### Removed
- List features or files removed in this release.

#### Documentation
- List important documentation updates.

## Changelog Guidelines

- Add new development changes under `[Unreleased]`.
- Move completed changes into a numbered version section when releasing.
- Use the release date in `YYYY-MM-DD` format.
- Describe changes clearly and concisely.
- Record actual changes rather than planned or unverified features.
- Do not mark a bug as fixed until the fix has been tested.
- Keep entries useful to developers and users.

---

*This changelog should be updated as the project evolves.*
