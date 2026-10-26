use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};
use gpu::ops;
use gpu::resource::buffer::GPUBuffer;
use gpu::runtime::Runtime;

fn bench_math(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("math");

    bench(&mut group, "exp_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.math.exp_d1(x, res);
        rt.dispatch(task);
    });
    bench(&mut group, "ln_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.math.ln_d1(x, 0.0, res);
        rt.dispatch(task);
    });
    bench(&mut group, "sigmoid_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.math.sigmoid_d1(x, res);
        rt.dispatch(task);
    });
    bench(&mut group, "pow_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.math.pow_d1(x, 2.0, res);
        rt.dispatch(task);
    });
    bench(&mut group, "sqrt_d1", &mut runtime, |x, res, rt| {
        let task = rt.kernels.math.sqrt_d1(x, 0.0, res);
        rt.dispatch(task);
    });

    group.finish();
}

fn bench<M: Measurement, F>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    runtime: &mut Runtime,
    op: F,
) where
    F: Fn(&GPUBuffer, &GPUBuffer, &mut Runtime),
{
    for size in [1_000, 1_000_000] {
        let data = vec![1.0; size];
        let x = ops::buffer::init(&data, &runtime);
        let result = ops::buffer::create(size, &runtime);

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

criterion_group!(benches, bench_math);
criterion_main!(benches);
