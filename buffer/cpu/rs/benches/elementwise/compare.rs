use std::hint::black_box;

use cpu::ops;
use cpu::ops::elementwise::compare;
use cpu::ops::elementwise::compare::r#where;
use criterion::{BenchmarkId, Criterion, criterion_group, criterion_main};

fn bench_compare(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("compare");

    for size in [1_000, 1_000_000] {
        let x = ops::buffer::init(&vec![1.0; size], &runtime);
        let y = ops::buffer::init(&vec![1.0; size], &runtime);
        let condition = ops::buffer::init(&vec![1.0; size], &runtime);
        let mut result = ops::buffer::create(size, &runtime);

        group.bench_with_input(
            BenchmarkId::new("greater_than_d1_to_d0", size),
            &size,
            |b, _| {
                b.iter(|| {
                    compare::greater_than_d1_to_d0(black_box(&x), 1.0, black_box(&mut result))
                });
            },
        );

        group.bench_with_input(
            BenchmarkId::new("greater_than_d1_to_d1", size),
            &size,
            |b, _| {
                b.iter(|| {
                    compare::greater_than_d1_to_d1(
                        black_box(&x),
                        black_box(&y),
                        black_box(&mut result),
                    )
                });
            },
        );

        group.bench_with_input(
            BenchmarkId::new("less_than_d1_to_d0", size),
            &size,
            |b, _| {
                b.iter(|| compare::less_than_d1_to_d0(black_box(&x), 1.0, black_box(&mut result)));
            },
        );

        group.bench_with_input(
            BenchmarkId::new("less_than_d1_to_d1", size),
            &size,
            |b, _| {
                b.iter(|| {
                    compare::less_than_d1_to_d1(
                        black_box(&x),
                        black_box(&y),
                        black_box(&mut result),
                    )
                });
            },
        );

        group.bench_with_input(BenchmarkId::new("equals_d1_to_d0", size), &size, |b, _| {
            b.iter(|| {
                compare::equals_d1_to_d0(black_box(&x), 1.0, 0.0, 0.0, black_box(&mut result))
            });
        });

        group.bench_with_input(BenchmarkId::new("equals_d1_to_d1", size), &size, |b, _| {
            b.iter(|| {
                compare::equals_d1_to_d1(
                    black_box(&x),
                    black_box(&y),
                    0.0,
                    0.0,
                    black_box(&mut result),
                )
            });
        });

        group.bench_with_input(BenchmarkId::new("where_d0_to_d0", size), &size, |b, _| {
            b.iter(|| {
                r#where::where_d0_to_d0(black_box(&condition), 1.0, 0.0, black_box(&mut result))
            });
        });

        group.bench_with_input(BenchmarkId::new("where_d0_to_d1", size), &size, |b, _| {
            b.iter(|| {
                r#where::where_d0_to_d1(
                    black_box(&condition),
                    1.0,
                    black_box(&y),
                    black_box(&mut result),
                )
            });
        });

        group.bench_with_input(BenchmarkId::new("where_d1_to_d0", size), &size, |b, _| {
            b.iter(|| {
                r#where::where_d1_to_d0(
                    black_box(&condition),
                    black_box(&y),
                    0.0,
                    black_box(&mut result),
                )
            });
        });

        group.bench_with_input(BenchmarkId::new("where_d1_to_d1", size), &size, |b, _| {
            b.iter(|| {
                r#where::where_d1_to_d1(
                    black_box(&condition),
                    black_box(&y),
                    black_box(&y),
                    black_box(&mut result),
                )
            });
        });
    }

    group.finish();
}

criterion_group!(benches, bench_compare);
criterion_main!(benches);
