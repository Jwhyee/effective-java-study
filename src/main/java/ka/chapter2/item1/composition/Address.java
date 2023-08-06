package ka.chapter2.item1.composition;

public final class Address {
    private final String city;
    private final String code;

    private Address(String city, String code) {
        this.city = city;
        this.code = code;
    }

    public static Address newInstance(String city, String code) {
        return new Address(city, code);
    }

    public String getCity() {
        return city;
    }

    public String getCode() {
        return code;
    }
}
