package com.wsr.open;

class JOpenBLAS {
    public native float sdot(
            int n,
            float[] x,
            int incX,
            float[] y,
            int incY
    );

    public native void sscal(
            int n,
            float alpha,
            float[] x,
            int incX
    );

    public native void saxpy(
            int n,
            float alpha,
            float[] x,
            int incX,
            float[] y,
            int incY
    );

    public native void sgemm(
            boolean transA,
            boolean transB,
            int m,
            int n,
            int k,
            float alpha,
            float[] a,
            int lda,
            float[] b,
            int ldb,
            float beta,
            float[] c,
            int ldc,
            int batchSize
    );

    public native void sgemv(
            boolean trans,
            int m,
            int n,
            float alpha,
            float[] a,
            int lda,
            float[] x,
            int incX,
            float beta,
            float[] y,
            int incY
    );
}
