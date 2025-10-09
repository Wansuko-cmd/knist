cmake_minimum_required(VERSION 3.16 FATAL_ERROR)
project(io-type-blas C CXX)

# ソースディレクトリを設定
set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/main)

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# ソースファイルをsrc/mainから参照
add_library(JBLAS SHARED ${SRC_DIR}/com_wsr_JBLAS.cpp)

# ライブラリ名とサフィックスを設定
set_target_properties(JBLAS PROPERTIES
        OUTPUT_NAME "${OUTPUT_NAME}"
        PREFIX "lib"
        SUFFIX ".dylib"
)
