use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};
use gpu::ops;
use gpu::resource::buffer::GPUBuffer;
use gpu::runtime::Runtime;

fn bench_reduction(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("reduction");

    // d1: → scalar
    bench_d1(&mut group, "average_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.reduction.average_d1(x, res);
        rt.dispatch(task);
    });
    bench_d1(&mut group, "max_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.reduction.max_d1(x, res);
        rt.dispatch(task);
    });
    bench_d1(&mut group, "min_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.reduction.min_d1(x, res);
        rt.dispatch(task);
    });
    bench_d1(&mut group, "sum_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.reduction.sum_d1(x, res);
        rt.dispatch(task);
    });

    // d2_axis0: [xi, xj] → [xj]
    bench_d2(
        &mut group,
        "average_d2_axis0",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.average_d2_axis0(x, xi, xj, res);
            rt.dispatch(task);
        },
    );
    bench_d2(
        &mut group,
        "max_d2_axis0",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.max_d2_axis0(x, xi, xj, res);
            rt.dispatch(task);
        },
    );
    bench_d2(
        &mut group,
        "min_d2_axis0",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.min_d2_axis0(x, xi, xj, res);
            rt.dispatch(task);
        },
    );
    bench_d2(
        &mut group,
        "sum_d2_axis0",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.sum_d2_axis0(x, xi, xj, res);
            rt.dispatch(task);
        },
    );

    // d2_axis1: [xi, xj] → [xi]
    bench_d2_axis1(
        &mut group,
        "average_d2_axis1",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.average_d2_axis1(x, xi, xj, res);
            rt.dispatch(task);
        },
    );
    bench_d2_axis1(
        &mut group,
        "max_d2_axis1",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.max_d2_axis1(x, xi, xj, res);
            rt.dispatch(task);
        },
    );
    bench_d2_axis1(
        &mut group,
        "min_d2_axis1",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.min_d2_axis1(x, xi, xj, res);
            rt.dispatch(task);
        },
    );
    bench_d2_axis1(
        &mut group,
        "sum_d2_axis1",
        &mut runtime,
        |x, xi, xj, res, rt| {
            let task = rt.kernels.reduction.sum_d2_axis1(x, xi, xj, res);
            rt.dispatch(task);
        },
    );

    // d3: [xi, xj, xk] → [xi, xk]
    bench_d3(
        &mut group,
        "average_d3",
        &mut runtime,
        |x, xi, xj, xk, res, rt| {
            let task = rt.kernels.reduction.average_d3(x, xi, xj, xk, res);
            rt.dispatch(task);
        },
    );
    bench_d3(
        &mut group,
        "max_d3",
        &mut runtime,
        |x, xi, xj, xk, res, rt| {
            let task = rt.kernels.reduction.max_d3(x, xi, xj, xk, res);
            rt.dispatch(task);
        },
    );
    bench_d3(
        &mut group,
        "min_d3",
        &mut runtime,
        |x, xi, xj, xk, res, rt| {
            let task = rt.kernels.reduction.min_d3(x, xi, xj, xk, res);
            rt.dispatch(task);
        },
    );
    bench_d3(
        &mut group,
        "sum_d3",
        &mut runtime,
        |x, xi, xj, xk, res, rt| {
            let task = rt.kernels.reduction.sum_d3(x, xi, xj, xk, res);
            rt.dispatch(task);
        },
    );

    group.finish();
}

fn bench_d1<M: Measurement, F>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    runtime: &mut Runtime,
    op: F,
) where
    F: Fn(&GPUBuffer, &GPUBuffer, &mut Runtime),
{
    for size in [1_000, 1_000_000] {
        let x = ops::buffer::init(&vec![1.0; size], runtime);
        let result = ops::buffer::create(1, runtime);

        group.bench_with_input(BenchmarkId::new(func_name, size), &size, |b, _| {
            b.iter(|| {
                for _ in 0..100 {
                    op(&x, &result, runtime);
                }
                runtime.submit();
                runtime.wait();
            });
        });
    }
}

// [xi, xj] → [xj]
fn bench_d2<M: Measurement, F>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    runtime: &mut Runtime,
    op: F,
) where
    F: Fn(&GPUBuffer, usize, usize, &GPUBuffer, &mut Runtime),
{
    for (xi, xj) in [(128, 128), (1024, 1024)] {
        let x = ops::buffer::init(&vec![1.0; xi * xj], runtime);
        let result = ops::buffer::create(xj, runtime);

        group.bench_with_input(
            BenchmarkId::new(func_name, format!("{}x{}", xi, xj)),
            &(xi * xj),
            |b, _| {
                b.iter(|| {
                    for _ in 0..100 {
                        op(&x, xi, xj, &result, runtime);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );
    }
}

// [xi, xj] → [xi]
fn bench_d2_axis1<M: Measurement, F>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    runtime: &mut Runtime,
    op: F,
) where
    F: Fn(&GPUBuffer, usize, usize, &GPUBuffer, &mut Runtime),
{
    for (xi, xj) in [(128, 128), (1024, 1024)] {
        let x = ops::buffer::init(&vec![1.0; xi * xj], runtime);
        let result = ops::buffer::create(xi, runtime);

        group.bench_with_input(
            BenchmarkId::new(func_name, format!("{}x{}", xi, xj)),
            &(xi * xj),
            |b, _| {
                b.iter(|| {
                    for _ in 0..100 {
                        op(&x, xi, xj, &result, runtime);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );
    }
}

// [xi, xj, xk] → [xi, xk]
fn bench_d3<M: Measurement, F>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    runtime: &mut Runtime,
    op: F,
) where
    F: Fn(&GPUBuffer, usize, usize, usize, &GPUBuffer, &mut Runtime),
{
    for (xi, xj, xk) in [(32, 32, 32), (128, 128, 128)] {
        let x = ops::buffer::init(&vec![1.0; xi * xj * xk], runtime);
        let result = ops::buffer::create(xi * xk, runtime);

        group.bench_with_input(
            BenchmarkId::new(func_name, format!("{}x{}x{}", xi, xj, xk)),
            &(xi * xj * xk),
            |b, _| {
                b.iter(|| {
                    for _ in 0..100 {
                        op(&x, xi, xj, xk, &result, runtime);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );
    }
}

criterion_group!(benches, bench_reduction);
criterion_main!(benches);
