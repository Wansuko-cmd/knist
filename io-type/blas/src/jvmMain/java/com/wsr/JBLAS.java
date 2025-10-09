package com.wsr;

public class JBLAS implements IBLAS {
    @Override
    public boolean isNative() {
        return true;
    }

    @Override
    public native void dgemm(
            boolean transA,
            boolean transB,
            int m,
            int n,
            int k,
            double alpha,
            double[] a,
            int lda,
            double[] b,
            int ldb,
            double beta,
            double[] c,
            int ldc
    );

    @Override
    public native void dgemv(
            boolean trans,
            int m,
            int n,
            double alpha,
            double[] a,
            int lda,
            double[] x,
            int incX,
            double beta,
            double[] y,
            int incY
    );

    @Override
    public native double ddot(
            int n,
            double[] x,
            int incX,
            double[] y,
            int incY
    );
}
