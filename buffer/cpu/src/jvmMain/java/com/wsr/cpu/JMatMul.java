package com.wsr.cpu;

import java.nio.ByteBuffer;

class JMatMul {
    public native void inner(
            ByteBuffer x,
            ByteBuffer y,
            int b,
            ByteBuffer result
    );

    public native void matMulD1ToD2(
            ByteBuffer x,
            ByteBuffer y,
            boolean transY,
            int n,
            int k,
            ByteBuffer result
    );

    public native void matMulD2ToD1(
            ByteBuffer x,
            boolean transX,
            ByteBuffer y,
            int m,
            int k,
            ByteBuffer result
    );

    public native void matMulD2ToD2(
            ByteBuffer x,
            boolean transX,
            ByteBuffer y,
            boolean transY,
            int m,
            int n,
            int k,
            int b,
            ByteBuffer result
    );
}
