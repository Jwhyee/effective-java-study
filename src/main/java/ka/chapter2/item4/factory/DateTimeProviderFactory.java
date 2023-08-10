package ka.chapter2.item4.factory;

public class DateTimeProviderFactory {
    private static final DateTimeProvider INSTANCE = new DateTimeProviderImpl();

    public static DateTimeProvider getInstance() {
        return INSTANCE;
    }
}
