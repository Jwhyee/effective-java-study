package ka.chapter2.item5.lexicon;

import java.util.ArrayList;
import java.util.List;

public class EnglishLexicon implements Lexicon {

    private final List<String> words = new ArrayList<>();

    public EnglishLexicon() {
        words.add("apple");
        words.add("banana");
        words.add("cherry");
    }

    @Override
    public boolean isContainsWord(String word) {
        return words.contains(word.toLowerCase());
    }
}
