//
// Created by Tibi on 24-Aug-20.
//

#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_tiberiugaspar_mylauncher_news_NewsFragment_getNativeNewsApiKey(JNIEnv *env, jobject instance) {

 return (*env)->  NewStringUTF(env, "450215834b3e476dad2a443d88ffeb2d");
}
