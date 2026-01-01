package com.wsr.cpu;

import java.nio.ByteBuffer;

class JCollection {
    public native float max(ByteBuffer x);
    public native void maxD1(ByteBuffer x, int xb, ByteBuffer result);
    public native void maxD2(ByteBuffer x, int xi, int xj, int axis, ByteBuffer result);
    public native void maxD3(ByteBuffer x, int xi, int xj, int xk, int axis, ByteBuffer result);
    public native void maxD4(ByteBuffer x, int xi, int xj, int xk, int xl, int axis, ByteBuffer result);

    public native float min(ByteBuffer x);
    public native void minD1(ByteBuffer x, int xb, ByteBuffer result);
    public native void minD2(ByteBuffer x, int xi, int xj, int axis, ByteBuffer result);
    public native void minD3(ByteBuffer x, int xi, int xj, int xk, int axis, ByteBuffer result);
    public native void minD4(ByteBuffer x, int xi, int xj, int xk, int xl, int axis, ByteBuffer result);

    public native float sum(ByteBuffer x);
    public native void sumD1(ByteBuffer x, int xb, ByteBuffer result);
    public native void sumD2(ByteBuffer x, int xi, int xj, int axis, ByteBuffer result);
    public native void sumD3(ByteBuffer x, int xi, int xj, int xk, int axis, ByteBuffer result);
    public native void sumD4(ByteBuffer x, int xi, int xj, int xk, int xl, int axis, ByteBuffer result);
}

