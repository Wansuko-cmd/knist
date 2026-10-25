use std::hint::black_box;

use cpu::ops;
use cpu::ops::linalg;
use cpu::runtime::Runtime;
use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};

fn bench_mat_mul(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("mat_mul");

    bench(&mut group, "nn", false, false, &runtime);
    bench(&mut group, "nt", false, true, &runtime);
    bench(&mut group, "tn", true, false, &runtime);
    bench(&mut group, "tt", true, true, &runtime);

    group.finish();
}

struct MatMulConfig {
    m: usize,
    n: usize,
    k: usize,
    b: usize,
}

impl MatMulConfig {
    fn name(&self) -> String {
        format!("m{}_n{}_k{}_b{}", self.m, self.n, self.k, self.b)
    }
}

fn bench<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    trans_x: bool,
    trans_y: bool,
    runtime: &Runtime,
) {
    let configs = [
        MatMulConfig {
            m: 128,
            n: 128,
            k: 128,
            b: 4,
        },
        MatMulConfig {
            m: 512,
            n: 512,
            k: 512,
            b: 4,
        },
        MatMulConfig {
            m: 128,
            n: 128,
            k: 32,
            b: 512,
        },
        MatMulConfig {
            m: 8192,
            n: 3000,
            k: 256,
            b: 1,
        },
    ];

    for config in configs {
        let x = ops::buffer::init(&vec![1.0; config.b * config.m * config.k], runtime);
        let y = ops::buffer::init(&vec![1.0; config.b * config.k * config.n], runtime);
        let mut result = ops::buffer::create(config.b * config.m * config.n, runtime);

        group.bench_with_input(
            BenchmarkId::new(func_name, config.name()),
            &config,
            |bencher, _| {
                bencher.iter(|| {
                    linalg::mat_mul_d2_to_d2(
                        black_box(&x),
                        trans_x,
                        black_box(&y),
                        trans_y,
                        config.m,
                        config.n,
                        config.k,
                        config.b,
                        black_box(&mut result),
                    );
                });
            },
        );
    }
}

criterion_group!(benches, bench_mat_mul);
criterion_main!(benches);
