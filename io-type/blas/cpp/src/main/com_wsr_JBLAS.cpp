#include "com_wsr_JBLAS.h"
#include <stdio.h>

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_dgemm(JNIEnv *env, jobject, jboolean transA, jboolean transB, jint m, jint n, jint k,
        jdouble alpha, jdoubleArray a, jint lda, jdoubleArray b, jint ldb,
        jdouble beta, jdoubleArray c, jint ldc
) {
}

JNIEXPORT void JNICALL Java_com_wsr_JBLAS_dgemv
(JNIEnv *env, jobject, jboolean trans, jint m, jint n, jdouble alpha,
jdoubleArray a, jint lda, jdoubleArray x, jint incx,
jdouble beta, jdoubleArray y, jint incy) {
}

JNIEXPORT jdouble JNICALL Java_com_wsr_JBLAS_ddot
        (JNIEnv *env, jobject, jint n, jdoubleArray x, jint incx, jdoubleArray y, jint incy) {
    return 0.0;
}

