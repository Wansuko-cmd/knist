#include "com_wsr_cpu_JOpenBLAS.h"
#include <stdio.h>
#include <cblas.h>

JNIEXPORT void JNICALL Java_com_wsr_cpu_JOpenBLAS_sgemm(
        JNIEnv *env, jobject, jboolean transA, jboolean transB,
        jint m, jint n, jint k,
        jfloat alpha, jfloatArray a, jint lda, jfloatArray b, jint ldb,
        jfloat beta, jfloatArray c, jint ldc, jint batchSize
) {
    // Javaからポインタを取得
    jfloat *a_ptr = env->GetFloatArrayElements(a, nullptr);
    jfloat *b_ptr = env->GetFloatArrayElements(b, nullptr);
    jfloat *c_ptr = env->GetFloatArrayElements(c, nullptr);

    int stride_a = m * k;
    int stride_b = k * n;
    int stride_c = m * n;

    // 転置フラグのキャスト
    CBLAS_TRANSPOSE transA_blas = transA ? CblasTrans : CblasNoTrans;
    CBLAS_TRANSPOSE transB_blas = transB ? CblasTrans : CblasNoTrans;

    // BLAS呼び出し
    for (int i = 0; i < batchSize; ++i) {
        jfloat *a_curr = a_ptr + (i * stride_a);
        jfloat *b_curr = b_ptr + (i * stride_b);
        jfloat *c_curr = c_ptr + (i * stride_c);

        // OpenBLAS呼び出し(row major)
        cblas_sgemm(
            CblasRowMajor,
            transA_blas, transB_blas,
            m, n, k,
            alpha,
            a_curr, lda,
            b_curr, ldb,
            beta,
            c_curr, ldc
        );
    }

    // リソース解放
    env->ReleaseFloatArrayElements(a, a_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(b, b_ptr, JNI_ABORT);
    env->ReleaseFloatArrayElements(c, c_ptr, 0);
}
