use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};
use gpu::ops;
use gpu::resource::buffer::GPUBuffer;
use gpu::runtime::Runtime;

fn bench_transpose(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("transpose");

    for (xi, xj) in [(32, 32), (1000, 1000)] {
        bench(
            &mut group,
            "d2",
            xi * xj,
            xi * xj,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.transpose_d2(x, xi, xj, res);
                rt.dispatch(task);
            },
        );
    }

    for (xi, xj, xk) in [(16, 16, 16), (256, 256, 256)] {
        bench(
            &mut group,
            "d3",
            xi * xj * xk,
            xi * xj * xk,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.transpose_d3(x, xi, xj, xk, 2, 1, 0, res);
                rt.dispatch(task);
            },
        );
    }

    for (xi, xj, xk, xl) in [(8, 8, 8, 8), (64, 64, 64, 64)] {
        bench(
            &mut group,
            "d4",
            xi * xj * xk * xl,
            xi * xj * xk * xl,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .transpose_d4(x, xi, xj, xk, xl, 3, 2, 1, 0, res);
                rt.dispatch(task);
            },
        );
    }

    group.finish();
}

fn bench_slice(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("slice");

    for size in [1_000, 1_000_000] {
        let half = size / 2;
        bench(&mut group, "d1", size, half, &mut runtime, |x, res, rt| {
            let task = rt.kernels.shape.slice_d1(x, 0, half, 1, res);
            rt.dispatch(task);
        });
    }

    for (xi, xj) in [(32, 32), (1000, 1000)] {
        bench(
            &mut group,
            "d2_axis0",
            xi * xj,
            (xi / 2) * xj,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .slice_d2_axis0(x, xi, xj, 0, xi / 2, 1, res);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d2_axis1",
            xi * xj,
            xi * (xj / 2),
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .slice_d2_axis1(x, xi, xj, 0, xj / 2, 1, res);
                rt.dispatch(task);
            },
        );
    }

    for (xi, xj, xk) in [(16, 16, 16), (100, 100, 100)] {
        bench(
            &mut group,
            "d3_axis0",
            xi * xj * xk,
            (xi / 2) * xj * xk,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .slice_d2_axis0(x, xi, xj * xk, 0, xi / 2, 1, res);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d3_axis1",
            xi * xj * xk,
            xi * (xj / 2) * xk,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.slice_d3(x, xi, xj, xk, 0, xj / 2, 1, res);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d3_axis2",
            xi * xj * xk,
            xi * xj * (xk / 2),
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .slice_d2_axis1(x, xi * xj, xk, 0, xk / 2, 1, res);
                rt.dispatch(task);
            },
        );
    }

    group.finish();
}

fn bench_copy_into(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("copy_into");

    for size in [1_000, 1_000_000] {
        let half = size / 2;
        bench(&mut group, "d1", half, size, &mut runtime, |x, res, rt| {
            let task = rt.kernels.shape.copy_into_d1(x, res, 0, half, 1);
            rt.dispatch(task);
        });
    }

    for (ri, rj) in [(32, 32), (1000, 1000)] {
        bench(
            &mut group,
            "d2_axis0",
            (ri / 2) * rj,
            ri * rj,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .copy_into_d2_axis0(x, res, ri, rj, 0, ri / 2, 1);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d2_axis1",
            ri * (rj / 2),
            ri * rj,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .copy_into_d2_axis1(x, res, ri, rj, 0, rj / 2, 1);
                rt.dispatch(task);
            },
        );
    }

    for (ri, rj, rk) in [(16, 16, 16), (100, 100, 100)] {
        bench(
            &mut group,
            "d3_axis0",
            (ri / 2) * rj * rk,
            ri * rj * rk,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .copy_into_d2_axis0(x, res, ri, rj * rk, 0, ri / 2, 1);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d3_axis1",
            ri * (rj / 2) * rk,
            ri * rj * rk,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .copy_into_d3(x, res, ri, rj, rk, 0, rj / 2, 1);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d3_axis2",
            ri * rj * (rk / 2),
            ri * rj * rk,
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .copy_into_d2_axis1(x, res, ri * rj, rk, 0, rk / 2, 1);
                rt.dispatch(task);
            },
        );
    }

    group.finish();
}

fn bench_flip(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("flip");

    for (xi, xj) in [(32, 32), (1000, 1000)] {
        bench(
            &mut group,
            "d2_axis0",
            xi * xj,
            xi * xj,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.flip_d2_axis0(x, xi, xj, res);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d2_axis1",
            xi * xj,
            xi * xj,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.flip_d2_axis1(x, xi, xj, res);
                rt.dispatch(task);
            },
        );
    }

    for (xi, xj, xk) in [(16, 16, 16), (100, 100, 100)] {
        bench(
            &mut group,
            "d3_axis0",
            xi * xj * xk,
            xi * xj * xk,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.flip_d2_axis0(x, xi, xj * xk, res);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d3_axis1",
            xi * xj * xk,
            xi * xj * xk,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.flip_d3(x, xi, xj, xk, res);
                rt.dispatch(task);
            },
        );
        bench(
            &mut group,
            "d3_axis2",
            xi * xj * xk,
            xi * xj * xk,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.shape.flip_d2_axis1(x, xi * xj, xk, res);
                rt.dispatch(task);
            },
        );
    }

    group.finish();
}

fn bench_unfold(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
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
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .unfold_d1(x, xi, xj, b, window, stride, padding, res);
                rt.dispatch(task);
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
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .unfold_d2(x, xi, xj, xk, b, window, stride, padding, res);
                rt.dispatch(task);
            },
        );
    }

    group.finish();
}

fn bench_fold(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
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
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .fold_d1(x, xi, xj, xk, b, stride, padding, res);
                rt.dispatch(task);
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
            &mut runtime,
            |x, res, rt| {
                let task = rt
                    .kernels
                    .shape
                    .fold_d2(x, xi, xj, xk, xl, b, stride, padding, res);
                rt.dispatch(task);
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
    runtime: &mut Runtime,
    op: F,
) where
    F: Fn(&GPUBuffer, &GPUBuffer, &mut Runtime),
{
    let data = vec![1.0; input_size];
    let x = ops::buffer::init(&data, &runtime);
    let result = ops::buffer::create(result_size, &runtime);

    group.bench_with_input(BenchmarkId::new(name, input_size), &input_size, |b, _| {
        b.iter(|| {
            for _ in 0..100 {
                op(&x, &result, runtime);
            }
            runtime.submit();
            runtime.wait();
        });
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
