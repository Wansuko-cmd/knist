use criterion::{
    BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main,
    measurement::Measurement,
};
use gpu::{ops, resource::buffer::GPUBuffer, runtime::Runtime};

fn bench_mat_mul(c: &mut Criterion) {
    let mut runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("mat_mul");

    bench(&mut group, "nn", &mut runtime, |x, y, res, config, rt| {
        let task = rt
            .kernels
            .mat_mul
            .mat_mul_nn(x, y, config.m, config.n, config.k, config.b, res);
        rt.dispatch(task);
    });
    bench(&mut group, "nt", &mut runtime, |x, y, res, config, rt| {
        let task = rt
            .kernels
            .mat_mul
            .mat_mul_nt(x, y, config.m, config.n, config.k, config.b, res);
        rt.dispatch(task);
    });
    bench(&mut group, "tn", &mut runtime, |x, y, res, config, rt| {
        let task = rt
            .kernels
            .mat_mul
            .mat_mul_tn(x, y, config.m, config.n, config.k, config.b, res);
        rt.dispatch(task);
    });
    bench(&mut group, "tt", &mut runtime, |x, y, res, config, rt| {
        let task = rt
            .kernels
            .mat_mul
            .mat_mul_tt(x, y, config.m, config.n, config.k, config.b, res);
        rt.dispatch(task);
    });

    group.finish();
}

struct MatMulConfig {
    m: usize,
    n: usize,
    k: usize,
    b: usize,
    repeat: usize,
}

impl MatMulConfig {
    fn name(&self) -> String {
        format!("m{}_n{}_k{}_b{}", self.m, self.n, self.k, self.b)
    }
}

fn bench<M: Measurement, F>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    runtime: &mut Runtime,
    op: F,
) where
    F: Fn(&GPUBuffer, &GPUBuffer, &GPUBuffer, &MatMulConfig, &mut Runtime),
{
    let configs = [
        MatMulConfig {
            m: 128,
            n: 128,
            k: 128,
            b: 4,
            repeat: 50,
        },
        MatMulConfig {
            m: 512,
            n: 512,
            k: 512,
            b: 4,
            repeat: 20,
        },
    ];

    for config in configs {
        let x = ops::buffer::init(&vec![1.0; config.b * config.m * config.k], &runtime);
        let y = ops::buffer::init(&vec![1.0; config.b * config.k * config.n], &runtime);
        let result = ops::buffer::create(config.b * config.m * config.n, &runtime);

        group.bench_with_input(
            BenchmarkId::new(func_name, config.name()),
            &config,
            |bencher, _| {
                bencher.iter(|| {
                    for _ in 0..config.repeat {
                        op(&x, &y, &result, &config, runtime);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );
    }
}

criterion_group!(benches, bench_mat_mul);
criterion_main!(benches);
