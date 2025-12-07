#include "com_wsr_cl_JCLBLast.h"
#include <stdio.h>
#include <clblast.h>

#define LOG_DEBUG(msg) { fprintf(stderr, "[JNI] %s (Line: %d)\n", msg, __LINE__); fflush(stderr); }
#define LOG_VAL(msg, val) { fprintf(stderr, "[JNI] %s: %d (Line: %d)\n", msg, val, __LINE__); fflush(stderr); }


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
JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBLast_init
        (JNIEnv *env, jobject) {
    cl_int err;

    // プラットフォームを取得
    err = clGetPlatformIDs(1, &platform, nullptr);
    LOG_VAL("clGetPlatformIDs result", err);
    fprintf(stderr, "start", err);
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
    if (context == nullptr) {
        LOG_DEBUG("Context is NULL!");
    } else {
        LOG_DEBUG("Context created successfully");
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
    if (queue == nullptr) {
        LOG_DEBUG("Queue is NULL!");
    } else {
        LOG_DEBUG("Queue created successfully");
    }

    LOG_DEBUG("init finished");
}

JNIEXPORT jfloat JNICALL Java_com_wsr_cl_JCLBLast_sdot
        (JNIEnv *env, jobject, jint n, jfloatArray x, jint incx, jfloatArray y, jint incy) {
    cl_int err;
     LOG_DEBUG("sdot start");
     if (context == nullptr) LOG_DEBUG("ERROR: context is null in sdot");
     if (queue == nullptr) LOG_DEBUG("ERROR: queue is null in sdot");

    // Get array elements from Java
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    LOG_DEBUG("Creating buffers...");
    // OpenCLバッファを作成
    size_t x_size = (1 + (n - 1) * abs(incx)) * sizeof(cl_float);
    size_t y_size = (1 + (n - 1) * abs(incy)) * sizeof(cl_float);

    cl_mem x_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                     x_size, x_ptr, &err);
    cl_mem y_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                     y_size, y_ptr, &err);
    cl_mem dot_buffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY,
                                       sizeof(cl_float), nullptr, &err);

    LOG_DEBUG("Calling clblast::Dot...");
    // CLBlast sdotを呼び出し
    clblast::StatusCode status = clblast::Dot<float>(
        n,
        dot_buffer, 0,
        x_buffer, 0, incx,
        y_buffer, 0, incy,
        &queue
    );
    LOG_VAL("clblast::Dot status", (int)status);

    jfloat result = 0.0f;
    if (status == clblast::StatusCode::kSuccess) {
        clEnqueueReadBuffer(queue, dot_buffer, CL_TRUE, 0, sizeof(cl_float), &result, 0, nullptr, nullptr);
    } else {
        fprintf(stderr, "CLBlast sdot failed: %d\n", static_cast<int>(status));
    }

    // リソースを解放
    clReleaseMemObject(x_buffer);
    clReleaseMemObject(y_buffer);
    clReleaseMemObject(dot_buffer);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, JNI_ABORT);
    LOG_DEBUG("sdot finished");

    return result;
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBLast_sscal
    (JNIEnv *env, jobject, jint n, jfloat alpha, jfloatArray x, jint incx) {
    cl_int err;

    // Get array elements from Java
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);

    // OpenCLバッファを作成
    size_t x_size = (1 + (n - 1) * abs(incx)) * sizeof(cl_float);
    cl_mem x_buffer = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                                     x_size, x_ptr, &err);

    // CLBlast sscalを呼び出し
    clblast::StatusCode status = clblast::Scal<float>(
        n,
        alpha,
        x_buffer, 0, incx,
        &queue
    );

    if (status == clblast::StatusCode::kSuccess) {
        clEnqueueReadBuffer(queue, x_buffer, CL_TRUE, 0, x_size, x_ptr, 0, nullptr, nullptr);
    } else {
        fprintf(stderr, "CLBlast sscal failed: %d\n", static_cast<int>(status));
    }

    // リソースを解放
    clReleaseMemObject(x_buffer);

    // Release array back to Java (mode 0 = copy back and free)
    env->ReleaseFloatArrayElements(x, x_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBLast_saxpy
    (JNIEnv *env, jobject, jint n, jfloat alpha, jfloatArray x, jint incx, jfloatArray y, jint incy) {
    cl_int err;

    // Get array elements from Java
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // OpenCLバッファを作成
    size_t x_size = (1 + (n - 1) * abs(incx)) * sizeof(cl_float);
    size_t y_size = (1 + (n - 1) * abs(incy)) * sizeof(cl_float);

    cl_mem x_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                     x_size, x_ptr, &err);
    cl_mem y_buffer = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                                     y_size, y_ptr, &err);

    // CLBlast saxpyを呼び出し
    clblast::StatusCode status = clblast::Axpy<float>(
        n,
        alpha,
        x_buffer, 0, incx,
        y_buffer, 0, incy,
        &queue
    );

    if (status == clblast::StatusCode::kSuccess) {
        clEnqueueReadBuffer(queue, y_buffer, CL_TRUE, 0, y_size, y_ptr, 0, nullptr, nullptr);
    } else {
        fprintf(stderr, "CLBlast saxpy failed: %d\n", static_cast<int>(status));
    }

    // リソースを解放
    clReleaseMemObject(x_buffer);
    clReleaseMemObject(y_buffer);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBLast_sgemm(
    JNIEnv *env, jobject, jboolean transA, jboolean transB, jint m, jint n, jint k,
    jfloat alpha, jfloatArray a, jint lda, jfloatArray b, jint ldb,
    jfloat beta, jfloatArray c, jint ldc
) {
    cl_int err;

    // Get array elements from Java
    jfloat *a_ptr = env->GetFloatArrayElements(a, nullptr);
    jfloat *b_ptr = env->GetFloatArrayElements(b, nullptr);
    jfloat *c_ptr = env->GetFloatArrayElements(c, nullptr);

    // Convert transpose flags
    clblast::Transpose transA_cl = transA ? clblast::Transpose::kYes : clblast::Transpose::kNo;
    clblast::Transpose transB_cl = transB ? clblast::Transpose::kYes : clblast::Transpose::kNo;

    // 配列サイズを計算
    size_t a_rows = transA ? k : m;
    size_t a_cols = transA ? m : k;
    size_t b_rows = transB ? n : k;
    size_t b_cols = transB ? k : n;

    size_t a_size = a_rows * lda * sizeof(cl_float);
    size_t b_size = b_rows * ldb * sizeof(cl_float);
    size_t c_size = m * ldc * sizeof(cl_float);

    // OpenCLバッファを作成
    cl_mem a_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                     a_size, a_ptr, &err);
    cl_mem b_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                     b_size, b_ptr, &err);
    cl_mem c_buffer = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                                     c_size, c_ptr, &err);

    // CLBlast sgemmを呼び出し（Row-major order）
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

    if (status == clblast::StatusCode::kSuccess) {
        clEnqueueReadBuffer(queue, c_buffer, CL_TRUE, 0, c_size, c_ptr, 0, nullptr, nullptr);
    } else {
        fprintf(stderr, "CLBlast sgemm failed: %d\n", static_cast<int>(status));
    }

    // リソースを解放
    clReleaseMemObject(a_buffer);
    clReleaseMemObject(b_buffer);
    clReleaseMemObject(c_buffer);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(b, b_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(c, c_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_cl_JCLBLast_sgemv
(JNIEnv *env, jobject, jboolean trans, jint m, jint n, jfloat alpha,
jfloatArray a, jint lda, jfloatArray x, jint incx,
jfloat beta, jfloatArray y, jint incy) {
    cl_int err;

    LOG_DEBUG("sgemv start"); // <--- 追加

    if (context == nullptr) { LOG_DEBUG("ERROR: Context is null in sgemv"); return; }
    if (queue == nullptr) { LOG_DEBUG("ERROR: Queue is null in sgemv"); return; }

    // Get array elements from Java
    jfloat *a_ptr = env->GetFloatArrayElements(a, nullptr);
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // Convert transpose flag
    clblast::Transpose trans_cl = trans ? clblast::Transpose::kYes : clblast::Transpose::kNo;

    // 配列サイズを計算
    size_t a_size = m * lda * sizeof(cl_float);
    size_t x_len = trans ? m : n;
    size_t y_len = trans ? n : m;
    size_t x_size = (1 + (x_len - 1) * abs(incx)) * sizeof(cl_float);
    size_t y_size = (1 + (y_len - 1) * abs(incy)) * sizeof(cl_float);

    LOG_DEBUG("sgemv: Creating buffers...");

    // OpenCLバッファを作成
    cl_mem a_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                     a_size, a_ptr, &err);
    if (err != CL_SUCCESS || a_buffer == nullptr) {
        LOG_VAL("ERROR: Failed to create A buffer! err=", err);
        return; // 即リターン
    }
    cl_mem x_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                                     x_size, x_ptr, &err);
    if (err != CL_SUCCESS || x_buffer == nullptr) {
        LOG_VAL("ERROR: Failed to create X buffer! err=", err);
        clReleaseMemObject(a_buffer);
        return;
    }
    cl_mem y_buffer = clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR,
                                     y_size, y_ptr, &err);
    if (err != CL_SUCCESS || y_buffer == nullptr) {
        LOG_VAL("ERROR: Failed to create Y buffer! err=", err);
        clReleaseMemObject(a_buffer);
        clReleaseMemObject(x_buffer);
        return;
    }

    LOG_DEBUG("sgemv: Buffers created successfully");

    try {
        LOG_DEBUG("sgemv: Calling clblast::Gemv...");

        clblast::StatusCode status = clblast::Gemv<float>(
            clblast::Layout::kRowMajor,
            trans ? clblast::Transpose::kYes : clblast::Transpose::kNo,
            m, n,
            alpha,
            a_buffer, 0, lda,
            x_buffer, 0, incx,
            beta,
            y_buffer, 0, incy,
            &queue
        );

        LOG_VAL("sgemv: clblast status", (int)status);

        if (status != clblast::StatusCode::kSuccess) {
            fprintf(stderr, "[JNI] CLBlast Error: %d\n", (int)status);
        } else {
             clEnqueueReadBuffer(queue, y_buffer, CL_TRUE, 0, y_size, y_ptr, 0, nullptr, nullptr);
        }

    } catch (const std::exception& e) {
        fprintf(stderr, "[JNI] CAUGHT EXCEPTION: %s\n", e.what());
        fflush(stderr);

        jclass exClass = env->FindClass("java/lang/RuntimeException");
        if(exClass) env->ThrowNew(exClass, e.what());
    } catch (...) {
        fprintf(stderr, "[JNI] CAUGHT UNKNOWN EXCEPTION\n");
        fflush(stderr);
    }

    // リソースを解放
    clReleaseMemObject(a_buffer);
    clReleaseMemObject(x_buffer);
    clReleaseMemObject(y_buffer);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, 0);

     LOG_DEBUG("sgemv finished");
}
