package com.wsr.cpu;

import java.nio.ByteBuffer;

public class JMath {
    public native void exp(ByteBuffer x, ByteBuffer result);
    public native void ln(ByteBuffer x, float e, ByteBuffer result);
    public native void pow(ByteBuffer x, int n, ByteBuffer result);
    public native void sqrt(ByteBuffer x, float e, ByteBuffer result);
}
