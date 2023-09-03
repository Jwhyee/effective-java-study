package ka.chapter3.item10.impl.autovalue;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Person {
    public abstract String name();
    public abstract int age();

    /*public static Person create(String name, int age) {
        return new AutoValue_Person(name, age);
    }*/
}
