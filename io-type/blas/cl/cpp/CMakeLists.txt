cmake_minimum_required(VERSION 3.16 FATAL_ERROR)
project(io-type-blas-cl C CXX)

set(SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src/cl)

if(MSVC)
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreadedDLL")
    add_compile_definitions(NDEBUG)

    foreach(flag_var
            CMAKE_C_FLAGS CMAKE_C_FLAGS_DEBUG CMAKE_C_FLAGS_RELEASE
            CMAKE_CXX_FLAGS CMAKE_CXX_FLAGS_DEBUG CMAKE_CXX_FLAGS_RELEASE)
        if(${flag_var} MATCHES "/MT")
            string(REPLACE "/MT" "/MD" ${flag_var} "${${flag_var}}")
        endif()
        if(${flag_var} MATCHES "/D_DEBUG")
            string(REPLACE "/D_DEBUG" "" ${flag_var} "${${flag_var}}")
        endif()
        # 念押しで追加
        set(${flag_var} "${${flag_var}} /MD")
    endforeach()
endif()

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

if(APPLE)
    find_library(OPENCL_LIBRARY OpenCL REQUIRED)
    set(OPENCL_INCLUDE_DIR "")
else()
    find_package(OpenCL REQUIRED)
    set(OPENCL_LIBRARY OpenCL::OpenCL)
endif()

include(FetchContent)

set(CLBLAST_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(CLBLAST_BUILD_SAMPLES OFF CACHE BOOL "" FORCE)
set(CLBLAST_BUILD_CLIENTS OFF CACHE BOOL "" FORCE)
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)

FetchContent_Declare(
    clblast
    GIT_REPOSITORY https://github.com/CNugteren/CLBlast.git
    GIT_TAG 1.6.3
    GIT_SHALLOW TRUE
)
FetchContent_MakeAvailable(clblast)

add_library(JCLBlast SHARED ${SRC_DIR}/com_wsr_cl_JCLBLast.cpp)

target_link_libraries(JCLBlast PRIVATE clblast)
target_link_libraries(JCLBlast PRIVATE ${OPENCL_LIBRARY})

target_include_directories(JCLBlast PRIVATE ${OPENCL_INCLUDE_DIR})

if(UNIX AND NOT APPLE AND NOT ANDROID)
    target_link_libraries(JCLBlast PRIVATE pthread m)
endif()

set_target_properties(JCLBlast PROPERTIES OUTPUT_NAME "cl_blast")
