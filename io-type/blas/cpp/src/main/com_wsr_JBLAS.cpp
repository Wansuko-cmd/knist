#include "com_wsr_JBLAS.h"
#include <stdio.h>
#include <cblas.h>

JNIEXPORT jfloat JNICALL Java_com_wsr_JBLAS_sdot
        (JNIEnv *env, jobject, jint n, jfloatArray x, jint incx, jfloatArray y, jint incy) {
    // Get array elements from Java
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // Call OpenBLAS sdot
    jfloat result = cblas_sdot(n, x_ptr, incx, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, JNI_ABORT);

    return result;
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_sscal
    (JNIEnv *env, jobject, jint n, jfloat alpha, jfloatArray x, jint incx) {
    // Get array elements from Java
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);

    // Call OpenBLAS sscal
    cblas_sscal(n, alpha, x_ptr, incx);

    // Release array back to Java (mode 0 = copy back and free)
    env->ReleaseFloatArrayElements(x, x_ptr, 0);
    }

    JNIEXPORT void JNICALL Java_com_wsr_JBLAS_saxpy
    (JNIEnv *env, jobject, jint n, jfloat alpha, jfloatArray x, jint incx, jfloatArray y, jint incy) {
    // Get array elements from Java
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // Call OpenBLAS saxpy
    cblas_saxpy(n, alpha, x_ptr, incx, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_sgemm(JNIEnv *env, jobject, jboolean transA, jboolean transB, jint m, jint n, jint k,
        jfloat alpha, jfloatArray a, jint lda, jfloatArray b, jint ldb,
jfloat beta, jfloatArray c, jint ldc
) {
    // Get array elements from Java
    jfloat *a_ptr = env->GetFloatArrayElements(a, nullptr);
    jfloat *b_ptr = env->GetFloatArrayElements(b, nullptr);
    jfloat *c_ptr = env->GetFloatArrayElements(c, nullptr);

    // Convert transpose flags
    CBLAS_TRANSPOSE transA_blas = transA ? CblasTrans : CblasNoTrans;
    CBLAS_TRANSPOSE transB_blas = transB ? CblasTrans : CblasNoTrans;

    // Call OpenBLAS sgemm with row-major order
    cblas_sgemm(CblasRowMajor, transA_blas, transB_blas, m, n, k,
            alpha, a_ptr, lda, b_ptr, ldb, beta, c_ptr, ldc);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(b, b_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(c, c_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_sgemv
(JNIEnv *env, jobject, jboolean trans, jint m, jint n, jfloat alpha,
jfloatArray a, jint lda, jfloatArray x, jint incx,
jfloat beta, jfloatArray y, jint incy) {
    // Get array elements from Java
    jfloat *a_ptr = env->GetFloatArrayElements(a, nullptr);
    jfloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jfloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // Convert transpose flag
    CBLAS_TRANSPOSE trans_blas = trans ? CblasTrans : CblasNoTrans;

    // Call OpenBLAS sgemv with row-major order
    cblas_sgemv(CblasRowMajor, trans_blas, m, n, alpha, a_ptr, lda, x_ptr, incx, beta, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, 0);
}
