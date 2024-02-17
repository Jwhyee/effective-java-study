package ka.chapter6.item39;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(MultiExceptionContainer.class)
public @interface MultiExceptionTestByContainer {
    Class<? extends Throwable> value();
}
