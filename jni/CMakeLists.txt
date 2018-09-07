cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             jniTest

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/cradle_rancune_learningandroid_jni_JniUtils.cpp)