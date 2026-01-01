package com.wsr.cpu;

import java.nio.ByteBuffer;

class JTranspose {
    public native void transposeD2(
            ByteBuffer x,
            int xi,
            int xj,
            ByteBuffer result
    );

    public native void transposeD3(
            ByteBuffer x,
            int xi,
            int xj,
            int xk,
            int axisI,
            int axisJ,
            int axisK,
            ByteBuffer result
    );

    public native void transposeD4(
            ByteBuffer x,
            int xi,
            int xj,
            int xk,
            int xl,
            int axisI,
            int axisJ,
            int axisK,
            int axisL,
            ByteBuffer result
    );
}
