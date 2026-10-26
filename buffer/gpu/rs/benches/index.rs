use criterion::{BenchmarkId, Criterion, criterion_group, criterion_main};
use gpu::ops;

fn bench_index(c: &mut Criterion) {
    let runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("index");

    let configs = [
        IndexConfig {
            i: 128,
            j: 128,
            k: 128,
            n: 128,
            b: 1,
            repeat: 50,
        },
        IndexConfig {
            i: 256,
            j: 256,
            k: 256,
            n: 256,
            b: 1,
            repeat: 20,
        },
    ];

    for config in configs {
        let i = config.i;
        let j = config.j;
        let k = config.k;
        let n = config.n;
        let b = config.b;

        let x = ops::buffer::create(n, &runtime);
        let y = ops::buffer::create(i * j * k, &runtime);
        let result = ops::buffer::create(i * n * k, &runtime);

        group.bench_with_input(
            BenchmarkId::new("gather", config.name()),
            &config,
            |bencher, _| {
                bencher.iter(|| {
                    for _ in 0..config.repeat {
                        let task = runtime.kernels.index.gather(&x, &y, i, j, k, &result);
                        runtime.dispatch(task);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );

        let x = ops::buffer::create(i * n * k * b, &runtime);
        let y = ops::buffer::create(n * b, &runtime);
        let result = ops::buffer::create(i * j * k, &runtime);

        group.bench_with_input(
            BenchmarkId::new("scatter_add", config.name()),
            &config,
            |bencher, _| {
                bencher.iter(|| {
                    for _ in 0..config.repeat {
                        let task = runtime
                            .kernels
                            .index
                            .scatter_add(&x, &y, i, j, k, b, &result);
                        runtime.dispatch(task);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );
    }

    group.finish();
}

struct IndexConfig {
    i: usize,
    j: usize,
    k: usize,
    n: usize,
    b: usize,
    repeat: usize,
}

impl IndexConfig {
    fn name(&self) -> String {
        format!("i{}_j{}_k{}_b{}", self.i, self.j, self.k, self.b)
    }
}

criterion_group!(benches, bench_index);
criterion_main!(benches);
