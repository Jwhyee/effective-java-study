package ka.chapter2.item8.native_lib;

public class NativeClass {
    public native int calculateSum(int a, int b);

    static {
        System.loadLibrary("NativeLibrary");
    }
}
