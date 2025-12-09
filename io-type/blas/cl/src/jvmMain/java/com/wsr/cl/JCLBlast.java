package com.wsr.cl;

public class JCLBlast {
    public native void init();

    public native long transfer(float[] data, int size);

    public native void read(long address, float[] destination);

    public native void release(long address);

    public native float sdot(
            int n,
            long x,
            int incX,
            long y,
            int incY
    );

    public native void sscal(
            int n,
            float alpha,
            long x,
            int incX
    );

    public native void saxpy(
            int n,
            float alpha,
            long x,
            int incX,
            long y,
            int incY
    );

    public native void sgemm(
            boolean transA,
            boolean transB,
            int m,
            int n,
            int k,
            float alpha,
            long a,
            int lda,
            long b,
            int ldb,
            float beta,
            long c,
            int ldc
    );

    public native void sgemv(
            boolean trans,
            int m,
            int n,
            float alpha,
            long a,
            int lda,
            long x,
            int incX,
            float beta,
            long y,
            int incY
    );
}
