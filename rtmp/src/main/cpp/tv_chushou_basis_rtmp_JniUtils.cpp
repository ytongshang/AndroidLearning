#include <jni.h>
#include "tv_chushou_basis_rtmp_JniUtils.h"

JNIEXPORT jstring JNICALL Java_tv_chushou_basis_rtmp_JniUtils_getStringFromNative
        (JNIEnv *env, jclass) {
    return env->NewStringUTF("String from Jni");
}