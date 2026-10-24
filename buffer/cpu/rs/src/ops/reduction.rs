use rayon::iter::{IntoParallelRefIterator, ParallelIterator};

use crate::ops::shape::{transpose_d2, transpose_d3};
use crate::resource::buffer::CPUBuffer;
use crate::ops::utils::FastForEachExt;

const SUM_D1_PAR_THRESHOLD: usize = 2_000_000;
const REDUCE_D2_AXIS1_PAR_THRESHOLD: usize = 2_000_000;

pub fn average_d1(x: &[f32]) -> f32 {
    return sum_d1(x) / x.len() as f32;
}

pub fn average_d2(
    x: &CPUBuffer,
    xi: usize, xj: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    sum_d2(x, xi, xj, axis, result);

    let n = match axis {
        0 => xi,
        1 => xj,
        _ => panic!("invalid parameter. [axis: {}]", axis),
    };

    let inv = 1f32 / n as f32;
    for res in result.iter_mut() {
        *res *= inv;
    }
}

pub fn average_d3(
    x: &CPUBuffer,
    xi: usize, xj: usize, xk: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    sum_d3(x, xi, xj, xk, axis, result);

    let n = match axis {
        0 => xi,
        1 => xj,
        2 => xk,
        _ => panic!("invalid parameter. [axis: {}]", axis),
    };

    let inv = 1f32 / n as f32;
    for res in result.iter_mut() {
        *res *= inv;
    }
}

pub fn max_d1(x: &[f32]) -> f32 {
    return x.iter().fold(f32::MIN, |acc, i| acc.max(*i));
}

pub fn max_d2(
    x: &CPUBuffer,
    xi: usize, xj: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    result.fill(f32::MIN);
    reduce_d2(x, xi, xj, axis, result, |acc, i| acc.max(i));
}

pub fn max_d3(
    x: &CPUBuffer,
    xi: usize, xj: usize, xk: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    result.fill(f32::MIN);
    reduce_d3(x, xi, xj, xk, axis, result, |acc, i| acc.max(i));
}

pub fn min_d1(x: &[f32]) -> f32 {
    return x.iter().fold(f32::MAX, |acc, i| acc.min(*i));
}

pub fn min_d2(
    x: &CPUBuffer,
    xi: usize, xj: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    result.fill(f32::MAX);
    reduce_d2(x, xi, xj, axis, result, |acc, i| acc.min(i));
}

pub fn min_d3(
    x: &CPUBuffer,
    xi: usize, xj: usize, xk: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    result.fill(f32::MAX);
    reduce_d3(x, xi, xj, xk, axis, result, |acc, i| acc.min(i));
}

pub fn sum_d1(x: &[f32]) -> f32 {
    if x.len() >= SUM_D1_PAR_THRESHOLD {
        return x.par_iter().sum();
    }
    return x.iter().sum();
}

pub fn sum_d2(
    x: &CPUBuffer,
    xi: usize, xj: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    result.fill(0f32);
    reduce_d2(x, xi, xj, axis, result, |acc, i| acc + i);
}

pub fn sum_d3(
    x: &CPUBuffer,
    xi: usize, xj: usize, xk: usize,
    axis: usize,
    result: &mut CPUBuffer,
) {
    result.fill(0f32);
    reduce_d3(x, xi, xj, xk, axis, result, |acc, i| acc + i);
}

pub fn max_index_d1(x: &[f32]) -> usize {
    let mut max = 0;
    for i in 1..x.len() {
        if x[i] > x[max] {
            max = i;
        }
    }
    return max;
}

pub fn max_index_d2(x: &CPUBuffer, xi: usize, xj: usize, axis: usize, result: &mut CPUBuffer) {
    reduce_index_d2(x, xi, xj, axis, result, |a| max_index_d1(a) as f32);
}

pub fn max_index_d3(x: &CPUBuffer, xi: usize, xj: usize, xk: usize, axis: usize, result: &mut CPUBuffer) {
    reduce_index_d3(x, xi, xj, xk, axis, result, |a| max_index_d1(a) as f32);
}

pub fn top_k_d1(x: &[f32], k: usize, seed: u64) -> usize {
    let mut target: Vec<(&f32, usize)> = x.iter().zip(0..x.len()).collect();
    target.sort_by(|a, b| b.0.partial_cmp(a.0).unwrap_or(std::cmp::Ordering::Equal));
    return random_index(&target[..k.min(target.len())], seed);
}

pub fn top_k_d2(x: &CPUBuffer, xi: usize, xj: usize, k: usize, axis: usize, result: &mut CPUBuffer, seed: u64) {
    let mut count = 0u64;
    reduce_index_d2(x, xi, xj, axis, result, |slice| {
        let index = top_k_d1(slice, k, seed + count);
        count += 1;
        index as f32
    });
}

pub fn top_k_d3(x: &CPUBuffer, xi: usize, xj: usize, xk: usize, k: usize, axis: usize, result: &mut CPUBuffer, seed: u64) {
    let mut count = 0u64;
    reduce_index_d3(x, xi, xj, xk, axis, result, |slice| {
        let index = top_k_d1(slice, k, seed + count);
        count += 1;
        index as f32
    });
}

pub fn top_p_d1(x: &[f32], p: f32, seed: u64) -> usize {
    let mut target: Vec<(&f32, usize)> = x.iter().zip(0..x.len()).collect();
    target.sort_by(|a, b| b.0.partial_cmp(a.0).unwrap_or(std::cmp::Ordering::Equal));

    let mut total = 0f32;
    let mut nucleus: Vec<(&f32, usize)> = Vec::new();
    for &item in target.iter() {
        nucleus.push(item);
        total += item.0;
        if total >= p {
            break;
        }
    }
    return random_index(&nucleus, seed);
}

pub fn top_p_d2(x: &CPUBuffer, xi: usize, xj: usize, p: f32, axis: usize, result: &mut CPUBuffer, seed: u64) {
    let mut count = 0u64;
    reduce_index_d2(x, xi, xj, axis, result, |slice| {
        let index = top_p_d1(slice, p, seed + count);
        count += 1;
        index as f32
    });
}

pub fn top_p_d3(x: &CPUBuffer, xi: usize, xj: usize, xk: usize, p: f32, axis: usize, result: &mut CPUBuffer, seed: u64) {
    let mut count = 0u64;
    reduce_index_d3(x, xi, xj, xk, axis, result, |slice| {
        let index = top_p_d1(slice, p, seed + count);
        count += 1;
        index as f32
    });
}

fn reduce_d2<F: Fn(f32, f32) -> f32 + Sync>(
    x: &CPUBuffer,
    xi: usize, xj: usize,
    axis: usize,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(x.count() == xi * xj);
    match axis {
        0 => {
            assert_eq!(result.count(), xj);
            for inner in x.chunks_exact(xj) {
                for (res, &val) in result.iter_mut().zip(inner) {
                    *res = block(*res, val);
                }
            }
        }
        1 => {
            assert_eq!(result.count(), xi);
            result.fast_for_each(REDUCE_D2_AXIS1_PAR_THRESHOLD, |i, v| {
                *v = x[i * xj..(i + 1) * xj].iter().copied().fold(*v, &block);
            });
        }
        _ => panic!("invalid parameter. [axis: {}]", axis)
    }
}

fn reduce_d3<F: Fn(f32, f32) -> f32>(
    x: &CPUBuffer,
    xi: usize, xj: usize, xk: usize,
    axis: usize,
    result: &mut CPUBuffer,
    block: F,
) {
    assert!(x.count() == xi * xj * xk);
    match axis {
        0 => {
            assert_eq!(result.count(), xj * xk);
            for reduction in x.chunks_exact(xj * xk) {
                 for (res, &val) in result.iter_mut().zip(reduction) {
                    *res = block(*res, val);
                }
            }
        }
        1 => {
            assert_eq!(result.count(), xi * xk);
            for (res_slice, outer) in result.chunks_exact_mut(xk).zip(x.chunks_exact(xj * xk)) {
                for reduction in outer.chunks_exact(xk) {
                    for (res, &val) in res_slice.iter_mut().zip(reduction) {
                        *res = block(*res, val);
                    }
                }
            }
        }
        2 => {
            assert_eq!(result.count(), xi * xj);
            for (res, outer) in result.iter_mut().zip(x.chunks_exact(xk)) {
                *res = outer.iter().copied()
                    .fold(*res, |acc, i| block(acc, i));
            }
        }
        _ => panic!("invalid parameter. [axis: {}]", axis)
    }
}

fn reduce_index_d2<F: FnMut(&[f32]) -> f32>(
    x: &CPUBuffer,
    xi: usize, xj: usize,
    axis: usize,
    result: &mut CPUBuffer,
    mut block: F,
) {
    assert!(x.count() == xi * xj);
    match axis {
        0 => {
            assert_eq!(result.count(), xj);
            let mut tmp = CPUBuffer::create(x.count());
            transpose_d2(x, xi, xj, &mut tmp);
            for (res, outer) in result.iter_mut().zip(tmp.chunks_exact(xi)) {
                *res = block(outer);
            }
        }
        1 => {
            assert_eq!(result.count(), xi);
            for (res, outer) in result.iter_mut().zip(x.chunks_exact(xj)) {
                *res = block(outer);
            }
        }
        _ => panic!("invalid parameter. [axis: {}]", axis)
    }
}

fn reduce_index_d3<F: FnMut(&[f32]) -> f32>(
    x: &CPUBuffer,
    xi: usize, xj: usize, xk: usize,
    axis: usize,
    result: &mut CPUBuffer,
    mut block: F,
) {
    assert!(x.count() == xi * xj * xk);
    match axis {
        0 => reduce_index_d2(x, xi, xj * xk, 0, result, block),
        1 => {
            assert_eq!(result.count(), xi * xk);
            let mut tmp = CPUBuffer::create(x.count());
            transpose_d3(x, xi, xj, xk, 0, 2, 1, &mut tmp);
            for (res, outer) in result.iter_mut().zip(tmp.chunks_exact(xj)) {
                *res = block(outer);
            }
        }
        2 => reduce_index_d2(x, xi * xj, xk, 1, result, block),
        _ => panic!("invalid parameter. [axis: {}]", axis)
    }
}

fn random_index(x: &[(&f32, usize)], seed: u64) -> usize {
    let total: f32 = x.iter().map(|(v, _)| *v).sum();
    let mut state = if seed == 0 { 1 } else { seed };
    state ^= state << 13;
    state ^= state >> 7;
    state ^= state << 17;
    let threshold = (state as f32 / u64::MAX as f32) * total;
    let mut acc = 0f32;
    for &(val, i) in x.iter() {
        acc += val;
        if acc >= threshold {
            return i;
        }
    }
    return x.last().unwrap().1;
}
