use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};
use gpu::ops;
use gpu::resource::buffer::GPUBuffer;
use gpu::runtime::Runtime;

fn bench_compare(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("compare");

    for size in [1_000, 1_000_000] {
        bench(
            &mut group,
            "gt_d1_to_d0",
            size,
            1,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.compare.gt_d1_to_d0(x, 1.0, res);
                rt.dispatch(task);
            },
        );

        let y = ops::buffer::init(&vec![1.0; size], &runtime);

        bench(
            &mut group,
            "gt_d1_to_d1",
            size,
            size,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.compare.gt_d1_to_d1(x, &y, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "lt_d1_to_d0",
            size,
            1,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.compare.lt_d1_to_d0(x, 1.0, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "lt_d1_to_d1",
            size,
            size,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.compare.lt_d1_to_d1(x, &y, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "eq_d1_to_d0",
            size,
            1,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.compare.eq_d1_to_d0(x, 1.0, 0.0, 0.0, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "eq_d1_to_d1",
            size,
            size,
            &mut runtime,
            |x, res, rt| {
                let task = rt.kernels.compare.eq_d1_to_d1(x, &y, 0.0, 0.0, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "where_d0_to_d0",
            size,
            1,
            &mut runtime,
            |condition, res, rt| {
                let task = rt.kernels.compare.where_d0_to_d0(condition, 1.0, 0.0, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "where_d0_to_d1",
            size,
            size,
            &mut runtime,
            |condition, res, rt| {
                let task = rt.kernels.compare.where_d0_to_d1(condition, 1.0, &y, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "where_d1_to_d0",
            size,
            1,
            &mut runtime,
            |condition, res, rt| {
                let task = rt.kernels.compare.where_d1_to_d0(condition, &y, 0.0, res);
                rt.dispatch(task);
            },
        );

        bench(
            &mut group,
            "where_d1_to_d1",
            size,
            size,
            &mut runtime,
            |condition, res, rt| {
                let task = rt.kernels.compare.where_d1_to_d1(condition, &y, &y, res);
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

criterion_group!(benches, bench_compare);
criterion_main!(benches);
