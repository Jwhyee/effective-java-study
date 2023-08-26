#include <jni.h>

JNIEXPORT jint JNICALL Java_NativeClass_calculateSum(JNIEnv *env, jobject obj, jint a, jint b) {
    printf("%d", a);
    return a + b;
}