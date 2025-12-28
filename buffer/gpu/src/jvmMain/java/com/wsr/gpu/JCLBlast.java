package com.wsr.gpu;

public class JCLBlast {
    public native void init();

    public native long transfer(float[] data, int size);

    public native void read(long address, float[] destination);

    public native void release(long address);

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
            int ldc,
            int batchSize
    );
}
