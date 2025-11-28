cmake_minimum_required(VERSION 3.16 FATAL_ERROR)
project(io-type-blas C CXX)

# ソースディレクトリを設定
set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/open)

# JNIのセットアップ
if(ANDROID)
    # Android: NDKのJNIヘッダーを使用
    include_directories(${CMAKE_SYSROOT}/usr/include)
    include_directories(${CMAKE_SYSROOT}/usr/include/${CMAKE_ANDROID_ARCH_TRIPLE})
else()
    # Desktop: システムのJDKを使用
    find_package(JNI REQUIRED)
    include_directories(${JNI_INCLUDE_DIRS})
endif()

# OpenBLASのセットアップ
include(ExternalProject)

# OpenBLASのインストール先
set(OPENBLAS_PREFIX ${CMAKE_CURRENT_BINARY_DIR}/openblas)
set(OPENBLAS_INSTALL_DIR ${OPENBLAS_PREFIX}/install)

# Android向けOpenBLASビルドオプション
if(ANDROID)
    set(OPENBLAS_ANDROID_ARGS
        -DCMAKE_TOOLCHAIN_FILE=${CMAKE_TOOLCHAIN_FILE}
        -DANDROID_ABI=${ANDROID_ABI}
        -DANDROID_PLATFORM=${ANDROID_PLATFORM}
        -DNOFORTRAN=1
        -DBUILD_WITHOUT_LAPACK=ON
        -DC_LAPACK=OFF
    )
else()
    set(OPENBLAS_ANDROID_ARGS
        -DBUILD_WITHOUT_LAPACK=OFF
        -DC_LAPACK=ON
    )
endif()

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
        -DUSE_THREAD=ON
        -DUSE_OPENMP=OFF
        -DCMAKE_POLICY_VERSION_MINIMUM=3.5
        ${OPENBLAS_ANDROID_ARGS}
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

add_library(JOpenBLAS SHARED ${SRC_DIR}/com_wsr_JOpenBLAS.cpp)
add_dependencies(JOpenBLAS OpenBLAS)

target_include_directories(JOpenBLAS PRIVATE
        ${OPENBLAS_INCLUDE_DIR}
        ${OPENBLAS_INSTALL_DIR}/include
)

# OpenBLASをリンク
target_link_libraries(JOpenBLAS PRIVATE ${OPENBLAS_LIB})
if(ANDROID)
    # Android: logライブラリのみ必要（pthreadはBionicに含まれる）
    target_link_libraries(JOpenBLAS PRIVATE log m)
elseif(UNIX AND NOT APPLE)
    # Linux: pthreadとgfortranが必要
    target_link_libraries(JOpenBLAS PRIVATE pthread m gfortran)
elseif(APPLE)
    # macOS
    target_link_libraries(JOpenBLAS PRIVATE pthread m)
elseif(WIN32)
    # Windows: 追加のライブラリは不要（OpenBLASが静的リンク）
endif()

# ライブラリ名とサフィックスを設定
set_target_properties(JOpenBLAS PROPERTIES OUTPUT_NAME "open_blas")
