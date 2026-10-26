use criterion::{BenchmarkId, Criterion, criterion_group, criterion_main};
use gpu::ops;

fn bench_operation(c: &mut Criterion) {
    let runtime = ops::runtime::allocate();
    let mut group = c.benchmark_group("operation");

    // (name, result_shape, x_stride, y_stride, x_size, y_size)
    let configs: &[(&str, [u32; 4], [u32; 4], [u32; 4], usize, usize)] = &[
        // 同形状
        (
            "d1_1024",
            [1, 1, 1, 1024],
            [0, 0, 0, 1],
            [0, 0, 0, 1],
            1024,
            1024,
        ),
        (
            "d1_1M",
            [1, 1, 1, 1_048_576],
            [0, 0, 0, 1],
            [0, 0, 0, 1],
            1_048_576,
            1_048_576,
        ),
        (
            "d2_128x128",
            [1, 1, 128, 128],
            [0, 0, 128, 1],
            [0, 0, 128, 1],
            128 * 128,
            128 * 128,
        ),
        (
            "d4_4x16x16x16",
            [4, 16, 16, 16],
            [4096, 256, 16, 1],
            [4096, 256, 16, 1],
            4 * 16 * 16 * 16,
            4 * 16 * 16 * 16,
        ),
        // ブロードキャスト
        (
            "bcast_d0_to_d1",
            [1, 1, 1, 1024],
            [0, 0, 0, 0],
            [0, 0, 0, 1],
            1,
            1024,
        ),
        (
            "bcast_d1_to_d2",
            [1, 1, 128, 128],
            [0, 0, 0, 1],
            [0, 0, 128, 1],
            128,
            128 * 128,
        ),
    ];

    for &(config_name, result_shape, x_stride, y_stride, x_size, y_size) in configs {
        let result_size = result_shape.iter().map(|&v| v as usize).product();
        let x = ops::buffer::init(&vec![1.0; x_size], &runtime);
        let y = ops::buffer::init(&vec![1.0; y_size], &runtime);
        let result = ops::buffer::create(result_size, &runtime);

        group.bench_with_input(
            BenchmarkId::new("plus_d4", config_name),
            &config_name,
            |b, _| {
                b.iter(|| {
                    for _ in 0..100 {
                        let task = runtime.kernels.operation.plus_d4(
                            &x,
                            &y,
                            result_shape,
                            x_stride,
                            y_stride,
                            &result,
                        );
                        runtime.dispatch(task);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );

        group.bench_with_input(
            BenchmarkId::new("minus_d4", config_name),
            &config_name,
            |b, _| {
                b.iter(|| {
                    for _ in 0..100 {
                        let task = runtime.kernels.operation.minus_d4(
                            &x,
                            &y,
                            result_shape,
                            x_stride,
                            y_stride,
                            &result,
                        );
                        runtime.dispatch(task);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );

        group.bench_with_input(
            BenchmarkId::new("times_d4", config_name),
            &config_name,
            |b, _| {
                b.iter(|| {
                    for _ in 0..100 {
                        let task = runtime.kernels.operation.times_d4(
                            &x,
                            &y,
                            result_shape,
                            x_stride,
                            y_stride,
                            &result,
                        );
                        runtime.dispatch(task);
                    }
                    runtime.submit();
                    runtime.wait();
                });
            },
        );

        group.bench_with_input(
            BenchmarkId::new("div_d4", config_name),
            &config_name,
            |b, _| {
                b.iter(|| {
                    for _ in 0..100 {
                        let task = runtime.kernels.operation.div_d4(
                            &x,
                            &y,
                            result_shape,
                            x_stride,
                            y_stride,
                            &result,
                        );
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

criterion_group!(benches, bench_operation);
criterion_main!(benches);
