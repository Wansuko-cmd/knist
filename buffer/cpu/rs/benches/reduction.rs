use std::hint::black_box;

use cpu::ops;
use cpu::ops::reduction;
use cpu::resource::buffer::CPUBuffer;
use cpu::runtime::Runtime;
use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};

fn bench_reduction(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("reduction");

    // d1: → scalar
    bench_d1(&mut group, "average_d1", &runtime, reduction::average_d1);
    bench_d1(&mut group, "max_d1", &runtime, reduction::max_d1);
    bench_d1(&mut group, "min_d1", &runtime, reduction::min_d1);
    bench_d1(&mut group, "sum_d1", &runtime, reduction::sum_d1);

    // d2_axis0: [xi, xj] → [xj]
    bench_d2(
        &mut group,
        "average_d2_axis0",
        0,
        &runtime,
        reduction::average_d2,
    );
    bench_d2(&mut group, "max_d2_axis0", 0, &runtime, reduction::max_d2);
    bench_d2(&mut group, "min_d2_axis0", 0, &runtime, reduction::min_d2);
    bench_d2(&mut group, "sum_d2_axis0", 0, &runtime, reduction::sum_d2);

    // d2_axis1: [xi, xj] → [xi]
    bench_d2(
        &mut group,
        "average_d2_axis1",
        1,
        &runtime,
        reduction::average_d2,
    );
    bench_d2(&mut group, "max_d2_axis1", 1, &runtime, reduction::max_d2);
    bench_d2(&mut group, "min_d2_axis1", 1, &runtime, reduction::min_d2);
    bench_d2(&mut group, "sum_d2_axis1", 1, &runtime, reduction::sum_d2);

    // d3 (axis=1): [xi, xj, xk] → [xi, xk]
    bench_d3(&mut group, "average_d3", 1, &runtime, reduction::average_d3);
    bench_d3(&mut group, "max_d3", 1, &runtime, reduction::max_d3);
    bench_d3(&mut group, "min_d3", 1, &runtime, reduction::min_d3);
    bench_d3(&mut group, "sum_d3", 1, &runtime, reduction::sum_d3);

    group.finish();
}

fn bench_d1<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    runtime: &Runtime,
    op: fn(&[f32]) -> f32,
) {
    for size in [1_000, 1_000_000] {
        let x = ops::buffer::init(&vec![1.0; size], runtime);

        group.bench_with_input(BenchmarkId::new(func_name, size), &size, |b, _| {
            b.iter(|| op(black_box(&x[..])));
        });
    }
}

// [xi, xj] → axisを潰した1次元
fn bench_d2<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    axis: usize,
    runtime: &Runtime,
    op: fn(&CPUBuffer, usize, usize, usize, &mut CPUBuffer),
) {
    for (xi, xj) in [(128, 128), (1024, 1024)] {
        let x = ops::buffer::init(&vec![1.0; xi * xj], runtime);
        let result_size = if axis == 0 { xj } else { xi };
        let mut result = ops::buffer::create(result_size, runtime);

        group.bench_with_input(
            BenchmarkId::new(func_name, format!("{}x{}", xi, xj)),
            &(xi * xj),
            |b, _| {
                b.iter(|| op(black_box(&x), xi, xj, axis, black_box(&mut result)));
            },
        );
    }
}

// [xi, xj, xk] → axisを潰した2次元
fn bench_d3<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    axis: usize,
    runtime: &Runtime,
    op: fn(&CPUBuffer, usize, usize, usize, usize, &mut CPUBuffer),
) {
    for (xi, xj, xk) in [(32, 32, 32), (128, 128, 128)] {
        let x = ops::buffer::init(&vec![1.0; xi * xj * xk], runtime);
        let mut result = ops::buffer::create(xi * xk, runtime);

        group.bench_with_input(
            BenchmarkId::new(func_name, format!("{}x{}x{}", xi, xj, xk)),
            &(xi * xj * xk),
            |b, _| {
                b.iter(|| op(black_box(&x), xi, xj, xk, axis, black_box(&mut result)));
            },
        );
    }
}

criterion_group!(benches, bench_reduction);
criterion_main!(benches);
