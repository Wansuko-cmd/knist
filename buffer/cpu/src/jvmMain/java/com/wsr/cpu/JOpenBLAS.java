package com.wsr.cpu;

import java.nio.ByteBuffer;

class JOpenBLAS {
    public native void sgemm(
            boolean transA,
            boolean transB,
            int m,
            int n,
            int k,
            float alpha,
            ByteBuffer a,
            int lda,
            ByteBuffer b,
            int ldb,
            float beta,
            ByteBuffer c,
            int ldc,
            int batchSize
    );
}
