#include <mat_mul_fun.h>

#include <cblas.h>

void inner(float* x, float* y, size_t size, int b, float* result) {
    int n = size / b;
    for (int i = 0; i < b; ++i) {
        float* a_ptr = x + (i * b);
        float* b_ptr = y + (i * b);

        result[i] = cblas_sdot(n, a_ptr, 1, b_ptr, 1);
    }
}

void mat_mul_d1_to_d2(
    const float* x,
    const float* y, bool trans_y,
    int n, int k,
    float* result
) {
    CBLAS_TRANSPOSE trans_a = CblasNoTrans;
    CBLAS_TRANSPOSE trans_b = trans_y ? CblasTrans : CblasNoTrans;

    int m = 1;

    int lda = k;
    int ldb = trans_y ? k : n;
    int ldc = n;

    cblas_sgemm(
        CblasRowMajor,
        trans_a, trans_b,
        m, n, k,
        1,
        x, lda,
        y, ldb,
        0,
        result, ldc
    );
}

void mat_mul_d2_to_d1(
    const float* x, bool trans_x,
    const float* y,
    int m, int k,
    float* result
) {
    CBLAS_TRANSPOSE trans_a = trans_x ? CblasTrans : CblasNoTrans;
    CBLAS_TRANSPOSE trans_b = CblasNoTrans;

    int n = 1;

    int lda = trans_x ? m : k;
    int ldb = 1;
    int ldc = 1;

    cblas_sgemm(
        CblasRowMajor,
        trans_a, trans_b,
        m, n, k,
        1,
        x, lda,
        y, ldb,
        0,
        result, ldc
    );
}

void mat_mul_d2_to_d2(
    float* x, bool trans_x,
    float* y, bool trans_y,
    int m, int n, int k,
    int b,
    float* result
) {
    int stride_a = m * k;
    int stride_b = k * n;
    int stride_c = m * n;

    CBLAS_TRANSPOSE trans_a = trans_x ? CblasTrans : CblasNoTrans;
    CBLAS_TRANSPOSE trans_b = trans_y ? CblasTrans : CblasNoTrans;

    int lda = trans_x ? m : k;
    int ldb = trans_y ? k : n;
    int ldc = n;

    for (int i = 0; i < b; ++i) {
        float* a_ptr = x + (i * stride_a);
        float* b_ptr = y + (i * stride_b);
        float* c_ptr = result + (i * stride_c);

        cblas_sgemm(
            CblasRowMajor,
            trans_a, trans_b,
            m, n, k,
            1,
            a_ptr, lda,
            b_ptr, ldb,
            0,
            c_ptr, ldc
        );
    }
}
