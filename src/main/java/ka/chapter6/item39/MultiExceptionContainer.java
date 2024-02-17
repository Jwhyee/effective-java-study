package ka.chapter6.item39;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MultiExceptionContainer {
    MultiExceptionTestByContainer[] value();
}
