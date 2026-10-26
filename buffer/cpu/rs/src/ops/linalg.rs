use rayon::{
    iter::{IndexedParallelIterator, ParallelIterator},
    slice::ParallelSliceMut,
};

use crate::resource::buffer::CPUBuffer;

const MAT_MUL_D2_TO_D2_FLOP_PAR_THRESHOLD: usize = 1_000_000;
const MAT_MUL_D2_TO_D2_B1_M_THRESHOLD: usize = 64;

pub fn inner(x: &CPUBuffer, y: &CPUBuffer, b: usize, result: &mut CPUBuffer) {
    assert_eq!(x.count(), y.count());
    let n = x.count() / b;
    for i in 0..b {
        let x_ptr = &x[i * n..(i + 1) * n];
        let y_ptr = &y[i * n..(i + 1) * n];

        result[i] = x_ptr.iter().zip(y_ptr).map(|(a, b)| a * b).sum();
    }
}

pub fn mat_mul_d1_to_d2(
    x: &CPUBuffer,
    y: &CPUBuffer,
    trans_y: bool,
    n: usize,
    k: usize,
    result: &mut CPUBuffer,
) {
    let m = 1;

    let rsa = k as isize;
    let csa = 1;

    let (rsb, csb) = match trans_y {
        true => (1, k as isize),
        false => (n as isize, 1),
    };

    let rsc = n as isize;
    let csc = 1;

    unsafe {
        gemm::gemm(
            m,
            n,
            k,
            result.as_mut_ptr(),
            csc,
            rsc,
            false,
            x.as_ptr(),
            csa,
            rsa,
            y.as_ptr(),
            csb,
            rsb,
            0f32,
            1f32,
            false,
            false,
            false,
            gemm::Parallelism::None,
        );
    }
}

pub fn mat_mul_d2_to_d1(
    x: &CPUBuffer,
    trans_x: bool,
    y: &CPUBuffer,
    m: usize,
    k: usize,
    result: &mut CPUBuffer,
) {
    let n = 1;

    let (rsa, csa) = match trans_x {
        true => (1, m as isize),
        false => (k as isize, 1),
    };

    let rsb = 1;
    let csb = 1;

    let rsc = 1;
    let csc = 1;

    unsafe {
        gemm::gemm(
            m,
            n,
            k,
            result.as_mut_ptr(),
            csc,
            rsc,
            false,
            x.as_ptr(),
            csa,
            rsa,
            y.as_ptr(),
            csb,
            rsb,
            0f32,
            1f32,
            false,
            false,
            false,
            gemm::Parallelism::None,
        );
    }
}

pub fn mat_mul_d2_to_d2(
    x: &CPUBuffer,
    trans_x: bool,
    y: &CPUBuffer,
    trans_y: bool,
    m: usize,
    n: usize,
    k: usize,
    b: usize,
    result: &mut CPUBuffer,
) {
    let stride_a = m * k;
    let stride_b = k * n;
    let stride_c = m * n;

    let (rsa, csa) = match trans_x {
        true => (1, m as isize),
        false => (k as isize, 1),
    };
    let (rsb, csb) = match trans_y {
        true => (1, k as isize),
        false => (n as isize, 1),
    };
    let rsc = n as isize;
    let csc = 1;

    let parallel = b * m * n * k >= MAT_MUL_D2_TO_D2_FLOP_PAR_THRESHOLD;
    if b > 1 {
        chunked_for_each(result, stride_c, parallel, |i, c_ptr| {
            let a_ptr = &x[i * stride_a..];
            let b_ptr = &y[i * stride_b..];
            unsafe {
                gemm::gemm(
                    m,
                    n,
                    k,
                    c_ptr.as_mut_ptr(),
                    csc,
                    rsc,
                    false,
                    a_ptr.as_ptr(),
                    csa,
                    rsa,
                    b_ptr.as_ptr(),
                    csb,
                    rsb,
                    0f32,
                    1f32,
                    false,
                    false,
                    false,
                    gemm::Parallelism::None,
                );
            }
        });
    } else {
        let is_large_m = m > MAT_MUL_D2_TO_D2_B1_M_THRESHOLD;
        // mを分割してスレッドに割り当てる
        let threads = if parallel {
            rayon::current_num_threads().max(1)
        } else {
            1
        };
        let m_per_threads = m.div_ceil(threads).max(1);
        chunked_for_each(
            result,
            m_per_threads * n,
            parallel && is_large_m,
            |i, c_ptr| {
                let m_offset = i * m_per_threads;
                let a_ptr = &x[(m_offset as isize * rsa) as usize..];
                unsafe {
                    gemm::gemm(
                        c_ptr.len() / n,
                        n,
                        k,
                        c_ptr.as_mut_ptr(),
                        csc,
                        rsc,
                        false,
                        a_ptr.as_ptr(),
                        csa,
                        rsa,
                        y.as_ptr(),
                        csb,
                        rsb,
                        0f32,
                        1f32,
                        false,
                        false,
                        false,
                        gemm::Parallelism::None,
                    );
                }
            },
        );
    }
}

fn chunked_for_each<F>(result: &mut [f32], chunk_size: usize, parallel: bool, f: F)
where
    F: Fn(usize, &mut [f32]) + Sync,
{
    if parallel {
        result
            .par_chunks_mut(chunk_size)
            .enumerate()
            .for_each(|(i, c)| f(i, c));
    } else {
        result
            .chunks_mut(chunk_size)
            .enumerate()
            .for_each(|(i, c)| f(i, c));
    }
}
