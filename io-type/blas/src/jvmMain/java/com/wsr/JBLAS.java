package com.wsr;

public class JBLAS implements IBLAS {
    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public native float ddot(
            int n,
            float[] x,
            int incX,
            float[] y,
            int incY
    );

    @Override
    public native void dscal(
            int n,
            float alpha,
            float[] x,
            int incX
    );

    @Override
    public native void daxpy(
            int n,
            float alpha,
            float[] x,
            int incX,
            float[] y,
            int incY
    );

    @Override
    public native void dgemm(
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
            int ldc
    );

    @Override
    public native void dgemv(
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
