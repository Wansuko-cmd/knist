use std::hint::black_box;

use cpu::ops;
use cpu::ops::elementwise::math;
use criterion::{BenchmarkId, Criterion, criterion_group, criterion_main};

fn bench_math(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("math");

    for size in [1_000, 1_000_000] {
        let x = ops::buffer::init(&vec![1.0; size], &runtime);
        let mut result = ops::buffer::create(size, &runtime);

        group.bench_with_input(BenchmarkId::new("exp_d1", size), &size, |b, _| {
            b.iter(|| math::exp_d1(black_box(&x), black_box(&mut result)));
        });

        group.bench_with_input(BenchmarkId::new("ln_d1", size), &size, |b, _| {
            b.iter(|| math::ln_d1(black_box(&x), 0.0, black_box(&mut result)));
        });

        group.bench_with_input(BenchmarkId::new("sigmoid_d1", size), &size, |b, _| {
            b.iter(|| math::sigmoid_d1(black_box(&x), black_box(&mut result)));
        });

        group.bench_with_input(BenchmarkId::new("pow_d1", size), &size, |b, _| {
            b.iter(|| math::pow_d1(black_box(&x), 2, black_box(&mut result)));
        });

        group.bench_with_input(BenchmarkId::new("sqrt_d1", size), &size, |b, _| {
            b.iter(|| math::sqrt_d1(black_box(&x), 0.0, black_box(&mut result)));
        });
    }

    group.finish();
}

criterion_group!(benches, bench_math);
criterion_main!(benches);
