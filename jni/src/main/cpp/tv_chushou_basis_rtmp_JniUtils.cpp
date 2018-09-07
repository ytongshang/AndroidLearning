#include <jni.h>
#include <string.h>
#include "tv_chushou_basis_rtmp_JniUtils.h"

jstring Java_tv_chushou_basis_rtmp_JniUtils_getStringFromNative(JNIEnv *env, jclass) {
    return env->NewStringUTF("String from native");
}

jint Java_tv_chushou_basis_rtmp_JniUtils_addNum(JNIEnv *env, jobject obj) {
    jclass jclazz = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(jclazz, "num", "I");
    jint num = env->GetIntField(obj, fid);
    return ++num;
}

void Java_tv_chushou_basis_rtmp_JniUtils_setNormalField(JNIEnv *env, jobject obj) {
    jclass jclazz = env->GetObjectClass(obj);
    jfieldID fid = env->GetFieldID(jclazz, "hello", "Ljava/lang/String");
    jstring str = static_cast<jstring>(env->GetStaticObjectField(jclazz, fid));
    const char *cstr = env->GetStringUTFChars(str, JNI_FALSE);
    char newCstr[30] = "hello ";
    strcat(newCstr, cstr);
    jstring newjstring = env->NewStringUTF(newCstr);
    env->SetObjectField(obj, fid, newjstring);
}

void Java_tv_chushou_basis_rtmp_JniUtils_setStaticField(JNIEnv *env, jclass jclazz) {
    jfieldID fid = env->GetStaticFieldID(jclazz, "staticStr", "Ljava/lang/String");
    jstring jstr = static_cast<jstring>(env->GetStaticObjectField(jclazz, fid));
    const char *cstr = env->GetStringUTFChars(jstr, JNI_FALSE);
    char newCstr[30] = "hello ";
    strcat(newCstr, cstr);
    jstring newjstring = env->NewStringUTF(newCstr);
    env->SetStaticObjectField(jclazz, fid, newjstring);
}

