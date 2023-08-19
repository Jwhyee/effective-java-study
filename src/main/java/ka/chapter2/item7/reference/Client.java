package ka.chapter2.item7.reference;

import java.lang.ref.WeakReference;

public class Client {
    private WeakReference<CallBack> callBackRef;

    public void registerCallback(CallBack callBack) {
        callBackRef = new WeakReference<>(callBack);
    }

    public void performTask() {
        if (callBackRef != null && callBackRef.get() != null) {
            CallBack callBack = callBackRef.get();
            callBack.onEvent("Task complete");
        } else {
            System.out.println("Callback not available");
        }
    }
}
