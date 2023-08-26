package ka.chapter2.item8.closeable;

import ka.chapter2.item8.finalizer.Util;

public class Obj implements AutoCloseable{
    private final int id;
    private boolean isClose;

    public Obj(int id) {
        this.id = id;
        isClose = false;
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 생성");
    }

    @Override
    public void close() throws Exception {
        System.out.println(Thread.currentThread() + " - " + id + "번 객체 소멸");
        isClose = true;
        Util.cnt++;
    }

    public boolean isClose() {
        if(isClose) {
            throw new IllegalStateException("소멸된 객체입니다.");
        }
        return false;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}