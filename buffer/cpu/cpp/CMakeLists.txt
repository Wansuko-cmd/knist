cmake_minimum_required(VERSION 3.16 FATAL_ERROR)
project(io-type-blas C CXX)

# ソースディレクトリを設定
set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/cpu)

# JNIのセットアップ
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# OpenBLASのセットアップ
include(ExternalProject)

# OpenBLASのインストール先
set(OPENBLAS_PREFIX ${CMAKE_CURRENT_BINARY_DIR}/openblas)
set(OPENBLAS_INSTALL_DIR ${OPENBLAS_PREFIX}/install)

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
        -DCMAKE_POLICY_VERSION_MINIMUM=3.5
        ${OPENBLAS_CMAKE_ARGS}
        BUILD_COMMAND ${CMAKE_COMMAND} --build . --config Release
        INSTALL_COMMAND ${CMAKE_COMMAND} --install . --config Release
        LOG_DOWNLOAD ON
        LOG_CONFIGURE ON
        LOG_BUILD ON
)

set(OPENBLAS_INCLUDE_DIR ${OPENBLAS_INSTALL_DIR}/include/openblas)
if(WIN32)
    # Windows (MSVC): openblas.lib
    set(OPENBLAS_LIB ${OPENBLAS_INSTALL_DIR}/lib/openblas.lib)
else()
    # Unix系 (macOS, Linux): libopenblas.a
    set(OPENBLAS_LIB ${OPENBLAS_INSTALL_DIR}/lib/libopenblas.a)
endif()

set(SOURCE_FILES
        ${SRC_DIR}/com_wsr_cpu_JOpenBLAS.cpp
        ${SRC_DIR}/com_wsr_cpu_JTranspose.cpp
)
add_library(JCPU SHARED ${SOURCE_FILES})
add_dependencies(JCPU OpenBLAS)

target_include_directories(JCPU PRIVATE
        ${OPENBLAS_INCLUDE_DIR}
        ${OPENBLAS_INSTALL_DIR}/include
)

# OpenBLASをリンク
target_link_libraries(JCPU PRIVATE ${OPENBLAS_LIB})
if(UNIX AND NOT APPLE AND NOT ANDROID)
    # Linux: pthreadとgfortranが必要
    target_link_libraries(JCPU PRIVATE pthread m gfortran)
elseif(APPLE)
    # macOS
    target_link_libraries(JCPU PRIVATE pthread m)
elseif(WIN32)
    # Windows: 追加のライブラリは不要（OpenBLASが静的リンク）
endif()

# ライブラリ名とサフィックスを設定
set_target_properties(JCPU PROPERTIES OUTPUT_NAME "cpu")
