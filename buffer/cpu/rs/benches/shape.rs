use std::hint::black_box;

use cpu::ops;
use cpu::ops::shape;
use cpu::resource::buffer::CPUBuffer;
use cpu::runtime::Runtime;
use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};

fn bench_transpose(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("transpose");

    for (xi, xj) in [(32, 32), (1000, 1000)] {
        bench(
            &mut group,
            "d2",
            xi * xj,
            xi * xj,
            &runtime,
            move |x, res| {
                shape::transpose_d2(x, xi, xj, res);
            },
        );
    }

    for (xi, xj, xk) in [(16, 16, 16), (256, 256, 256)] {
        bench(
            &mut group,
            "d3",
            xi * xj * xk,
            xi * xj * xk,
            &runtime,
            move |x, res| {
                shape::transpose_d3(x, xi, xj, xk, 2, 1, 0, res);
            },
        );
    }

    for (xi, xj, xk, xl) in [(8, 8, 8, 8), (64, 64, 64, 64)] {
        bench(
            &mut group,
            "d4",
            xi * xj * xk * xl,
            xi * xj * xk * xl,
            &runtime,
            move |x, res| {
                shape::transpose_d4(x, xi, xj, xk, xl, 3, 2, 1, 0, res);
            },
        );
    }

    group.finish();
}

fn bench_slice(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("slice");

    for size in [1_000, 1_000_000] {
        let half = size / 2;
        bench(&mut group, "d1", size, half, &runtime, move |x, res| {
            shape::slice_d1(x, 0, half, 1, res);
        });
    }

    // slice_d2/d3のendは終端を含む(inclusive)ため、要素数を揃えるにはhalf-1を渡す
    for (xi, xj) in [(32, 32), (1000, 1000)] {
        let half_i = xi / 2;
        let half_j = xj / 2;
        bench(
            &mut group,
            "d2_axis0",
            xi * xj,
            half_i * xj,
            &runtime,
            move |x, res| {
                shape::slice_d2(x, xi, xj, 0, 0, half_i - 1, 1, res);
            },
        );
        bench(
            &mut group,
            "d2_axis1",
            xi * xj,
            xi * half_j,
            &runtime,
            move |x, res| {
                shape::slice_d2(x, xi, xj, 1, 0, half_j - 1, 1, res);
            },
        );
    }

    for (xi, xj, xk) in [(16, 16, 16), (100, 100, 100)] {
        let half_i = xi / 2;
        let half_j = xj / 2;
        let half_k = xk / 2;
        bench(
            &mut group,
            "d3_axis0",
            xi * xj * xk,
            half_i * xj * xk,
            &runtime,
            move |x, res| {
                shape::slice_d3(x, xi, xj, xk, 0, 0, half_i - 1, 1, res);
            },
        );
        bench(
            &mut group,
            "d3_axis1",
            xi * xj * xk,
            xi * half_j * xk,
            &runtime,
            move |x, res| {
                shape::slice_d3(x, xi, xj, xk, 1, 0, half_j - 1, 1, res);
            },
        );
        bench(
            &mut group,
            "d3_axis2",
            xi * xj * xk,
            xi * xj * half_k,
            &runtime,
            move |x, res| {
                shape::slice_d3(x, xi, xj, xk, 2, 0, half_k - 1, 1, res);
            },
        );
    }

    group.finish();
}

fn bench_copy_into(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("copy_into");

    for size in [1_000, 1_000_000] {
        let half = size / 2;
        bench(&mut group, "d1", half, size, &runtime, move |x, res| {
            shape::copy_into_d1(x, res, 0, half, 1);
        });
    }

    // copy_into_d2/d3のendも終端を含む(inclusive)ため、入力側の要素数に合わせてhalf-1を渡す
    for (ri, rj) in [(32, 32), (1000, 1000)] {
        let half_i = ri / 2;
        let half_j = rj / 2;
        bench(
            &mut group,
            "d2_axis0",
            half_i * rj,
            ri * rj,
            &runtime,
            move |x, res| {
                shape::copy_into_d2(x, res, ri, rj, 0, 0, half_i - 1, 1);
            },
        );
        bench(
            &mut group,
            "d2_axis1",
            ri * half_j,
            ri * rj,
            &runtime,
            move |x, res| {
                shape::copy_into_d2(x, res, ri, rj, 1, 0, half_j - 1, 1);
            },
        );
    }

    for (ri, rj, rk) in [(16, 16, 16), (100, 100, 100)] {
        let half_i = ri / 2;
        let half_j = rj / 2;
        let half_k = rk / 2;
        bench(
            &mut group,
            "d3_axis0",
            half_i * rj * rk,
            ri * rj * rk,
            &runtime,
            move |x, res| {
                shape::copy_into_d3(x, res, ri, rj, rk, 0, 0, half_i - 1, 1);
            },
        );
        bench(
            &mut group,
            "d3_axis1",
            ri * half_j * rk,
            ri * rj * rk,
            &runtime,
            move |x, res| {
                shape::copy_into_d3(x, res, ri, rj, rk, 1, 0, half_j - 1, 1);
            },
        );
        bench(
            &mut group,
            "d3_axis2",
            ri * rj * half_k,
            ri * rj * rk,
            &runtime,
            move |x, res| {
                shape::copy_into_d3(x, res, ri, rj, rk, 2, 0, half_k - 1, 1);
            },
        );
    }

    group.finish();
}

fn bench_flip(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("flip");

    // flip_d2は存在しないため、flip_d3にxk=1を渡して2次元として扱う
    for (xi, xj) in [(32, 32), (1000, 1000)] {
        bench(
            &mut group,
            "d2_axis0",
            xi * xj,
            xi * xj,
            &runtime,
            move |x, res| {
                shape::flip_d3(x, xi, xj, 1, 0, res);
            },
        );
        bench(
            &mut group,
            "d2_axis1",
            xi * xj,
            xi * xj,
            &runtime,
            move |x, res| {
                shape::flip_d3(x, xi, xj, 1, 1, res);
            },
        );
    }

    for (xi, xj, xk) in [(16, 16, 16), (100, 100, 100)] {
        bench(
            &mut group,
            "d3_axis0",
            xi * xj * xk,
            xi * xj * xk,
            &runtime,
            move |x, res| {
                shape::flip_d3(x, xi, xj, xk, 0, res);
            },
        );
        bench(
            &mut group,
            "d3_axis1",
            xi * xj * xk,
            xi * xj * xk,
            &runtime,
            move |x, res| {
                shape::flip_d3(x, xi, xj, xk, 1, res);
            },
        );
        bench(
            &mut group,
            "d3_axis2",
            xi * xj * xk,
            xi * xj * xk,
            &runtime,
            move |x, res| {
                shape::flip_d3(x, xi, xj, xk, 2, res);
            },
        );
    }

    group.finish();
}

fn bench_unfold(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("unfold");

    for (b, xi, xj, window, stride, padding) in [(4, 1, 28, 3, 1, 0), (4, 16, 256, 3, 1, 0)] {
        let oj = (xj - window + 2 * padding) / stride + 1;
        let input_size = b * xi * xj;
        let result_size = b * xi * oj * window;
        bench(
            &mut group,
            "d1",
            input_size,
            result_size,
            &runtime,
            move |x, res| {
                shape::unfold_d1(x, res, xi, xj, b, window, stride, 1, padding);
            },
        );
    }

    for (b, xi, xj, xk, window, stride, padding) in
        [(4, 1, 28, 28, 3, 1, 0), (4, 16, 28, 28, 3, 1, 0)]
    {
        let oj = (xj - window + 2 * padding) / stride + 1;
        let ok = (xk - window + 2 * padding) / stride + 1;
        let input_size = b * xi * xj * xk;
        let result_size = b * xi * oj * ok * window * window;
        bench(
            &mut group,
            "d2",
            input_size,
            result_size,
            &runtime,
            move |x, res| {
                shape::unfold_d2(x, res, xi, xj, xk, b, window, stride, 1, padding);
            },
        );
    }

    group.finish();
}

fn bench_fold(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("fold");

    for (b, xi, xj, xk, stride, padding) in [(4, 1, 26, 3, 1, 0), (4, 16, 254, 3, 1, 0)] {
        let out_len = (xj - 1) * stride + xk - 2 * padding;
        let input_size = b * xi * xj * xk;
        let result_size = b * xi * out_len;
        bench(
            &mut group,
            "d1",
            input_size,
            result_size,
            &runtime,
            move |x, res| {
                shape::fold_d1(x, res, xi, xj, xk, b, stride, 1, padding);
            },
        );
    }

    for (b, xi, xj, xk, xl, stride, padding) in [(4, 1, 26, 26, 9, 1, 0), (4, 16, 26, 26, 9, 1, 0)]
    {
        let window = (xl as f64).sqrt() as usize;
        let out_j = (xj - 1) * stride + window - 2 * padding;
        let out_k = (xk - 1) * stride + window - 2 * padding;
        let input_size = b * xi * xj * xk * xl;
        let result_size = b * xi * out_j * out_k;
        bench(
            &mut group,
            "d2",
            input_size,
            result_size,
            &runtime,
            move |x, res| {
                shape::fold_d2(x, res, xi, xj, xk, xl, b, stride, 1, padding);
            },
        );
    }

    group.finish();
}

fn bench<M: Measurement, F>(
    group: &mut BenchmarkGroup<M>,
    name: &str,
    input_size: usize,
    result_size: usize,
    runtime: &Runtime,
    op: F,
) where
    F: Fn(&CPUBuffer, &mut CPUBuffer),
{
    let data = vec![1.0; input_size];
    let x = ops::buffer::init(&data, runtime);
    let mut result = ops::buffer::create(result_size, runtime);

    group.bench_with_input(BenchmarkId::new(name, input_size), &input_size, |b, _| {
        b.iter(|| op(black_box(&x), black_box(&mut result)));
    });
}

criterion_group!(
    benches,
    bench_transpose,
    bench_slice,
    bench_copy_into,
    bench_flip,
    bench_unfold,
    bench_fold
);
criterion_main!(benches);
