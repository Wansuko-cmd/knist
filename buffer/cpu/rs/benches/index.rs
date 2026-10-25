use std::hint::black_box;

use cpu::ops;
use criterion::{BenchmarkId, Criterion, criterion_group, criterion_main};

struct IndexConfig {
    i: usize,
    j: usize,
    k: usize,
    n: usize,
    b: usize,
}

impl IndexConfig {
    fn name(&self) -> String {
        format!("i{}_j{}_k{}_b{}", self.i, self.j, self.k, self.b)
    }
}

fn bench_index(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("index");

    let configs = [
        IndexConfig {
            i: 128,
            j: 128,
            k: 128,
            n: 128,
            b: 1,
        },
        IndexConfig {
            i: 256,
            j: 256,
            k: 256,
            n: 256,
            b: 1,
        },
    ];

    for config in configs {
        let i = config.i;
        let j = config.j;
        let k = config.k;
        let n = config.n;
        let b = config.b;

        // gatherのxはyのj次元を指すインデックスなので、[0, j)に収める
        let indices: Vec<f32> = (0..n).map(|v| (v % j) as f32).collect();

        let x = ops::buffer::init(&indices, &runtime);
        let y = ops::buffer::init(&vec![1.0; i * j * k], &runtime);
        let mut result = ops::buffer::create(i * n * k, &runtime);

        group.bench_with_input(
            BenchmarkId::new("gather", config.name()),
            &config,
            |bencher, _| {
                bencher.iter(|| {
                    ops::index::gather(
                        black_box(&x),
                        black_box(&y),
                        i,
                        j,
                        k,
                        black_box(&mut result),
                    );
                });
            },
        );

        let x = ops::buffer::init(&vec![1.0; b * i * n * k], &runtime);
        let y = ops::buffer::init(&indices, &runtime);
        let mut result = ops::buffer::create(i * j * k, &runtime);

        group.bench_with_input(
            BenchmarkId::new("scatter_add", config.name()),
            &config,
            |bencher, _| {
                bencher.iter(|| {
                    ops::index::scatter_add(
                        black_box(&x),
                        black_box(&y),
                        i,
                        j,
                        k,
                        b,
                        black_box(&mut result),
                    );
                });
            },
        );
    }

    group.finish();
}

criterion_group!(benches, bench_index);
criterion_main!(benches);
