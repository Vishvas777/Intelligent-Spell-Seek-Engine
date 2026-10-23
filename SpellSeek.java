import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Dependency-free regression tests for Spellseek.java.
 * Requires Java 8 or newer. No JUnit required
 */
public final class SpellseekTest {
    private static int passed;
    private static int failed;

    private SpellseekTest() {}

    public static void main(String[] args) throws Exception {
        Path fixtures = Paths.get(
            args.length > 0 ? args[0] : "tests/fixtures"
        );

        testNormalizationAndValidation();
        testLevenshteinDistance();
        testTranspositionAwareDistance();
        testDictionaryLoadingAndDeduplication(
            fixtures.resolve("dictionary-basic.txt")
        );
        testSuggestionAndTransposition(
            fixtures.resolve("dictionary-basic.txt")
        );
        testSuggestionRanking(
            fixtures.resolve("dictionary-ranking.txt")
        );
        testSuggestionLimit(
            fixtures.resolve("dictionary-basic.txt")
        );
        testContextRules(
            fixtures.resolve("dictionary-context.txt")
        );

        System.out.println();
        System.out.println(
            "Results: " + passed + " passed, " +
            failed + " failed."
        );

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void testNormalizationAndValidation()
            throws Exception {
        Class<?> type = Spellseek.class;

        Method normalize = method(
            type, "normalizeWord", String.class
        );
        Method valid = method(
            type, "isValidWord", String.class
        );

        equal("normalizes case and whitespace",
            "hello", normalize.invoke(null, "  HeLLo  "));

        equal("normalizes curly apostrophe",
            "can't", normalize.invoke(null, "CAN’T"));

        equal("null normalizes to empty",
            "", normalize.invoke(null, new Object[] { null }));

        equal("accepts letters",
            true, valid.invoke(null, "dictionary"));

        equal("accepts internal apostrophe",
            true, valid.invoke(null, "can't"));

        equal("rejects hyphenated token",
            false, valid.invoke(null, "bk-tree"));

        equal("rejects empty token",
            false, valid.invoke(null, ""));
    }

    private static void testLevenshteinDistance()
            throws Exception {
        Method distance = method(
            Spellseek.class, "levenshtein",
            String.class, String.class
        );

        equal("same word distance", 0,
            distance.invoke(null, "spell", "spell"));

        equal("single substitution", 1,
            distance.invoke(null, "cat", "cut"));

        equal("single insertion", 1,
            distance.invoke(null, "cat", "cart"));

        equal("single deletion", 1,
            distance.invoke(null, "cart", "cat"));

        equal("empty string distance", 4,
            distance.invoke(null, "", "word"));

        equal("distance is symmetric",
            distance.invoke(null, "spelling", "selling"),
            distance.invoke(null, "selling", "spelling"));
    }

    private static void testTranspositionAwareDistance()
            throws Exception {
        Method distance = method(
            Spellseek.class,
            "optimalStringAlignmentDistance",
            String.class, String.class
        );

        equal("adjacent transposition costs one", 1,
            distance.invoke(null, "recieve", "receive"));

        equal("ordinary substitution costs one", 1,
            distance.invoke(null, "cat", "cut"));

        equal("empty to word distance", 4,
            distance.invoke(null, "", "word"));
    }

    private static void testDictionaryLoadingAndDeduplication(
            Path fixture) throws Exception {
        Object app = new Spellseek();

        int loaded = (Integer) method(
            Spellseek.class, "loadDictionary", Path.class
        ).invoke(app, fixture);

        Map<?, ?> dictionary = dictionary(app);

        equal("fixture accepted line count", 8, loaded);
        equal("duplicate words indexed once", 7,
            dictionary.size());

        equal("word lookup is case-normalized", true,
            dictionary.containsKey("hello"));

        equal("invalid hyphenated word skipped", false,
            dictionary.containsKey("bad-word"));

        Object hello = dictionary.get("hello");

        equal("duplicate keeps highest frequency", 15L,
            field(hello, "frequency"));

        equal("apostrophe word indexed", true,
            dictionary.containsKey("can't"));
    }

    private static void testSuggestionAndTransposition(
            Path fixture) throws Exception {
        Object app = new Spellseek();

        method(
            Spellseek.class, "loadDictionary", Path.class
        ).invoke(app, fixture);

        List<?> results = suggestions(app, "recieve");

        equal("transposition suggestion found",
            "receive", suggestionWord(results.get(0)));

        equal("transposition suggestion distance", 1,
            field(results.get(0), "distance"));

        equal("empty query gives no suggestions", 0,
            suggestions(app, "").size());
    }

    private static void testSuggestionRanking(Path fixture)
            throws Exception {
        Object app = new Spellseek();

        method(
            Spellseek.class, "loadDictionary", Path.class
        ).invoke(app, fixture);

        List<?> results = suggestions(app, "dat");

        equal("ranked candidate count", 3, results.size());

        equal("highest frequency ranks first",
            "bat", suggestionWord(results.get(0)));

        equal("second frequency ranks second",
            "hat", suggestionWord(results.get(1)));

        equal("lowest frequency ranks third",
            "cat", suggestionWord(results.get(2)));
    }

    private static void testSuggestionLimit(Path fixture)
            throws Exception {
        Object app = new Spellseek();

        method(
            Spellseek.class, "loadDictionary", Path.class
        ).invoke(app, fixture);

        List<?> results = suggestions(app, "speling");

        check("suggestion list never exceeds five",
            results.size() <= 5);
    }

    private static void testContextRules(Path fixture)
            throws Exception {
        Object app = new Spellseek();

        method(
            Spellseek.class, "loadDictionary", Path.class
        ).invoke(app, fixture);

        Method correction = method(
            Spellseek.class,
            "contextualCorrection",
            String.class, String.class, int.class, int.class
        );

        String sentence = "How are you dong?";
        int start = sentence.indexOf("dong");

        equal("context corrects dong to doing",
            "doing",
            correction.invoke(
                app, "dong", sentence, start, start + 4
            ));

        sentence = "Their going home.";
        start = sentence.indexOf("Their");

        equal("context corrects their to they're",
            "they're",
            correction.invoke(
                app, "their", sentence, start, start + 5
            ));

        sentence = "Their house is large.";
        start = sentence.indexOf("Their");

        equal("avoids possessive false positive",
            null,
            correction.invoke(
                app, "their", sentence, start, start + 5
            ));

        sentence = "This is better then that.";
        start = sentence.indexOf("then");

        equal("comparative context corrects then",
            "than",
            correction.invoke(
                app, "then", sentence, start, start + 4
            ));
    }

    private static List<?> suggestions(
            Object app, String query) throws Exception {
        return (List<?>) method(
            Spellseek.class, "suggest", String.class
        ).invoke(app, query);
    }

    private static String suggestionWord(Object suggestion)
            throws Exception {
        return (String) field(suggestion, "word");
    }

    private static Map<?, ?> dictionary(Object app)
            throws Exception {
        Field field = Spellseek.class.getDeclaredField("dictionary");
        field.setAccessible(true);
        return (Map<?, ?>) field.get(app);
    }

    private static Method method(
            Class<?> type, String name,
            Class<?>... parameterTypes) throws Exception {
        Method result = type.getDeclaredMethod(
            name, parameterTypes
        );
        result.setAccessible(true);
        return result;
    }

    private static Object field(
            Object target, String name) throws Exception {
        Field result = target.getClass().getDeclaredField(name);
        result.setAccessible(true);
        return result.get(target);
    }

    private static void equal(
            String label, Object expected, Object actual) {
        if (expected == null
                ? actual == null
                : expected.equals(actual)) {
            passed++;
            System.out.println("PASS: " + label);
        } else {
            failed++;
            System.out.println(
                "FAIL: " + label +
                " (expected=" + expected +
                ", actual=" + actual + ")"
            );
        }
    }

    private static void check(String label, boolean condition) {
        equal(label, true, condition);
    }
}
