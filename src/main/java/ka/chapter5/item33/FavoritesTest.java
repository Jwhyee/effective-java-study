package ka.chapter5.item33;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoritesTest {

    private final Favorites f = new Favorites();

    @Test
    @DisplayName("올바르지 않은 인스턴스 대입 테스트")
    void failTest1() {
//        f.putFavorite(String.class, 1);
    }

    @Test
    @DisplayName("이종 컨테이너를 사용하지 않은 테스트")
    void failTest2() {
        f.putFavorite2(String.class, 1);
        System.out.println(f.getFavorite(String.class));
    }

    @Test
    @DisplayName("악의적인 로 타입 인스턴스 테스트")
    void test1() {
        f.putFavorite((Class) Integer.class, "Integer의 인스턴스가 아닙니다.");
        System.out.println(f.getFavorite(Integer.class));
    }

    @Test
    @DisplayName("실체화 불가 타입 저장 테스트")
    void test2() {
        f.putFavorite(List<String>.class, List.of("1", "2", "3"));
//        f.putFavorite(List.class, List.of("1", "2", "3"));
        System.out.println(f.getFavorite(List.class));
    }

    @Test
    @DisplayName("성공 테스트")
    void successTest() {
        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebebe);
        f.putFavorite(Class.class, Favorites.class);

        String favoriteString = f.getFavorite(String.class);
        int favoriteInteger = f.getFavorite(Integer.class);
        Class<?> favoriteClass = f.getFavorite(Class.class);

        System.out.printf("%s %x %s\n", favoriteString, favoriteInteger, favoriteClass.getSimpleName());
    }
}
