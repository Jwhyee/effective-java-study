package ka.chapter5.item33;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Favorites {

    private Map<Class<?>, Object> db = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        db.put(Objects.requireNonNull(type), type.cast(instance));
    }

    public <T> void putFavorite2(T type, T instance) {
        db.put(Objects.requireNonNull(type.getClass()), instance);
    }

    public <T> void putFavorite3(T instance) {
        db.put(Objects.requireNonNull(instance.getClass()), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(db.get(type));
    }
}