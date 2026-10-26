use crate::resource::buffer::CPUBuffer;

pub mod div;
pub mod minus;
pub mod plus;
pub mod times;

pub(super) fn zip_with_d0_to_d1<F: Fn(f32, f32) -> f32>(
    x: f32,
    y: &CPUBuffer,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(y.count() == result.count());
    for (i, &value) in y.iter().enumerate() {
        result[i] = block(x, value);
    }
}

pub(super) fn zip_with_d1_to_d0<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    y: f32,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(x.count() == result.count());
    for (i, &value) in x.iter().enumerate() {
        result[i] = block(value, y);
    }
}

pub(super) fn zip_with_d1_to_d1<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    y: &CPUBuffer,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(x.count() == y.count());
    assert!(x.count() == result.count());
    for (i, value) in result.iter_mut().enumerate() {
        *value = block(x[i], y[i]);
    }
}

pub(super) fn zip_with_d1_to_d2<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    axis: usize,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(y.count() == yi * yj);
    assert!(result.count() == yi * yj);

    match axis {
        0 => {
            assert!(x.count() == yi);
            for i in 0..yi {
                let x_value = x[i];
                let y_i = i * yj;
                for j in 0..yj {
                    let index = y_i + j;
                    result[index] = block(x_value, y[index]);
                }
            }
        }
        1 => {
            assert!(x.count() == yj);
            for i in 0..yi {
                let y_i = i * yj;
                for j in 0..yj {
                    let index = y_i + j;
                    result[index] = block(x[j], y[index]);
                }
            }
        }
        _ => panic!("invalid parameter. [axis: {}]", axis),
    }
}

pub(super) fn zip_with_d1_to_d3<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    y: &CPUBuffer,
    yi: usize,
    yj: usize,
    yk: usize,
    axis: usize,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(y.count() == yi * yj * yk);
    assert!(result.count() == yi * yj * yk);

    match axis {
        0 => {
            assert!(x.count() == yi);
            for i in 0..yi {
                let x_value = x[i];
                let y_i = i * yj;
                for j in 0..yj {
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let index = y_j + k;
                        result[index] = block(x_value, y[index]);
                    }
                }
            }
        }
        1 => {
            assert!(x.count() == yj);
            for i in 0..yi {
                let y_i = i * yj;
                for j in 0..yj {
                    let x_value = x[j];
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let index = y_j + k;
                        result[index] = block(x_value, y[index]);
                    }
                }
            }
        }
        2 => {
            assert!(x.count() == yk);
            for i in 0..yi {
                let y_i = i * yj;
                for j in 0..yj {
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let index = y_j + k;
                        result[index] = block(x[k], y[index]);
                    }
                }
            }
        }
        _ => panic!("invalid parameter. [axis: {}]", axis),
    }
}

pub(super) fn zip_with_d2_to_d1<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    y: &CPUBuffer,
    axis: usize,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(x.count() == xi * xj);
    assert!(result.count() == xi * xj);

    match axis {
        0 => {
            assert!(xi == y.count());
            for i in 0..xi {
                let y_value = y[i];
                let x_i = i * xj;
                for j in 0..xj {
                    let index = x_i + j;
                    result[index] = block(x[index], y_value);
                }
            }
        }
        1 => {
            assert!(xj == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let index = x_i + j;
                    result[index] = block(x[index], y[j]);
                }
            }
        }
        _ => panic!("invalid parameter. [axis: {}]", axis),
    }
}

pub(super) fn zip_with_d2_to_d3<F: Fn(f32, f32) -> f32>(
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
    block: F,
) {
    assert!(x.count() == xi * xj);
    assert!(y.count() == yi * yj * yk);
    assert!(result.count() == yi * yj * yk);

    match (axis1, axis2) {
        (0, 1) => {
            assert!(xi == yi && xj == yj);
            for i in 0..yi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..yj {
                    let x_value = x[x_i + j];
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let index = y_j + k;
                        result[index] = block(x_value, y[index]);
                    }
                }
            }
        }
        (0, 2) => {
            assert!(xi == yi && xj == yk);
            for i in 0..yi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..yj {
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let index = y_j + k;
                        result[index] = block(x[x_i + k], y[index]);
                    }
                }
            }
        }
        (1, 2) => {
            assert!(xi == yj && xj == yk);
            for i in 0..yi {
                let y_i = i * yj;
                for j in 0..yj {
                    let x_i = j * xj;
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let index = y_j + k;
                        result[index] = block(x[x_i + k], y[index]);
                    }
                }
            }
        }
        _ => panic!("invalid parameter. [axis1: {}, axis2: {}]", axis1, axis2),
    }
}

pub(super) fn zip_with_d3_to_d1<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    y: &CPUBuffer,
    axis: usize,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(x.count() == xi * xj * xk);
    assert!(result.count() == xi * xj * xk);

    match axis {
        0 => {
            assert!(xi == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                let y_value = y[i];
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let index = x_j + k;
                        result[index] = block(x[index], y_value);
                    }
                }
            }
        }
        1 => {
            assert!(xj == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_value = y[j];
                    for k in 0..xk {
                        let index = x_j + k;
                        result[index] = block(x[index], y_value);
                    }
                }
            }
        }
        2 => {
            assert!(xk == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let index = x_j + k;
                        result[index] = block(x[index], y[k]);
                    }
                }
            }
        }
        _ => panic!("invalid parameter. [axis: {}]", axis),
    }
}

pub(super) fn zip_with_d3_to_d2<F: Fn(f32, f32) -> f32>(
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
    block: F,
) {
    assert!(x.count() == xi * xj * xk);
    assert!(y.count() == yi * yj);
    assert!(result.count() == xi * xj * xk);

    match (axis1, axis2) {
        (0, 1) => {
            assert!(xi == yi && xj == yj);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_value = y[y_i + j];
                    for k in 0..xk {
                        let index = x_j + k;
                        result[index] = block(x[index], y_value);
                    }
                }
            }
        }
        (0, 2) => {
            assert!(xi == yi && xk == yj);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let index = x_j + k;
                        result[index] = block(x[index], y[y_i + k]);
                    }
                }
            }
        }
        (1, 2) => {
            assert!(xj == yi && xk == yj);
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_i = j * yj;
                    for k in 0..xk {
                        let index = x_j + k;
                        result[index] = block(x[index], y[y_i + k]);
                    }
                }
            }
        }
        _ => panic!("invalid parameter. [axis1: {}, axis2: {}]", axis1, axis2),
    }
}

pub(super) fn zip_with_d3_to_d4<F: Fn(f32, f32) -> f32>(
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
    block: F,
) {
    assert!(x.count() == xi * xj * xk);
    assert!(y.count() == yi * yj * yk * yl);
    assert!(result.count() == yi * yj * yk * yl);

    match (axis1, axis2, axis3) {
        (0, 1, 2) => {
            assert!(xi == yi && xj == yj && xk == yk);
            for i in 0..yi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..yj {
                    let x_j = (x_i + j) * xk;
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let x_value = x[x_j + k];
                        let y_k = (y_j + k) * yl;
                        for l in 0..yl {
                            let index = y_k + l;
                            result[index] = block(x_value, y[index]);
                        }
                    }
                }
            }
        }
        (0, 1, 3) => {
            assert!(xi == yi && xj == yj && xk == yl);
            for i in 0..yi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..yj {
                    let x_j = (x_i + j) * xk;
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let y_k = (y_j + k) * yl;
                        for l in 0..yl {
                            let index = y_k + l;
                            result[index] = block(x[x_j + l], y[index]);
                        }
                    }
                }
            }
        }
        (0, 2, 3) => {
            assert!(xi == yi && xj == yk && xk == yl);
            for i in 0..yi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..yj {
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let x_j = (x_i + k) * xk;
                        let y_k = (y_j + k) * yl;
                        for l in 0..yl {
                            let index = y_k + l;
                            result[index] = block(x[x_j + l], y[index]);
                        }
                    }
                }
            }
        }
        (1, 2, 3) => {
            assert!(xi == yj && xj == yk && xk == yl);
            for i in 0..yi {
                let y_i = i * yj;
                for j in 0..yj {
                    let x_i = j * xj;
                    let y_j = (y_i + j) * yk;
                    for k in 0..yk {
                        let x_j = (x_i + k) * xk;
                        let y_k = (y_j + k) * yl;
                        for l in 0..yl {
                            let index = y_k + l;
                            result[index] = block(x[x_j + l], y[index]);
                        }
                    }
                }
            }
        }
        _ => panic!(
            "invalid parameter. [axis1: {}, axis2: {}, axis3: {}]",
            axis1, axis2, axis3
        ),
    }
}

pub(super) fn zip_with_d4_to_d1<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    y: &CPUBuffer,
    axis: usize,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(x.count() == xi * xj * xk * xl);
    assert!(result.count() == xi * xj * xk * xl);

    match axis {
        0 => {
            assert!(xi == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                let y_value = y[i];
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y_value);
                        }
                    }
                }
            }
        }
        1 => {
            assert!(xj == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_value = y[j];
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y_value);
                        }
                    }
                }
            }
        }
        2 => {
            assert!(xk == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        let y_value = y[k];
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y_value);
                        }
                    }
                }
            }
        }
        3 => {
            assert!(xl == y.count());
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y[l]);
                        }
                    }
                }
            }
        }
        _ => panic!("invalid parameter. [axis: {}]", axis),
    }
}

pub(super) fn zip_with_d4_to_d2<F: Fn(f32, f32) -> f32>(
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
    block: F,
) {
    assert!(x.count() == xi * xj * xk * xl);
    assert!(y.count() == yi * yj);
    assert!(result.count() == xi * xj * xk * xl);

    match (axis1, axis2) {
        (0, 1) => {
            assert!(xi == yi && xj == yj);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_value = y[y_i + j];
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y_value);
                        }
                    }
                }
            }
        }
        (0, 2) => {
            assert!(xi == yi && xk == yj);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        let y_value = y[y_i + k];
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y_value);
                        }
                    }
                }
            }
        }
        (0, 3) => {
            assert!(xi == yi && xl == yj);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y[y_i + l]);
                        }
                    }
                }
            }
        }
        (1, 2) => {
            assert!(xj == yi && xk == yj);
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_i = j * yj;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        let y_value = y[y_i + k];
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y_value);
                        }
                    }
                }
            }
        }
        (1, 3) => {
            assert!(xj == yi && xl == yj);
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_i = j * yj;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y[y_i + l]);
                        }
                    }
                }
            }
        }
        (2, 3) => {
            assert!(xk == yi && xl == yj);
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        let y_i = k * yj;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y[y_i + l]);
                        }
                    }
                }
            }
        }
        _ => panic!("invalid parameter. [axis1: {}, axis2: {}]", axis1, axis2),
    }
}

pub(super) fn zip_with_d4_to_d3<F: Fn(f32, f32) -> f32>(
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
    block: F,
) {
    assert!(x.count() == xi * xj * xk * xl);
    assert!(y.count() == yi * yj * yk);
    assert!(result.count() == xi * xj * xk * xl);

    match (axis1, axis2, axis3) {
        (0, 1, 2) => {
            assert!(xi == yi && xj == yj && xk == yk);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_j = (y_i + j) * yk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        let y_value = y[y_j + k];
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y_value);
                        }
                    }
                }
            }
        }
        (0, 1, 3) => {
            assert!(xi == yi && xj == yj && xl == yk);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_j = (y_i + j) * yk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y[y_j + l]);
                        }
                    }
                }
            }
        }
        (0, 2, 3) => {
            assert!(xi == yi && xk == yj && xl == yk);
            for i in 0..xi {
                let x_i = i * xj;
                let y_i = i * yj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        let y_j = (y_i + k) * yk;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y[y_j + l]);
                        }
                    }
                }
            }
        }
        (1, 2, 3) => {
            assert!(xj == yi && xk == yj && xl == yk);
            for i in 0..xi {
                let x_i = i * xj;
                for j in 0..xj {
                    let x_j = (x_i + j) * xk;
                    let y_i = j * yj;
                    for k in 0..xk {
                        let x_k = (x_j + k) * xl;
                        let y_j = (y_i + k) * yk;
                        for l in 0..xl {
                            let index = x_k + l;
                            result[index] = block(x[index], y[y_j + l]);
                        }
                    }
                }
            }
        }
        _ => panic!(
            "invalid parameter. [axis1: {}, axis2: {}, axis3: {}]",
            axis1, axis2, axis3
        ),
    }
}
