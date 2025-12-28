package com.wsr.cpu;

class JTranspose {
    public native void transposeD2(
            float[] x,
            int xi,
            int xj,
            float[] result
    );

    public native void transposeD3(
            float[] x,
            int xi,
            int xj,
            int xk,
            int axisI,
            int axisJ,
            int axisK,
            float[] result
    );

    public native void transposeD4(
            float[] x,
            int xi,
            int xj,
            int xk,
            int xl,
            int axisI,
            int axisJ,
            int axisK,
            int axisL,
            float[] result
    );
}
