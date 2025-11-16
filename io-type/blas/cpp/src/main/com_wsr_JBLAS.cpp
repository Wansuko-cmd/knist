#include "com_wsr_JBLAS.h"
#include <stdio.h>
#include <cblas.h>

JNIEXPORT jFloat JNICALL Java_com_wsr_JBLAS_ddot
        (JNIEnv *env, jobject, jint n, jFloatArray x, jint incx, jFloatArray y, jint incy) {
    // Get array elements from Java
    jFloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jFloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // Call OpenBLAS ddot
    jFloat result = cblas_ddot(n, x_ptr, incx, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, JNI_ABORT);

    return result;
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_dscal
    (JNIEnv *env, jobject, jint n, jFloat alpha, jFloatArray x, jint incx) {
    // Get array elements from Java
    jFloat *x_ptr = env->GetFloatArrayElements(x, nullptr);

    // Call OpenBLAS dscal
    cblas_dscal(n, alpha, x_ptr, incx);

    // Release array back to Java (mode 0 = copy back and free)
    env->ReleaseFloatArrayElements(x, x_ptr, 0);
    }

    JNIEXPORT void JNICALL Java_com_wsr_JBLAS_daxpy
    (JNIEnv *env, jobject, jint n, jFloat alpha, jFloatArray x, jint incx, jFloatArray y, jint incy) {
    // Get array elements from Java
    jFloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jFloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // Call OpenBLAS daxpy
    cblas_daxpy(n, alpha, x_ptr, incx, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_dgemm(JNIEnv *env, jobject, jboolean transA, jboolean transB, jint m, jint n, jint k,
        jFloat alpha, jFloatArray a, jint lda, jFloatArray b, jint ldb,
jFloat beta, jFloatArray c, jint ldc
) {
    // Get array elements from Java
    jFloat *a_ptr = env->GetFloatArrayElements(a, nullptr);
    jFloat *b_ptr = env->GetFloatArrayElements(b, nullptr);
    jFloat *c_ptr = env->GetFloatArrayElements(c, nullptr);

    // Convert transpose flags
    CBLAS_TRANSPOSE transA_blas = transA ? CblasTrans : CblasNoTrans;
    CBLAS_TRANSPOSE transB_blas = transB ? CblasTrans : CblasNoTrans;

    // Call OpenBLAS dgemm with row-major order
    cblas_dgemm(CblasRowMajor, transA_blas, transB_blas, m, n, k,
            alpha, a_ptr, lda, b_ptr, ldb, beta, c_ptr, ldc);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(b, b_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(c, c_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_dgemv
(JNIEnv *env, jobject, jboolean trans, jint m, jint n, jFloat alpha,
jFloatArray a, jint lda, jFloatArray x, jint incx,
jFloat beta, jFloatArray y, jint incy) {
    // Get array elements from Java
    jFloat *a_ptr = env->GetFloatArrayElements(a, nullptr);
    jFloat *x_ptr = env->GetFloatArrayElements(x, nullptr);
    jFloat *y_ptr = env->GetFloatArrayElements(y, nullptr);

    // Convert transpose flag
    CBLAS_TRANSPOSE trans_blas = trans ? CblasTrans : CblasNoTrans;

    // Call OpenBLAS dgemv with row-major order
    cblas_dgemv(CblasRowMajor, trans_blas, m, n, alpha, a_ptr, lda, x_ptr, incx, beta, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseFloatArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(y, y_ptr, 0);
}
