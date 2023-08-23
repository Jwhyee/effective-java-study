package ka.chapter2.item8.cleaner;

import java.lang.ref.Cleaner;

public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // 청소가 필요한 자원 : Room에 있는 쓰레기를 생성자를 통해 넘겨 받음
    // Room을 참조할 경우 순환참조 발생!
    private static class State implements Runnable {
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        // cleanable.clean()이 실행되면 run() 메소드 실행
        @Override public void run() {
            System.out.println("방 청소");
            numJunkPiles = 0;
        }
    }

    private final State state;

    // cleanable 객체, 수거 대상이 되면 방을 청소함
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }

    // 객체가 소멸될 때, 실행되는 메소드
    @Override public void close() {
        // cleanable에 청소를 실행할 객체(Runnable 구현체)로 state를 지정함
        // 때문에 clean()이 실행되면 State 클래스 내부 run() 메소드 실행
        cleanable.clean();
    }
}
