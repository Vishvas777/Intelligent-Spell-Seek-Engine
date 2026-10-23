import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spellseek: an intelligent spelling-correction engine.
 * Java 8+; no external libraries required.
 *
 * Data structures:
 * 1. HashMap: stores each dictionary word and its frequency.
 * 2. Trie: exact lookup and prefix autocomplete.
 * 3. BK-tree: approximate lookup using Levenshtein edit distance.
 */
public class Spellseek {
    private static final int MAX_SUGGESTIONS = 5;
    private static final Pattern WORD_PATTERN =
            Pattern.compile("[\\p{L}]+(?:['’][\\p{L}]+)*");

    private final Map<String, WordEntry> dictionary = new HashMap<String, WordEntry>();
    private final Trie trie = new Trie();
    private final BKTree bkTree = new BKTree();

    public static void main(String[] args) {
        Path dictionaryPath = args.length > 0
                ? Paths.get(args[0])
                : Paths.get("dictionary.txt");

        Spellseek spellseek = new Spellseek();
        spellseek.loadStarterWords();

        if (Files.isRegularFile(dictionaryPath)) {
            try {
                int loaded = spellseek.loadDictionary(dictionaryPath);
                System.out.println("Loaded " + loaded + " dictionary entries from "
                        + dictionaryPath.toAbsolutePath());
            } catch (IOException ex) {
                System.out.println("Warning: could not read dictionary file: " + ex.getMessage());
                System.out.println("Continuing with the built-in starter vocabulary.");
            }
        } else {
            System.out.println("No dictionary file found at " + dictionaryPath.toAbsolutePath());
            System.out.println("Using the built-in starter vocabulary. You can add words from the menu.");
        }

        System.out.println("Spellseek is ready. Dictionary size: " + spellseek.dictionary.size());
        spellseek.runMenu(dictionaryPath);
    }

    private void loadStarterWords() {
        String words =
                "about above across act action actually add after again against age ago air all " +
                "allow almost along already also although always am among an and animal another " +
                "answer any anyone anything appear are area around as ask at away back be because " +
                "become been before began begin behind believe best better between big book both " +
                "boy bring build business but by call came can car care case change check child " +
                "city class clear close code computer consider could country course create day " +
                "different do does done door down each early earth easy end enough even ever every " +
                "example eye face fact family far feel few find first follow food for form found " +
                "four friend from full game general get give go good got great group grow had hand " +
                "happen has have he head hear help her here high him his history home house how " +
                "however I idea if important in include information input inside instead into is it " +
                "its job just keep kind know language large last later learn least leave left less " +
                "let life light like line list little live local long look made make man many map " +
                "mark may me mean measure men might mind miss more most move much music must my " +
                "name near need never new next night no not note number of off often old on once one " +
                "only open or order other our out over own page part people place plan play point " +
                "possible power present problem program project provide public put question quick " +
                "quite read real reason right run said same saw say school science sea search second " +
                "see seem sentence serve set several she should show side simple since small so some " +
                "something sound spell spelling spellseek start state still stop story student study " +
                "such system take talk tell test than that the their them then there these they thing " +
                "think this those through time to together too tree try turn two under understand " +
                "until up use useful user usually value very want was water way we well went were " +
                "what when where which while white who whole why will with word work world would write " +
                "year yes yet you your hello programming java intelligent correction engine spelling " +
                "algorithm data structure dictionary frequency suggestion suggestions trie tree distance " +
                "autocomplete typo incorrect correct correction demonstration robust implementation " +
                "example sentence quick brown fox jumps over lazy dog their there they're receive " +
                "separate necessary definitely occurrence recommendation environment development " +
                "student college evaluation complexity efficient search insertion deletion substitution " +
                "transposition spellingchecker spellchecker spell-checking spellcheck";

        String[] starterWords = words.split("\\s+");
        for (String word : starterWords) {
            addWord(word, 1L);
        }

        boost("the", 50000); boost("be", 30000); boost("to", 29000);
        boost("of", 28000); boost("and", 27000); boost("a", 26000);
        boost("in", 25000); boost("that", 24000); boost("have", 23000);
        boost("I", 22000); boost("it", 21000); boost("for", 20000);
        boost("not", 19000); boost("on", 18000); boost("with", 17000);
        boost("he", 16000); boost("as", 15000); boost("you", 14000);
        boost("do", 13000); boost("at", 12000); boost("hello", 200);
        boost("world", 150); boost("programming", 120); boost("computer", 100);
    }

    private void boost(String word, long frequency) {
        addWord(word, frequency);
    }

    /** Loads a word per line, optionally followed by a numeric frequency. */
    private int loadDictionary(Path path) throws IOException {
        int accepted = 0;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                String[] parts = trimmed.split("\\s+");
                String word = parts[0];
                long frequency = 1L;
                if (parts.length >= 2) {
                    try {
                        long parsed = Long.parseLong(parts[1]);
                        if (parsed > 0L) {
                            frequency = parsed;
                        }
                    } catch (NumberFormatException ignored) {
                        // Treat a non-numeric second field as metadata; default frequency remains 1.
                    }
                }

                if (addWord(word, frequency)) {
                    accepted++;
                }
            }
        }
        return accepted;
    }

    private boolean addWord(String rawWord, long frequency) {
        String word = normalizeWord(rawWord);
        if (!isValidWord(word)) {
            return false;
        }

        WordEntry existing = dictionary.get(word);
        if (existing != null) {
            existing.frequency = Math.max(existing.frequency, Math.max(1L, frequency));
            return true;
        }

        WordEntry entry = new WordEntry(word, Math.max(1L, frequency));
        dictionary.put(word, entry);
        trie.insert(entry);
        bkTree.insert(entry);
        return true;
    }

    private static String normalizeWord(String word) {
        if (word == null) {
            return "";
        }
        return word.trim().replace('\u2019', '\'').toLowerCase(Locale.ROOT);
    }

    private static boolean isValidWord(String word) {
        return word != null && !word.isEmpty() && WORD_PATTERN.matcher(word).matches();
    }

    private void runMenu(Path dictionaryPath) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n========== SPELLSEEK ==========");
            System.out.println("1. Check a word");
            System.out.println("2. Check and suggest corrections for a sentence");
            System.out.println("3. Add a word to the dictionary");
            System.out.println("4. Autocomplete a prefix");
            System.out.println("5. Show dictionary statistics");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    checkOneWord(scanner);
                    break;
                case "2":
                    checkSentence(scanner);
                    break;
                case "3":
                    addWordFromUser(scanner, dictionaryPath);
                    break;
                case "4":
                    autocomplete(scanner);
                    break;
                case "5":
                    showStatistics();
                    break;
                case "0":
                    System.out.println("Spellseek closed. Thank you!");
                    return;
                default:
                    System.out.println("Invalid option. Enter a number from 0 to 5.");
            }
        }
    }

    private void checkOneWord(Scanner scanner) {
        System.out.print("Enter one word: ");
        String input = scanner.nextLine().trim();
        Matcher matcher = WORD_PATTERN.matcher(input);
        if (!matcher.find()) {
            System.out.println("No valid word was found in the input.");
            return;
        }
        String word = normalizeWord(matcher.group());
        if (matcher.find()) {
            System.out.println("Please enter only one word.");
            return;
        }

        if (trie.contains(word)) {
            System.out.println("Correct spelling: " + word);
            return;
        }

        List<Suggestion> suggestions = suggest(word);
        if (suggestions.isEmpty()) {
            System.out.println("No close match found for '" + word + "'. You can add it to the dictionary.");
        } else {
            System.out.println("'" + word + "' is not in the dictionary. Suggestions:");
            printSuggestions(suggestions);
        }
    }

    private void checkSentence(Scanner scanner) {
        System.out.print("Enter a sentence: ");
        String sentence = scanner.nextLine();
        Matcher matcher = WORD_PATTERN.matcher(sentence);
        StringBuilder proposed = new StringBuilder();
        List<String> reports = new ArrayList<String>();
        int lastPosition = 0;

        while (matcher.find()) {
            proposed.append(sentence, lastPosition, matcher.start());
            String original = matcher.group();
            String normalized = normalizeWord(original);

            if (trie.contains(normalized)) {
                proposed.append(original);
            } else {
                List<Suggestion> suggestions = suggest(normalized);
                if (suggestions.isEmpty()) {
                    proposed.append(original);
                    reports.add(original + " -> no close suggestion found");
                } else {
                    String replacement = applyOriginalCase(original, suggestions.get(0).word);
                    proposed.append(replacement);
                    reports.add(original + " -> " + replacement + " (options: "
                            + formatSuggestions(suggestions) + ")");
                }
            }
            lastPosition = matcher.end();
        }
        proposed.append(sentence.substring(lastPosition));

        System.out.println("Proposed sentence: " + proposed);
        if (reports.isEmpty()) {
            System.out.println("No misspellings found in the current dictionary.");
        } else {
            System.out.println("Review these words before accepting the proposed sentence:");
            for (String report : reports) {
                System.out.println("  " + report);
            }
        }
        System.out.println("Note: names and specialist terms may need to be added to the dictionary.");
    }

    private void addWordFromUser(Scanner scanner, Path dictionaryPath) {
        System.out.print("Enter the word to add: ");
        String rawWord = scanner.nextLine().trim();
        String word = normalizeWord(rawWord);
        if (!isValidWord(word)) {
            System.out.println("Invalid word. Use letters, with optional apostrophes inside the word.");
            return;
        }
        if (trie.contains(word)) {
            System.out.println("'" + word + "' is already in the dictionary.");
            return;
        }

        addWord(word, 5L);
        try {
            Path parent = dictionaryPath.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // A dictionary file may not end with a newline. Add one before appending,
            // otherwise the new word could be joined to the last existing dictionary entry.
            if (Files.exists(dictionaryPath) && Files.size(dictionaryPath) > 0L) {
                try (RandomAccessFile file = new RandomAccessFile(dictionaryPath.toFile(), "r")) {
                    file.seek(file.length() - 1L);
                    int lastByte = file.read();
                    if (lastByte != '\n' && lastByte != '\r') {
                        Files.write(dictionaryPath,
                                System.lineSeparator().getBytes(StandardCharsets.UTF_8),
                                StandardOpenOption.APPEND);
                    }
                }
            }
            Files.write(dictionaryPath,
                    Collections.singletonList(word + " 5"),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
            System.out.println("Added '" + word + "' to memory and saved it to " + dictionaryPath.toAbsolutePath());
        } catch (IOException ex) {
            System.out.println("Added '" + word + "' for this session, but could not save it: " + ex.getMessage());
        }
    }

    private void autocomplete(Scanner scanner) {
        System.out.print("Enter a prefix: ");
        String prefix = normalizeWord(scanner.nextLine());
        if (prefix.isEmpty() || !WORD_PATTERN.matcher(prefix).matches()) {
            System.out.println("Enter a prefix containing letters.");
            return;
        }

        List<WordEntry> matches = trie.autocomplete(prefix, 10);
        if (matches.isEmpty()) {
            System.out.println("No dictionary words start with '" + prefix + "'.");
            return;
        }
        System.out.println("Autocomplete results:");
        for (WordEntry entry : matches) {
            System.out.println("  " + entry.word + " (frequency " + entry.frequency + ")");
        }
    }

    private void showStatistics() {
        System.out.println("Dictionary words: " + dictionary.size());
        System.out.println("Indexes: HashMap + Trie + BK-tree");
        System.out.println("Maximum edit distance: 1-3, based on input word length");
        System.out.println("Ranking: lowest edit distance, highest frequency, then alphabetical order");
        System.out.println("Adjacent letter transpositions are counted as one edit (for example, recieve -> receive).");
    }

    private List<Suggestion> suggest(String word) {
        if (word == null || word.isEmpty()) {
            return Collections.emptyList();
        }
        int maxDistance;
        if (word.length() <= 4) {
            maxDistance = 1;
        } else if (word.length() <= 8) {
            maxDistance = 2;
        } else {
            maxDistance = 3;
        }

        // A transposition may cost two Levenshtein edits, so search a wider BK-tree
        // radius, then apply the transposition-aware distance to the returned candidates.
        List<Suggestion> candidates = bkTree.search(word, maxDistance * 2);
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        for (Suggestion candidate : candidates) {
            int distance = optimalStringAlignmentDistance(word, candidate.word);
            if (distance <= maxDistance) {
                suggestions.add(new Suggestion(candidate.word, distance, candidate.frequency));
            }
        }

        suggestions.sort(new Comparator<Suggestion>() {
            @Override
            public int compare(Suggestion left, Suggestion right) {
                int byDistance = Integer.compare(left.distance, right.distance);
                if (byDistance != 0) {
                    return byDistance;
                }
                int byFrequency = Long.compare(right.frequency, left.frequency);
                if (byFrequency != 0) {
                    return byFrequency;
                }
                return left.word.compareTo(right.word);
            }
        });

        if (suggestions.size() > MAX_SUGGESTIONS) {
            return new ArrayList<Suggestion>(suggestions.subList(0, MAX_SUGGESTIONS));
        }
        return suggestions;
    }

    /** Edit distance with support for one adjacent transposition. */
    private static int optimalStringAlignmentDistance(String first, String second) {
        int m = first.length();
        int n = second.length();
        int[][] distance = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = first.charAt(i - 1) == second.charAt(j - 1) ? 0 : 1;
                int deletion = distance[i - 1][j] + 1;
                int insertion = distance[i][j - 1] + 1;
                int substitution = distance[i - 1][j - 1] + cost;
                distance[i][j] = Math.min(Math.min(deletion, insertion), substitution);

                if (i > 1 && j > 1
                        && first.charAt(i - 1) == second.charAt(j - 2)
                        && first.charAt(i - 2) == second.charAt(j - 1)) {
                    distance[i][j] = Math.min(distance[i][j], distance[i - 2][j - 2] + 1);
                }
            }
        }
        return distance[m][n];
    }

    private static void printSuggestions(List<Suggestion> suggestions) {
        for (int i = 0; i < suggestions.size(); i++) {
            Suggestion suggestion = suggestions.get(i);
            System.out.println("  " + (i + 1) + ". " + suggestion.word
                    + "  [edit distance=" + suggestion.distance
                    + ", frequency=" + suggestion.frequency + "]");
        }
    }

    private static String formatSuggestions(List<Suggestion> suggestions) {
        List<String> values = new ArrayList<String>();
        for (Suggestion suggestion : suggestions) {
            values.add(suggestion.word + ":" + suggestion.distance);
        }
        return String.join(", ", values);
    }

    private static String applyOriginalCase(String original, String replacement) {
        if (original.equals(original.toUpperCase(Locale.ROOT))) {
            return replacement.toUpperCase(Locale.ROOT);
        }
        if (!original.isEmpty() && Character.isUpperCase(original.charAt(0))) {
            return Character.toUpperCase(replacement.charAt(0)) + replacement.substring(1);
        }
        return replacement;
    }

    private static final class WordEntry {
        private final String word;
        private long frequency;

        private WordEntry(String word, long frequency) {
            this.word = word;
            this.frequency = frequency;
        }
    }

    private static final class Suggestion {
        private final String word;
        private final int distance;
        private final long frequency;

        private Suggestion(String word, int distance, long frequency) {
            this.word = word;
            this.distance = distance;
            this.frequency = frequency;
        }
    }

    /** Trie supports O(L) lookup and prefix traversal, where L is word length. */
    private static final class Trie {
        private final TrieNode root = new TrieNode();

        private void insert(WordEntry entry) {
            TrieNode current = root;
            for (int i = 0; i < entry.word.length(); i++) {
                char ch = entry.word.charAt(i);
                TrieNode next = current.children.get(ch);
                if (next == null) {
                    next = new TrieNode();
                    current.children.put(ch, next);
                }
                current = next;
            }
            current.entry = entry;
        }

        private boolean contains(String word) {
            TrieNode node = findNode(word);
            return node != null && node.entry != null;
        }

        private TrieNode findNode(String prefix) {
            TrieNode current = root;
            for (int i = 0; i < prefix.length(); i++) {
                current = current.children.get(prefix.charAt(i));
                if (current == null) {
                    return null;
                }
            }
            return current;
        }

        private List<WordEntry> autocomplete(String prefix, int limit) {
            TrieNode start = findNode(prefix);
            if (start == null) {
                return Collections.emptyList();
            }

            List<WordEntry> found = new ArrayList<WordEntry>();
            Deque<TrieState> stack = new ArrayDeque<TrieState>();
            stack.push(new TrieState(start, prefix));
            while (!stack.isEmpty()) {
                TrieState state = stack.pop();
                if (state.node.entry != null) {
                    found.add(state.node.entry);
                }
                for (Map.Entry<Character, TrieNode> child : state.node.children.entrySet()) {
                    stack.push(new TrieState(child.getValue(), state.prefix + child.getKey()));
                }
            }

            found.sort(new Comparator<WordEntry>() {
                @Override
                public int compare(WordEntry left, WordEntry right) {
                    int byFrequency = Long.compare(right.frequency, left.frequency);
                    return byFrequency != 0 ? byFrequency : left.word.compareTo(right.word);
                }
            });
            if (found.size() > limit) {
                return new ArrayList<WordEntry>(found.subList(0, limit));
            }
            return found;
        }
    }

    private static final class TrieNode {
        private final Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();
        private WordEntry entry;
    }

    private static final class TrieState {
        private final TrieNode node;
        private final String prefix;

        private TrieState(TrieNode node, String prefix) {
            this.node = node;
            this.prefix = prefix;
        }
    }

    /**
     * BK-tree narrows approximate lookups by Levenshtein-distance intervals.
     * Iterative insert/search avoids recursive stack growth on unbalanced trees.
     */
    private static final class BKTree {
        private BKNode root;

        private void insert(WordEntry entry) {
            if (root == null) {
                root = new BKNode(entry);
                return;
            }

            BKNode current = root;
            while (true) {
                int distance = levenshtein(entry.word, current.entry.word);
                if (distance == 0) {
                    current.entry = entry;
                    return;
                }

                BKNode next = current.children.get(distance);
                if (next == null) {
                    current.children.put(distance, new BKNode(entry));
                    return;
                }
                current = next;
            }
        }

        private List<Suggestion> search(String query, int maxDistance) {
            if (root == null) {
                return Collections.emptyList();
            }

            List<Suggestion> found = new ArrayList<Suggestion>();
            Deque<BKNode> stack = new ArrayDeque<BKNode>();
            stack.push(root);

            while (!stack.isEmpty()) {
                BKNode node = stack.pop();
                int distance = levenshtein(query, node.entry.word);
                if (distance <= maxDistance) {
                    found.add(new Suggestion(node.entry.word, distance, node.entry.frequency));
                }

                int lowerBound = Math.max(0, distance - maxDistance);
                int upperBound = distance + maxDistance;
                for (Map.Entry<Integer, BKNode> child : node.children.entrySet()) {
                    int edgeDistance = child.getKey();
                    if (edgeDistance >= lowerBound && edgeDistance <= upperBound) {
                        stack.push(child.getValue());
                    }
                }
            }
            return found;
        }
    }

    private static final class BKNode {
        private WordEntry entry;
        private final Map<Integer, BKNode> children = new HashMap<Integer, BKNode>();

        private BKNode(WordEntry entry) {
            this.entry = entry;
        }
    }

    /** Exact Levenshtein distance using O(min(m,n)) extra memory. */
    private static int levenshtein(String first, String second) {
        if (first.equals(second)) {
            return 0;
        }
        if (first.length() < second.length()) {
            String temporary = first;
            first = second;
            second = temporary;
        }

        int m = first.length();
        int n = second.length();
        int[] previous = new int[n + 1];
        int[] current = new int[n + 1];

        for (int j = 0; j <= n; j++) {
            previous[j] = j;
        }

        for (int i = 1; i <= m; i++) {
            current[0] = i;
            char firstChar = first.charAt(i - 1);
            for (int j = 1; j <= n; j++) {
                int substitutionCost = firstChar == second.charAt(j - 1) ? 0 : 1;
                int deletion = previous[j] + 1;
                int insertion = current[j - 1] + 1;
                int substitution = previous[j - 1] + substitutionCost;
                current[j] = Math.min(Math.min(deletion, insertion), substitution);
            }
            int[] temporary = previous;
            previous = current;
            current = temporary;
        }
        return previous[n];
    }
}
