cmake_minimum_required(VERSION 3.16 FATAL_ERROR)
project(io-type-blas C CXX)

###########################
# CPUの設定
###########################
set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/cpu)
set(HEADER_FILES
        ${SRC_DIR}/collection_fun.h
        ${SRC_DIR}/mat_mul_fun.h
        ${SRC_DIR}/math_fun.h
        ${SRC_DIR}/operation_fun.h
        ${SRC_DIR}/transpose_fun.h
)
set(SOURCE_FILES
        ${SRC_DIR}/collection_fun.cpp
        ${SRC_DIR}/mat_mul_fun.cpp
        ${SRC_DIR}/math_fun.cpp
        ${SRC_DIR}/operation_fun.cpp
        ${SRC_DIR}/transpose_fun.cpp
        ${SRC_DIR}/jni/com_wsr_cpu_JCollection.cpp
        ${SRC_DIR}/jni/com_wsr_cpu_JMath.cpp
        ${SRC_DIR}/jni/com_wsr_cpu_JMatMul.cpp
        ${SRC_DIR}/jni/com_wsr_cpu_JOperation.cpp
        ${SRC_DIR}/jni/com_wsr_cpu_JTranspose.cpp
)
add_library(CPU SHARED ${HEADER_FILES} ${SOURCE_FILES})
target_include_directories(CPU PRIVATE ${SRC_DIR})

# コンパイラの設定
target_compile_features(CPU PRIVATE cxx_std_17)
set_target_properties(CPU PROPERTIES OUTPUT_NAME "cpu")
if (MSVC)
    target_compile_options(CPU PRIVATE
            /O2                # 標準最適化
            /Ob3               # 積極的インライン展開
            /fp:fast           # 高速浮動小数点演算
            /GL                # リンク時コード生成
            /Oi                # 組み込み関数使用
            /Ot                # 速度優先
            /arch:AVX2         # AVX2命令セットを有効化
    )
    set(OPENBLAS_TARGET "-DTARGET=HASWELL")
elseif (APPLE)
    target_compile_options(CPU PRIVATE
            -O3                # 高度な最適化
            -ffast-math        # 高速浮動小数点演算
            -flto              # リンク時最適化
            -funroll-loops     # ループ展開
            -fomit-frame-pointer # フレームポインタ省略
    )
    set(OPENBLAS_TARGET "-DTARGET=ARMV8")
else()
    target_compile_options(CPU PRIVATE
            -O3                # 高度な最適化
            -ffast-math        # 高速浮動小数点演算
            -flto              # リンク時最適化
            -funroll-loops     # ループ展開
            -fomit-frame-pointer # フレームポインタ省略
            -mavx2            # AVX2命令セットを有効化
    )
    set(OPENBLAS_TARGET "-DTARGET=HASWELL")
endif()



###########################
# JNI
###########################
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})



###########################
# OpenBLAS
###########################
include(ExternalProject)

set(OPENBLAS_PREFIX ${CMAKE_CURRENT_BINARY_DIR}/openblas)
set(OPENBLAS_INSTALL_DIR ${OPENBLAS_PREFIX}/install)
set(OPENBLAS_INCLUDE_DIR ${OPENBLAS_INSTALL_DIR}/include/openblas)

ExternalProject_Add(
        OpenBLAS
        PREFIX ${OPENBLAS_PREFIX}
        GIT_REPOSITORY https://github.com/xianyi/OpenBLAS.git
        GIT_TAG v0.3.27
        GIT_SHALLOW TRUE
        CMAKE_ARGS
        -DCMAKE_INSTALL_PREFIX=${OPENBLAS_INSTALL_DIR}
        -DCMAKE_BUILD_TYPE=Release
        -DBUILD_SHARED_LIBS=OFF
        -DBUILD_TESTING=OFF
        -DBUILD_WITHOUT_LAPACK=OFF
        -DC_LAPACK=ON
        -DUSE_THREAD=ON
        ${OPENBLAS_TARGET}
        -DCMAKE_POLICY_VERSION_MINIMUM=3.5
        ${OPENBLAS_CMAKE_ARGS}
        BUILD_COMMAND ${CMAKE_COMMAND} --build . --config Release
        INSTALL_COMMAND ${CMAKE_COMMAND} --install . --config Release
        LOG_DOWNLOAD ON
        LOG_CONFIGURE ON
        LOG_BUILD ON
)

if(WIN32)
    set(OPENBLAS_LIB ${OPENBLAS_INSTALL_DIR}/lib/openblas.lib)
else()
    set(OPENBLAS_LIB ${OPENBLAS_INSTALL_DIR}/lib/libopenblas.a)
endif()

add_dependencies(CPU OpenBLAS)
target_include_directories(CPU PRIVATE
        ${OPENBLAS_INCLUDE_DIR}
        ${OPENBLAS_INSTALL_DIR}/include
)
target_link_libraries(CPU PRIVATE ${OPENBLAS_LIB})



###########################
# OpenMP
###########################
find_package(OpenMP)

if(OpenMP_CXX_FOUND)
    target_link_libraries(CPU PRIVATE ${OpenMP_CXX_LIBRARIES})
    target_include_directories(CPU PRIVATE ${OpenMP_CXX_INCLUDE_DIRS})
    if (APPLE)
        target_compile_options(CPU PRIVATE  -Xpreprocessor -fopenmp)
    elseif (WIN32 AND MSVC)
         target_compile_options(CPU PRIVATE /openmp:experimental)
    else()
        target_compile_options(CPU PRIVATE ${OpenMP_CXX_FLAGS})
    endif()
    message(STATUS "OpenMP support enabled")
else()
    message(WARNING "OpenMP support disabled")
endif()
