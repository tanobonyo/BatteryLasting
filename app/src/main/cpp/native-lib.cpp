#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_btaudio_batterylasting_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Prueba Batería n°1";
    return env->NewStringUTF(hello.c_str());
}
