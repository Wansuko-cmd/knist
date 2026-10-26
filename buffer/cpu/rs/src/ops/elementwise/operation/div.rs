use crate::resource::buffer::CPUBuffer;

pub fn div_with_d0_to_d1(x: f32, y: &CPUBuffer, result: &mut CPUBuffer) {
    super::zip_with_d0_to_d1(x, y, result, |a, b| a / b);
}

pub fn div_with_d1_to_d0(x: &CPUBuffer, y: f32, result: &mut CPUBuffer) {
    super::zip_with_d1_to_d0(x, y, result, |a, b| a / b);
}

pub fn div_with_d1_to_d1(x: &CPUBuffer, y: &CPUBuffer, result: &mut CPUBuffer) {
    super::zip_with_d1_to_d1(x, y, result, |a, b| a / b);
}

pub fn div_with_d1_to_d2(
    x: &CPUBuffer,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d1_to_d2(x, y, yi, yj, axis, result, |a, b| a / b);
}

pub fn div_with_d1_to_d3(
    x: &CPUBuffer,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d1_to_d3(x, y, yi, yj, yk, axis, result, |a, b| a / b);
}

pub fn div_with_d2_to_d1(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    y: &CPUBuffer,
    axis: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d2_to_d1(x, xi, xj, y, axis, result, |a, b| a / b);
}

pub fn div_with_d2_to_d3(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d2_to_d3(x, xi, xj, y, yi, yj, yk, axis1, axis2, result, |a, b| a / b);
}

pub fn div_with_d3_to_d1(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &CPUBuffer,
    axis: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d3_to_d1(x, xi, xj, xk, y, axis, result, |a, b| a / b);
}

pub fn div_with_d3_to_d2(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d3_to_d2(x, xi, xj, xk, y, yi, yj, axis1, axis2, result, |a, b| a / b);
}

pub fn div_with_d3_to_d4(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    yl: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d3_to_d4(
        x,
        xi,
        xj,
        xk,
        y,
        yi,
        yj,
        yk,
        yl,
        axis1,
        axis2,
        axis3,
        result,
        |a, b| a / b,
    );
}

pub fn div_with_d4_to_d1(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &CPUBuffer,
    axis: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d4_to_d1(x, xi, xj, xk, xl, y, axis, result, |a, b| a / b);
}

pub fn div_with_d4_to_d2(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    axis1: usize,
    axis2: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d4_to_d2(
        x,
        xi,
        xj,
        xk,
        xl,
        y,
        yi,
        yj,
        axis1,
        axis2,
        result,
        |a, b| a / b,
    );
}

pub fn div_with_d4_to_d3(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis1: usize,
    axis2: usize,
    axis3: usize,
    result: &mut CPUBuffer,
) {
    super::zip_with_d4_to_d3(
        x,
        xi,
        xj,
        xk,
        xl,
        y,
        yi,
        yj,
        yk,
        axis1,
        axis2,
        axis3,
        result,
        |a, b| a / b,
    );
}
