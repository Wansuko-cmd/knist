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
        -DUSE_OPENMP=OFF
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

add_library(JOpenBLAS SHARED ${SRC_DIR}/com_wsr_cpu_JOpenBLAS.cpp)
add_dependencies(JOpenBLAS OpenBLAS)

target_include_directories(JOpenBLAS PRIVATE
        ${OPENBLAS_INCLUDE_DIR}
        ${OPENBLAS_INSTALL_DIR}/include
)

# OpenBLASをリンク
target_link_libraries(JOpenBLAS PRIVATE ${OPENBLAS_LIB})
if(UNIX AND NOT APPLE AND NOT ANDROID)
    # Linux: pthreadとgfortranが必要
    target_link_libraries(JOpenBLAS PRIVATE pthread m gfortran)
elseif(APPLE)
    # macOS
    target_link_libraries(JOpenBLAS PRIVATE pthread m)
elseif(WIN32)
    # Windows: 追加のライブラリは不要（OpenBLASが静的リンク）
endif()

# ライブラリ名とサフィックスを設定
set_target_properties(JOpenBLAS PROPERTIES OUTPUT_NAME "cpu")
