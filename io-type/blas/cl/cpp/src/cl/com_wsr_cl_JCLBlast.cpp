#include "com_wsr_cl_JCLBlast.h"
#include <stdio.h>
#include <clblast.h>

#ifdef __APPLE__
#include <OpenCL/opencl.h>
#else
#include <CL/cl.h>
#endif

// グローバルなOpenCLリソース
static cl_platform_id platform = nullptr;
static cl_device_id device = nullptr;
static cl_context context = nullptr;
static cl_command_queue queue = nullptr;

// Java側から呼び出される初期化関数
JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBlast_init
        (JNIEnv *env, jobject) {
    cl_int err;

    // プラットフォームを取得
    err = clGetPlatformIDs(1, &platform, nullptr);
    if (err != CL_SUCCESS) {
        fprintf(stderr, "Failed to get OpenCL platform: %d\n", err);
        return;
    }

    // デバイスを取得（GPUを優先、なければCPU）
    err = clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr);
    if (err != CL_SUCCESS) {
        err = clGetDeviceIDs(platform, CL_DEVICE_TYPE_CPU, 1, &device, nullptr);
        if (err != CL_SUCCESS) {
            fprintf(stderr, "Failed to get OpenCL device: %d\n", err);
            return;
        }
    }

    // コンテキストを作成
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &err);
    if (err != CL_SUCCESS) {
        fprintf(stderr, "Failed to create OpenCL context: %d\n", err);
        return;
    }

    // コマンドキューを作成
#ifdef CL_VERSION_2_0
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &err);
#else
    queue = clCreateCommandQueue(context, device, 0, &err);
#endif
    if (err != CL_SUCCESS) {
        fprintf(stderr, "Failed to create OpenCL command queue: %d\n", err);
        clReleaseContext(context);
        context = nullptr;
        return;
    }
}

JNIEXPORT jlong JNICALL Java_com_wsr_cl_JCLBlast_transfer
        (JNIEnv *env, jobject, jfloatArray data, jint size) {
    cl_int err;
    cl_mem buffer = nullptr;

    // 渡された配列のポインタを取得
    jfloat *ptr = env->GetFloatArrayElements(data, nullptr);
    size_t byte_size = size * sizeof(cl_float);

    // GPUに転送
    buffer = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, byte_size, ptr, &err);

    if (err != CL_SUCCESS) {
        fprintf(stderr, "Failed to create buffer: %d\n", err);
    }

    // 配列の解放
    env->ReleaseFloatArrayElements(data, ptr, JNI_ABORT);

    return (jlong)buffer;
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBlast_read
        (JNIEnv *env, jobject, jlong address, jfloatArray destination) {
    cl_int err;
    cl_mem buffer = (cl_mem)address;

    // 渡された配列のポインタを取得
    jfloat *ptr = env->GetFloatArrayElements(destination, nullptr);

    // 配列の長さを取得
    jsize length = env->GetArrayLength(destination);
    size_t byte_size = length * sizeof(cl_float);

    // GPU -> CPU データ転送
    err = clEnqueueReadBuffer(queue, buffer, CL_TRUE, 0, byte_size, ptr, 0, nullptr, nullptr);

    if (err != CL_SUCCESS) {
        fprintf(stderr, "Error reading buffer: %d\n", err);
    }

    // データをJava配列に反映して解放
    env->ReleaseFloatArrayElements(destination, ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBlast_release
        (JNIEnv *env, jobject, jlong address) {
    cl_mem buffer = (cl_mem)address;
    cl_int err = clReleaseMemObject(buffer);
    if (err != CL_SUCCESS) {
        fprintf(stderr, "Failed to release memory: %d\n", err);
    }
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cl_JCLBlast_sdot
         (JNIEnv *env, jobject, jint n, jlong x_ptr, jint incx, jlong y_ptr, jint incy) {
    cl_int err;
    cl_mem x_buffer = (cl_mem)x_ptr;
    cl_mem y_buffer = (cl_mem)y_ptr;

    // 結果用のバッファ確保
    cl_mem dot_buffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY, sizeof(cl_float), nullptr, &err);
    if (err != CL_SUCCESS) {
        fprintf(stderr, "Failed to create result buffer for sdot: %d\n", err);
        return 0.0f;
    }

    // CLBlast呼び出し
    clblast::StatusCode status = clblast::Dot<float>(
        n,
        dot_buffer, 0,
        x_buffer, 0, incx,
        y_buffer, 0, incy,
        &queue
    );

    jfloat result = 0.0f;
    if (status == clblast::StatusCode::kSuccess) {
        err = clEnqueueReadBuffer(queue, dot_buffer, CL_TRUE, 0, sizeof(cl_float), &result, 0, nullptr, nullptr);
        if (err != CL_SUCCESS) {
             fprintf(stderr, "Failed to read sdot result: %d\n", err);
        }
    } else {
        fprintf(stderr, "CLBlast sdot failed: %d\n", static_cast<int>(status));
    }

    // 一時バッファを解放
    clReleaseMemObject(dot_buffer);

    return result;
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBlast_sscal
        (JNIEnv *env, jobject, jint n, jfloat alpha, jlong x_ptr, jint incx) {
    cl_mem x_buffer = (cl_mem)x_ptr;

    clblast::StatusCode status = clblast::Scal<float>(
        n,
        alpha,
        x_buffer,
        0,
        incx,
        &queue
    );

    if (status != clblast::StatusCode::kSuccess) {
        fprintf(stderr, "CLBlast sscal failed: %d\n", static_cast<int>(status));
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBlast_saxpy(
    JNIEnv *env, jobject, jint n, jfloat alpha,
    jlong x_ptr, jint incx, jlong y_ptr, jint incy
) {
    cl_mem x_buffer = (cl_mem)x_ptr;
    cl_mem y_buffer = (cl_mem)y_ptr;

    clblast::StatusCode status = clblast::Axpy<float>(
        n,
        alpha,
        x_buffer, 0, incx,
        y_buffer, 0, incy,
        &queue
    );

    if (status != clblast::StatusCode::kSuccess) {
        fprintf(stderr, "CLBlast saxpy failed: %d\n", static_cast<int>(status));
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBlast_sgemm(
    JNIEnv *env, jobject,
    jboolean transA, jboolean transB,
    jint m, jint n, jint k,
    jfloat alpha, jlong a_ptr, jint lda, jlong b_ptr, jint ldb,
    jfloat beta, jlong c_ptr, jint ldc
) {
    cl_mem a_buffer = (cl_mem)a_ptr;
    cl_mem b_buffer = (cl_mem)b_ptr;
    cl_mem c_buffer = (cl_mem)c_ptr;

    // 転送フラグの変換
    auto transA_cl = transA ? clblast::Transpose::kYes : clblast::Transpose::kNo;
    auto transB_cl = transB ? clblast::Transpose::kYes : clblast::Transpose::kNo;

    clblast::StatusCode status = clblast::Gemm<float>(
        clblast::Layout::kRowMajor,
        transA_cl, transB_cl,
        m, n, k,
        alpha,
        a_buffer, 0, lda,
        b_buffer, 0, ldb,
        beta,
        c_buffer, 0, ldc,
        &queue
    );

    if (status != clblast::StatusCode::kSuccess) {
        fprintf(stderr, "CLBlast sgemm failed: %d\n", static_cast<int>(status));
    }
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBlast_sgemv(
    JNIEnv *env, jobject,
    jboolean trans,
    jint m, jint n,
    jfloat alpha, jlong a_ptr, jint lda, jlong x_ptr, jint incx,
    jfloat beta, jlong y_ptr, jint incy
) {
    cl_mem a_buffer = (cl_mem)a_ptr;
    cl_mem x_buffer = (cl_mem)x_ptr;
    cl_mem y_buffer = (cl_mem)y_ptr;

    auto trans_cl = trans ? clblast::Transpose::kYes : clblast::Transpose::kNo;

    clblast::StatusCode status = clblast::Gemv<float>(
        clblast::Layout::kRowMajor,
        trans_cl,
        m, n,
        alpha,
        a_buffer, 0, lda,
        x_buffer, 0, incx,
        beta,
        y_buffer, 0, incy,
        &queue
    );

    if (status != clblast::StatusCode::kSuccess) {
        fprintf(stderr, "CLBlast sgemv failed: %d\n", static_cast<int>(status));
    }
}
