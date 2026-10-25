use crate::ops::utils::FastForEachExt;
use crate::resource::buffer::CPUBuffer;

const TRANSPOSE_D2_PAR_THRESHOLD: usize = 2_000_000;
const TRANSPOSE_D3_PAR_THRESHOLD: usize = 2_000_000;
const TRANSPOSE_D4_PAR_THRESHOLD: usize = 2_000_000;
const UNFOLD_D1_PAR_THRESHOLD: usize = 100_000;
const UNFOLD_D2_PAR_THRESHOLD: usize = 100_000;
const FOLD_D1_PAR_THRESHOLD: usize = 200_000;
const FOLD_D2_PAR_THRESHOLD: usize = 500_000;

pub fn transpose_d2(x: &CPUBuffer, xi: usize, xj: usize, result: &mut CPUBuffer) {
    result.fast_chunks_for_each(xi, TRANSPOSE_D2_PAR_THRESHOLD, |ni, result_iter| {
        for (nj, res) in result_iter.iter_mut().enumerate() {
            *res = x[nj * xj + ni];
        }
    });
}

pub fn transpose_d3(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    axis_i: usize,
    axis_j: usize,
    axis_k: usize,
    result: &mut CPUBuffer,
) {
    let old_shape = [xi, xj, xk];
    let new_shape = [old_shape[axis_i], old_shape[axis_j], old_shape[axis_k]];

    let old_strides = [xj * xk, xk, 1];

    let ni_stride = old_strides[axis_i];
    let nj_stride = old_strides[axis_j];
    let nk_stride = old_strides[axis_k];

    let chunk_size = new_shape[1] * new_shape[2];
    result.fast_chunks_for_each(
        chunk_size,
        TRANSPOSE_D3_PAR_THRESHOLD,
        |index, result_iter| {
            let ni = index * ni_stride;
            let mut iter = result_iter.iter_mut();
            for nj in (ni..ni + new_shape[1] * nj_stride).step_by(nj_stride) {
                for index in (nj..nj + new_shape[2] * nk_stride).step_by(nk_stride) {
                    if let Some(res) = iter.next() {
                        *res = x[index];
                    }
                }
            }
        },
    );
}

pub fn transpose_d4(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    axis_i: usize,
    axis_j: usize,
    axis_k: usize,
    axis_l: usize,
    result: &mut CPUBuffer,
) {
    let old_shape = [xi, xj, xk, xl];
    let new_shape = [
        old_shape[axis_i],
        old_shape[axis_j],
        old_shape[axis_k],
        old_shape[axis_l],
    ];

    let old_strides = [xj * xk * xl, xk * xl, xl, 1];

    let ni_stride = old_strides[axis_i];
    let nj_stride = old_strides[axis_j];
    let nk_stride = old_strides[axis_k];
    let nl_stride = old_strides[axis_l];

    let chunk_size = new_shape[1] * new_shape[2] * new_shape[3];
    result.fast_chunks_for_each(
        chunk_size,
        TRANSPOSE_D4_PAR_THRESHOLD,
        |index, result_iter| {
            let ni = index * ni_stride;
            let mut iter = result_iter.iter_mut();
            for nj in (ni..ni + new_shape[1] * nj_stride).step_by(nj_stride) {
                for nk in (nj..nj + new_shape[2] * nk_stride).step_by(nk_stride) {
                    for index in (nk..nk + new_shape[3] * nl_stride).step_by(nl_stride) {
                        if let Some(res) = iter.next() {
                            *res = x[index];
                        }
                    }
                }
            }
        },
    );
}

pub fn transpose_d5(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    xm: usize,
    axis_i: usize,
    axis_j: usize,
    axis_k: usize,
    axis_l: usize,
    axis_m: usize,
    result: &mut CPUBuffer,
) {
    let old_shape = [xi, xj, xk, xl, xm];
    let new_shape = [
        old_shape[axis_i],
        old_shape[axis_j],
        old_shape[axis_k],
        old_shape[axis_l],
        old_shape[axis_m],
    ];

    let old_strides = [xj * xk * xl * xm, xk * xl * xm, xl * xm, xm, 1];

    let ni_stride = old_strides[axis_i];
    let nj_stride = old_strides[axis_j];
    let nk_stride = old_strides[axis_k];
    let nl_stride = old_strides[axis_l];
    let nm_stride = old_strides[axis_m];

    let mut result_iter = result.iter_mut();

    for n_i in (0..new_shape[0] * ni_stride).step_by(ni_stride) {
        for n_j in (n_i..n_i + new_shape[1] * nj_stride).step_by(nj_stride) {
            for n_k in (n_j..n_j + new_shape[2] * nk_stride).step_by(nk_stride) {
                for n_l in (n_k..n_k + new_shape[3] * nl_stride).step_by(nl_stride) {
                    for index in (n_l..n_l + new_shape[4] * nm_stride).step_by(nm_stride) {
                        if let Some(result_value) = result_iter.next() {
                            *result_value = x[index];
                        }
                    }
                }
            }
        }
    }
}

pub fn slice_d1(x: &CPUBuffer, start: usize, end: usize, step: isize, result: &mut CPUBuffer) {
    match step {
        0 => panic!("invalid parameter. [step: {}]", step),
        1 => {
            let size = result.count().min(end - start + 1);
            result[..size].copy_from_slice(&x[start..start + size]);
        }
        _ => {
            let indices = create_indices(start, end, step);
            for (i, res) in indices.iter().zip(result.iter_mut()) {
                *res = x[*i];
            }
        }
    }
}

pub fn slice_d2(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
    result: &mut CPUBuffer,
) {
    match step {
        0 => panic!("invalid parameter. [axis: {}, step: {}]", axis, step),
        _ => match axis {
            0 => {
                let indices = create_indices(start, end, step);
                for (r_i, &x_i) in indices.iter().enumerate() {
                    let x_offset = x_i * xj;
                    let result_offset = r_i * xj;
                    result[result_offset..result_offset + xj]
                        .copy_from_slice(&x[x_offset..x_offset + xj]);
                }
            }
            1 if step == 1 => {
                let size = (result.count() / xi).min(end - start + 1);
                for i in 0..xi {
                    let x_offset = i * xj + start;
                    let result_offset = i * size;
                    result[result_offset..result_offset + size]
                        .copy_from_slice(&x[x_offset..x_offset + size]);
                }
            }
            1 => {
                let indices = create_indices(start, end, step);
                let size = indices.len();
                for i in 0..xi {
                    let x_offset = i * xj;
                    let result_offset = i * size;
                    for (r_j, &x_j) in indices.iter().enumerate() {
                        result[result_offset + r_j] = x[x_offset + x_j];
                    }
                }
            }
            _ => panic!("invalid parameter. [axis: {}, step: {}]", axis, step),
        },
    }
}

pub fn slice_d3(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
    result: &mut CPUBuffer,
) {
    match axis {
        0 => slice_d2(x, xi, xj * xk, 0, start, end, step, result),
        1 => {
            let indices = create_indices(start, end, step);
            let size = indices.len();
            for i in 0..xi {
                let xii = i * xj * xk;
                let rii = i * size * xk;
                for (r_i, &x_i) in indices.iter().enumerate() {
                    let x_offset = xii + x_i * xk;
                    let result_offset = rii + r_i * xk;
                    result[result_offset..result_offset + xk]
                        .copy_from_slice(&x[x_offset..x_offset + xk]);
                }
            }
        }
        2 => slice_d2(x, xi * xj, xk, 1, start, end, step, result),
        _ => panic!("invalid parameter. [axis: {}, step: {}]", axis, step),
    }
}

pub fn copy_into_d1(x: &CPUBuffer, result: &mut CPUBuffer, start: usize, end: usize, step: isize) {
    match step {
        0 => panic!("invalid parameter. [step: {}]", step),
        1 => {
            let size = x.count().min(end - start + 1);
            result[start..start + size].copy_from_slice(&x[..size]);
        }
        _ => {
            let indices = create_indices(start, end, step);
            for (&i, x_val) in indices.iter().zip(x.iter()) {
                result[i] = *x_val;
            }
        }
    }
}

pub fn copy_into_d2(
    x: &CPUBuffer,
    result: &mut CPUBuffer,
    ri: usize,
    rj: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
) {
    match step {
        0 => panic!("invalid parameter. [axis: {}, step: {}]", axis, step),
        _ => match axis {
            0 => {
                let indices = create_indices(start, end, step);
                for (x_i, &r_i) in indices.iter().enumerate() {
                    let x_offset = x_i * rj;
                    let result_offset = r_i * rj;
                    result[result_offset..result_offset + rj]
                        .copy_from_slice(&x[x_offset..x_offset + rj]);
                }
            }
            1 if step == 1 => {
                let size = (x.count() / ri).min(end - start + 1);
                for i in 0..ri {
                    let x_offset = i * size;
                    let result_offset = i * rj + start;
                    result[result_offset..result_offset + size]
                        .copy_from_slice(&x[x_offset..x_offset + size]);
                }
            }
            1 => {
                let indices = create_indices(start, end, step);
                let size = indices.len();
                for i in 0..ri {
                    let x_offset = i * size;
                    let result_offset = i * rj;
                    for (x_j, &r_j) in indices.iter().enumerate() {
                        result[result_offset + r_j] = x[x_offset + x_j];
                    }
                }
            }
            _ => panic!("invalid parameter. [axis: {}, step: {}]", axis, step),
        },
    }
}

pub fn copy_into_d3(
    x: &CPUBuffer,
    result: &mut CPUBuffer,
    ri: usize,
    rj: usize,
    rk: usize,
    axis: usize,
    start: usize,
    end: usize,
    step: isize,
) {
    match axis {
        0 => copy_into_d2(x, result, ri, rj * rk, 0, start, end, step),
        1 => {
            let indices = create_indices(start, end, step);
            let size = indices.len();
            for i in 0..ri {
                let xii = i * size * rk;
                let rii = i * rj * rk;
                for (x_i, &r_i) in indices.iter().enumerate() {
                    let x_offset = xii + x_i * rk;
                    let result_offset = rii + r_i * rk;
                    result[result_offset..result_offset + rk]
                        .copy_from_slice(&x[x_offset..x_offset + rk]);
                }
            }
        }
        2 => copy_into_d2(x, result, ri * rj, rk, 1, start, end, step),
        _ => panic!("invalid parameter. [axis: {}, step: {}]", axis, step),
    }
}

fn create_indices(start: usize, end: usize, step: isize) -> Vec<usize> {
    match step {
        step if step > 0 => (start..=end).step_by(step as usize).collect(),
        step if step < 0 => (end..=start).rev().step_by(step.unsigned_abs()).collect(),
        _ => panic!("invalid parameter. [step: {}]", step),
    }
}

pub fn unfold_d1(
    x: &CPUBuffer,
    result: &mut CPUBuffer,
    _xi: usize,
    xj: usize,
    _b: usize,
    window: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
) {
    let window_size = (window - 1) * dilation + 1;
    let oj = (xj + padding * 2 - window_size) / stride + 1;
    result.fast_chunks_for_each(oj * window, UNFOLD_D1_PAR_THRESHOLD, |index, res_view| {
        let x_view = &x[index * xj..(index + 1) * xj];
        for j in 0..oj {
            for w in 0..window {
                let index = j * stride + w * dilation;
                if padding <= index && index < xj + padding {
                    res_view[j * window + w] = x_view[index - padding];
                }
            }
        }
    });
}

pub fn unfold_d2(
    x: &CPUBuffer,
    result: &mut CPUBuffer,
    _xi: usize,
    xj: usize,
    xk: usize,
    _b: usize,
    window: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
) {
    let window_size = (window - 1) * dilation + 1;
    let oj = (xj + padding * 2 - window_size) / stride + 1;
    let ok = (xk + padding * 2 - window_size) / stride + 1;
    let ww = window * window;
    result.fast_chunks_for_each(oj * ok * ww, UNFOLD_D2_PAR_THRESHOLD, |index, res_view| {
        let x_view = &x[index * xj * xk..(index + 1) * xj * xk];
        for ox in 0..oj {
            for oy in 0..ok {
                for wy in 0..window {
                    for wx in 0..window {
                        let input_x = (ox * stride + wy * dilation) as isize - padding as isize;
                        let input_y = (oy * stride + wx * dilation) as isize - padding as isize;
                        if input_x >= 0
                            && input_x < xj as isize
                            && input_y >= 0
                            && input_y < xk as isize
                        {
                            let input_x = input_x as usize;
                            let input_y = input_y as usize;
                            let result_index = (ox * ok + oy) * ww + wy * window + wx;
                            let x_index = input_x * xk + input_y;
                            res_view[result_index] = x_view[x_index];
                        }
                    }
                }
            }
        }
    });
}

pub fn fold_d1(
    x: &CPUBuffer,
    result: &mut CPUBuffer,
    _xi: usize,
    xj: usize,
    xk: usize,
    _b: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
) {
    let window_size = (xk - 1) * dilation + 1;
    let oj = window_size + (xj - 1) * stride - padding * 2;
    result.fast_chunks_for_each(oj, FOLD_D1_PAR_THRESHOLD, |index, res_view| {
        let x_view = &x[index * xj * xk..(index + 1) * xj * xk];
        for j in 0..xj {
            for k in 0..xk {
                let index = j * stride + k * dilation;
                if padding <= index && index < oj + padding {
                    res_view[index - padding] += x_view[j * xk + k];
                }
            }
        }
    });
}

pub fn fold_d2(
    x: &CPUBuffer,
    result: &mut CPUBuffer,
    _xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    _b: usize,
    stride: usize,
    dilation: usize,
    padding: usize,
) {
    let window = (xl as f64).sqrt() as usize;
    let window_size = (window - 1) * dilation + 1;
    let oj = window_size + (xj - 1) * stride - padding * 2;
    let ok = window_size + (xk - 1) * stride - padding * 2;
    result.fast_chunks_for_each(oj * ok, FOLD_D2_PAR_THRESHOLD, |index, res_view| {
        let x_view = &x[index * xj * xk * xl..(index + 1) * xj * xk * xl];
        for ox in 0..xj {
            for oy in 0..xk {
                for wy in 0..window {
                    for wx in 0..window {
                        let input_x = (ox * stride + wy * dilation) as isize - padding as isize;
                        let input_y = (oy * stride + wx * dilation) as isize - padding as isize;
                        if input_x >= 0
                            && input_x < oj as isize
                            && input_y >= 0
                            && input_y < ok as isize
                        {
                            let input_x = input_x as usize;
                            let input_y = input_y as usize;
                            let x_index = (ox * xk + oy) * xl + wy * window + wx;
                            let result_index = input_x * ok + input_y;
                            res_view[result_index] += x_view[x_index];
                        }
                    }
                }
            }
        }
    });
}

pub fn flip_d3(
    x: &CPUBuffer,
    xi: usize,
    xj: usize,
    xk: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    for i in 0..xi {
        for j in 0..xj {
            for k in 0..xk {
                let si = if axis == 0 { xi - 1 - i } else { i };
                let sj = if axis == 1 { xj - 1 - j } else { j };
                let sk = if axis == 2 { xk - 1 - k } else { k };
                let result_index = i * xj * xk + j * xk + k;
                let x_index = si * xj * xk + sj * xk + sk;
                result[result_index] = x[x_index];
            }
        }
    }
}
