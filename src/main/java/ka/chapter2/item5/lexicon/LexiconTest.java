package ka.chapter2.item5.lexicon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LexiconTest {

    @Test
    void lexiconTest1() {
//        assertTrue(SpellChecker.INSTANCE.isValid("apple"));
    }

    @Test
    void koreanLexiconTest() {
        SpellChecker checker = new SpellChecker(new KoreanLexicon());
        assertTrue(checker.isValid("사과"));
    }

    @Test
    void englishLexiconTest() {
        SpellChecker checker = new SpellChecker(new EnglishLexicon());
        assertTrue(checker.isValid("Apple"));
    }
}
