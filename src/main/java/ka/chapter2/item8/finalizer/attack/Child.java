package ka.chapter2.item8.finalizer.attack;

import java.io.Serializable;

public class Child extends Parent {

    public Child() throws Exception{
    }

    @Override
    protected void finalize() throws Throwable {
        System.out.println("하위 클래스 finalizer 실행");
        System.out.println("rm -rf *");
    }
}
