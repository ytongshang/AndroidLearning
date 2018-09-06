cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             jniTest

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/tv_chushou_basis_rtmp_JniUtils.cpp)