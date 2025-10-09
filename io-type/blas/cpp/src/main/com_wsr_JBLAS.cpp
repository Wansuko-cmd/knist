#include "com_wsr_JBLAS.h"
#include <stdio.h>
#include <cblas.h>

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_dgemm(JNIEnv *env, jobject, jboolean transA, jboolean transB, jint m, jint n, jint k,
        jdouble alpha, jdoubleArray a, jint lda, jdoubleArray b, jint ldb,
        jdouble beta, jdoubleArray c, jint ldc
) {
    // Get array elements from Java
    jdouble *a_ptr = env->GetDoubleArrayElements(a, nullptr);
    jdouble *b_ptr = env->GetDoubleArrayElements(b, nullptr);
    jdouble *c_ptr = env->GetDoubleArrayElements(c, nullptr);

    // Convert transpose flags
    CBLAS_TRANSPOSE transA_blas = transA ? CblasTrans : CblasNoTrans;
    CBLAS_TRANSPOSE transB_blas = transB ? CblasTrans : CblasNoTrans;

    // Call OpenBLAS dgemm with row-major order
    cblas_dgemm(CblasRowMajor, transA_blas, transB_blas, m, n, k,
                alpha, a_ptr, lda, b_ptr, ldb, beta, c_ptr, ldc);

    // Release arrays back to Java
    env->ReleaseDoubleArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseDoubleArrayElements(b, b_ptr, JNI_ABORT);
    env->ReleaseDoubleArrayElements(c, c_ptr, 0);
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_dgemv
(JNIEnv *env, jobject, jboolean trans, jint m, jint n, jdouble alpha,
jdoubleArray a, jint lda, jdoubleArray x, jint incx,
jdouble beta, jdoubleArray y, jint incy) {
    // Get array elements from Java
    jdouble *a_ptr = env->GetDoubleArrayElements(a, nullptr);
    jdouble *x_ptr = env->GetDoubleArrayElements(x, nullptr);
    jdouble *y_ptr = env->GetDoubleArrayElements(y, nullptr);

    // Convert transpose flag
    CBLAS_TRANSPOSE trans_blas = trans ? CblasTrans : CblasNoTrans;

    // Call OpenBLAS dgemv with row-major order
    cblas_dgemv(CblasRowMajor, trans_blas, m, n, alpha, a_ptr, lda, x_ptr, incx, beta, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseDoubleArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseDoubleArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseDoubleArrayElements(y, y_ptr, 0);
}

JNIEXPORT jdouble JNICALL Java_com_wsr_JBLAS_ddot
        (JNIEnv *env, jobject, jint n, jdoubleArray x, jint incx, jdoubleArray y, jint incy) {
    // Get array elements from Java
    jdouble *x_ptr = env->GetDoubleArrayElements(x, nullptr);
    jdouble *y_ptr = env->GetDoubleArrayElements(y, nullptr);

    // Call OpenBLAS ddot
    jdouble result = cblas_ddot(n, x_ptr, incx, y_ptr, incy);

    // Release arrays back to Java
    env->ReleaseDoubleArrayElements(x, x_ptr, JNI_ABORT);
    env->ReleaseDoubleArrayElements(y, y_ptr, JNI_ABORT);

    return result;
}

