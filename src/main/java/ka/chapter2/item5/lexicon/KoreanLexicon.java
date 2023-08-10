package ka.chapter2.item5.lexicon;

import java.util.ArrayList;
import java.util.List;

public class KoreanLexicon implements Lexicon {

    private final List<String> words = new ArrayList<>();

    public KoreanLexicon() {
        words.add("사과");
        words.add("바나나");
        words.add("체리");
    }

    @Override
    public boolean isContainsWord(String word) {
        return words.contains(word);
    }
}
