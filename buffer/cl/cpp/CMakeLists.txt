cmake_minimum_required(VERSION 3.16 FATAL_ERROR)
project(io-type-blas-cl C CXX)

# ソースディレクトリを設定
set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/cl)

# JNIのセットアップ
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

# OpenCLのセットアップ
if(APPLE)
    # macOS: OpenCLはシステムフレームワークとして提供
    find_library(OPENCL_LIBRARY OpenCL REQUIRED)
    set(OPENCL_INCLUDE_DIR "")
else()
    find_package(OpenCL REQUIRED)
    set(OPENCL_LIBRARY OpenCL::OpenCL)
endif()

# CLBlastのセットアップ
include(ExternalProject)

# CLBlastのインストール先
set(CLBLAST_PREFIX ${CMAKE_CURRENT_BINARY_DIR}/clblast)
set(CLBLAST_INSTALL_DIR ${CLBLAST_PREFIX}/install)

# MSVC用の追加引数
if(MSVC)
    set(CLBLAST_MSVC_ARGS
        -DCMAKE_C_FLAGS_RELEASE=/MD
        -DCMAKE_CXX_FLAGS_RELEASE=/MD
    )
else()
    set(CLBLAST_MSVC_ARGS "")
endif()

ExternalProject_Add(
        CLBlast
        PREFIX ${CLBLAST_PREFIX}
        GIT_REPOSITORY https://github.com/CNugteren/CLBlast.git
        GIT_TAG 1.6.3
        GIT_SHALLOW TRUE
        CMAKE_ARGS
        -DCMAKE_INSTALL_PREFIX=${CLBLAST_INSTALL_DIR}
        -DCMAKE_BUILD_TYPE=Release
        -DBUILD_SHARED_LIBS=OFF
        -DSAMPLES=OFF
        -DTUNERS=OFF
        -DCLIENTS=OFF
        -DTESTS=OFF
        -DNETLIB=OFF
        -DCMAKE_POLICY_VERSION_MINIMUM=3.5
        -DCMAKE_POSITION_INDEPENDENT_CODE=ON
        ${CLBLAST_MSVC_ARGS}
        BUILD_COMMAND ${CMAKE_COMMAND} --build . --config Release
        INSTALL_COMMAND ${CMAKE_COMMAND} --install . --config Release
        LOG_DOWNLOAD ON
        LOG_CONFIGURE ON
        LOG_BUILD ON
)

set(CLBLAST_INCLUDE_DIR ${CLBLAST_INSTALL_DIR}/include)
if(WIN32)
    # Windows (MSVC): clblast.lib
    set(CLBLAST_LIB ${CLBLAST_INSTALL_DIR}/lib/clblast.lib)
else()
    # Unix系 (macOS, Linux): libclblast.a
    set(CLBLAST_LIB ${CLBLAST_INSTALL_DIR}/lib/libclblast.a)
endif()

add_library(JCLBlast SHARED ${SRC_DIR}/com_wsr_cl_JCLBlast.cpp)
add_dependencies(JCLBlast CLBlast)

target_include_directories(JCLBlast PRIVATE
        ${CLBLAST_INCLUDE_DIR}
        ${OPENCL_INCLUDE_DIR}
)

# CLBlastとOpenCLをリンク
target_link_libraries(JCLBlast PRIVATE ${CLBLAST_LIB})
target_link_libraries(JCLBlast PRIVATE ${OPENCL_LIBRARY})

if(UNIX AND NOT APPLE AND NOT ANDROID)
    # Linux: 追加のライブラリ
    target_link_libraries(JCLBlast PRIVATE pthread m)
elseif(APPLE)
    # macOS: 追加のライブラリは不要（OpenCLはフレームワーク）
elseif(WIN32)
    # Windows: 追加のライブラリは不要
endif()

# ライブラリ名とサフィックスを設定
set_target_properties(JCLBlast PROPERTIES OUTPUT_NAME "cl_blast")
