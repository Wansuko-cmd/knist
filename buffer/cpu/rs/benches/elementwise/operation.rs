use std::hint::black_box;

use cpu::ops;
use cpu::ops::elementwise::operation::{div, minus, plus, times};
use cpu::resource::buffer::CPUBuffer;
use cpu::runtime::Runtime;
use criterion::measurement::Measurement;
use criterion::{BenchmarkGroup, BenchmarkId, Criterion, criterion_group, criterion_main};

fn bench_operation(c: &mut Criterion) {
    let runtime = ops::runtime::allocate(0);
    let mut group = c.benchmark_group("operation");

    // 同形状 (d1_to_d1) / スカラーブロードキャスト (d1_to_d0)
    for size in [1_024, 1_048_576] {
        bench_same_shape(
            &mut group,
            "plus_d1_to_d1",
            size,
            &runtime,
            plus::plus_with_d1_to_d1,
        );
        bench_same_shape(
            &mut group,
            "minus_d1_to_d1",
            size,
            &runtime,
            minus::minus_with_d1_to_d1,
        );
        bench_same_shape(
            &mut group,
            "times_d1_to_d1",
            size,
            &runtime,
            times::times_with_d1_to_d1,
        );
        bench_same_shape(
            &mut group,
            "div_d1_to_d1",
            size,
            &runtime,
            div::div_with_d1_to_d1,
        );

        bench_scalar(
            &mut group,
            "plus_d1_to_d0",
            size,
            &runtime,
            plus::plus_with_d1_to_d0,
        );
        bench_scalar(
            &mut group,
            "minus_d1_to_d0",
            size,
            &runtime,
            minus::minus_with_d1_to_d0,
        );
        bench_scalar(
            &mut group,
            "times_d1_to_d0",
            size,
            &runtime,
            times::times_with_d1_to_d0,
        );
        bench_scalar(
            &mut group,
            "div_d1_to_d0",
            size,
            &runtime,
            div::div_with_d1_to_d0,
        );
    }

    // 2次元へのブロードキャスト (d2_to_d1)
    for axis in [0, 1] {
        bench_broadcast_d2(
            &mut group,
            "plus_d2_to_d1",
            axis,
            128,
            128,
            &runtime,
            plus::plus_with_d2_to_d1,
        );
        bench_broadcast_d2(
            &mut group,
            "minus_d2_to_d1",
            axis,
            128,
            128,
            &runtime,
            minus::minus_with_d2_to_d1,
        );
        bench_broadcast_d2(
            &mut group,
            "times_d2_to_d1",
            axis,
            128,
            128,
            &runtime,
            times::times_with_d2_to_d1,
        );
        bench_broadcast_d2(
            &mut group,
            "div_d2_to_d1",
            axis,
            128,
            128,
            &runtime,
            div::div_with_d2_to_d1,
        );
    }

    // 4次元へのブロードキャスト (d4_to_d1)。先頭軸(0)と末尾軸(3)でメモリアクセスパターンの違いを見る
    for axis in [0, 3] {
        bench_broadcast_d4(
            &mut group,
            "plus_d4_to_d1",
            axis,
            4,
            16,
            16,
            16,
            &runtime,
            plus::plus_with_d4_to_d1,
        );
        bench_broadcast_d4(
            &mut group,
            "minus_d4_to_d1",
            axis,
            4,
            16,
            16,
            16,
            &runtime,
            minus::minus_with_d4_to_d1,
        );
        bench_broadcast_d4(
            &mut group,
            "times_d4_to_d1",
            axis,
            4,
            16,
            16,
            16,
            &runtime,
            times::times_with_d4_to_d1,
        );
        bench_broadcast_d4(
            &mut group,
            "div_d4_to_d1",
            axis,
            4,
            16,
            16,
            16,
            &runtime,
            div::div_with_d4_to_d1,
        );
    }

    group.finish();
}

fn bench_same_shape<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    size: usize,
    runtime: &Runtime,
    op: fn(&CPUBuffer, &CPUBuffer, &mut CPUBuffer),
) {
    let x = ops::buffer::init(&vec![1.0; size], runtime);
    let y = ops::buffer::init(&vec![1.0; size], runtime);
    let mut result = ops::buffer::create(size, runtime);

    group.bench_with_input(BenchmarkId::new(func_name, size), &size, |b, _| {
        b.iter(|| op(black_box(&x), black_box(&y), black_box(&mut result)));
    });
}

fn bench_scalar<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    size: usize,
    runtime: &Runtime,
    op: fn(&CPUBuffer, f32, &mut CPUBuffer),
) {
    let x = ops::buffer::init(&vec![1.0; size], runtime);
    let mut result = ops::buffer::create(size, runtime);

    group.bench_with_input(BenchmarkId::new(func_name, size), &size, |b, _| {
        b.iter(|| op(black_box(&x), 2.0, black_box(&mut result)));
    });
}

fn bench_broadcast_d2<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    axis: usize,
    xi: usize,
    xj: usize,
    runtime: &Runtime,
    op: fn(&CPUBuffer, usize, usize, &CPUBuffer, usize, &mut CPUBuffer),
) {
    let x = ops::buffer::init(&vec![1.0; xi * xj], runtime);
    let y_size = if axis == 0 { xi } else { xj };
    let y = ops::buffer::init(&vec![1.0; y_size], runtime);
    let mut result = ops::buffer::create(xi * xj, runtime);

    group.bench_with_input(
        BenchmarkId::new(func_name, format!("{}x{}_axis{}", xi, xj, axis)),
        &(xi * xj),
        |b, _| {
            b.iter(|| {
                op(
                    black_box(&x),
                    xi,
                    xj,
                    black_box(&y),
                    axis,
                    black_box(&mut result),
                )
            });
        },
    );
}

fn bench_broadcast_d4<M: Measurement>(
    group: &mut BenchmarkGroup<M>,
    func_name: &str,
    axis: usize,
    xi: usize,
    xj: usize,
    xk: usize,
    xl: usize,
    runtime: &Runtime,
    op: fn(&CPUBuffer, usize, usize, usize, usize, &CPUBuffer, usize, &mut CPUBuffer),
) {
    let size = xi * xj * xk * xl;
    let x = ops::buffer::init(&vec![1.0; size], runtime);
    let y_size = match axis {
        0 => xi,
        1 => xj,
        2 => xk,
        3 => xl,
        _ => panic!("invalid parameter. [axis: {}]", axis),
    };
    let y = ops::buffer::init(&vec![1.0; y_size], runtime);
    let mut result = ops::buffer::create(size, runtime);

    group.bench_with_input(
        BenchmarkId::new(
            func_name,
            format!("{}x{}x{}x{}_axis{}", xi, xj, xk, xl, axis),
        ),
        &size,
        |b, _| {
            b.iter(|| {
                op(
                    black_box(&x),
                    xi,
                    xj,
                    xk,
                    xl,
                    black_box(&y),
                    axis,
                    black_box(&mut result),
                )
            });
        },
    );
}

criterion_group!(benches, bench_operation);
criterion_main!(benches);
