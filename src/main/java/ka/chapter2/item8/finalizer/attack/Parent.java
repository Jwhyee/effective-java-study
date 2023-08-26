package ka.chapter2.item8.finalizer.attack;

import java.io.Serializable;

public class Parent implements Serializable {
    private boolean isCreated = false;

    public Parent() throws Exception {
        if (!isCreated) {
            isCreated = true;
        } else {
            throw new Exception("부모 클래스 생성자 예외");
        }
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("부모 클래스 finalze 실행");
    }

    private Object readResolve() {
        return this;
    }
}
