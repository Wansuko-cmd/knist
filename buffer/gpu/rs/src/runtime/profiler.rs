use std::cell::Cell;
use std::sync::atomic::{AtomicU32, AtomicU64, Ordering};
use std::time::Instant;

thread_local! {
    static CURRENT: Cell<Option<Instant>> = const { Cell::new(None) };
}

pub struct CpuProfiler {
    acc: AtomicU64,
    enabled: bool,
}

impl CpuProfiler {
    pub fn new(enabled: bool) -> Self {
        CpuProfiler {
            enabled,
            acc: AtomicU64::new(0),
        }
    }

    #[must_use = "bind to a variable (e.g. `let _t`), or the timer stops immediately"]
    pub fn start(&self) -> Option<OpTimer<'_>> {
        if !self.enabled || CURRENT.get().is_some() {
            return None;
        }
        CURRENT.set(Some(Instant::now()));
        Some(OpTimer { profiler: self })
    }

    pub fn take(&self) -> u64 {
        self.acc.swap(0, Ordering::Relaxed)
    }
}

pub struct OpTimer<'a> {
    profiler: &'a CpuProfiler,
}

impl Drop for OpTimer<'_> {
    fn drop(&mut self) {
        if let Some(t0) = CURRENT.take() {
            let time = t0.elapsed().as_nanos() as u64;
            self.profiler.acc.fetch_add(time, Ordering::Relaxed);
        }
    }
}

pub struct GpuProfiler {
    enabled: bool,
    query_set: Option<wgpu::QuerySet>,
    query_set_index: AtomicU32,
    resolve_buffer: Option<wgpu::Buffer>,
    staging_buffer: Option<wgpu::Buffer>,
}
pub struct GpuProfileResult {
    pub gpu_busy_ns: u64,
    pub gpu_span_ns: u64,
}

impl GpuProfiler {
    const TIMESTAMP_CAPACITY: u32 = 4096;

    pub fn new(device: &wgpu::Device, enabled: bool) -> Self {
        if !enabled {
            return GpuProfiler {
                enabled: enabled,
                query_set: None,
                query_set_index: AtomicU32::new(0),
                resolve_buffer: None,
                staging_buffer: None,
            };
        }
        let query_set = device.create_query_set(&wgpu::QuerySetDescriptor {
            label: Some("Profiler::new"),
            ty: wgpu::QueryType::Timestamp,
            count: Self::TIMESTAMP_CAPACITY,
        });
        let byte_size = (Self::TIMESTAMP_CAPACITY as u64) * 8;
        let resolve_buffer = device.create_buffer(&wgpu::BufferDescriptor {
            label: Some("Profiler::resolve_buffer"),
            size: byte_size,
            usage: wgpu::BufferUsages::QUERY_RESOLVE | wgpu::BufferUsages::COPY_SRC,
            mapped_at_creation: false,
        });
        let staging_buffer = device.create_buffer(&wgpu::BufferDescriptor {
            label: Some("Profiler::staging_buffer"),
            size: byte_size,
            usage: wgpu::BufferUsages::MAP_READ | wgpu::BufferUsages::COPY_DST,
            mapped_at_creation: false,
        });
        GpuProfiler {
            enabled: enabled,
            query_set: Some(query_set),
            query_set_index: AtomicU32::new(0),
            resolve_buffer: Some(resolve_buffer),
            staging_buffer: Some(staging_buffer),
        }
    }

    pub fn start(&self) -> Option<wgpu::ComputePassTimestampWrites<'_>> {
        if !self.enabled {
            return None;
        }
        let begin = self.query_set_index.fetch_add(2, Ordering::Relaxed);
        let end = begin + 1;
        if end >= Self::TIMESTAMP_CAPACITY {
            return None;
        }

        let query_set = self.query_set.as_ref().unwrap();
        Some(wgpu::ComputePassTimestampWrites {
            query_set,
            beginning_of_pass_write_index: Some(begin),
            end_of_pass_write_index: Some(end),
        })
    }

    pub fn take(&self, device: &wgpu::Device, queue: &wgpu::Queue) -> GpuProfileResult {
        let count = self
            .query_set_index
            .load(Ordering::Relaxed)
            .min(Self::TIMESTAMP_CAPACITY);
        if !self.enabled || count == 0 {
            return GpuProfileResult {
                gpu_busy_ns: 0,
                gpu_span_ns: 0,
            };
        }

        let query_set = self.query_set.as_ref().unwrap();
        let resolve_buffer = self.resolve_buffer.as_ref().unwrap();
        let staging_buffer = self.staging_buffer.as_ref().unwrap();

        // 読み出し
        let mut encoder = device.create_command_encoder(&wgpu::CommandEncoderDescriptor {
            label: Some("Profiler::take"),
        });
        encoder.resolve_query_set(query_set, 0..count, resolve_buffer, 0);
        encoder.copy_buffer_to_buffer(resolve_buffer, 0, staging_buffer, 0, (count as u64) * 8);
        queue.submit(Some(encoder.finish()));

        let slice = staging_buffer.slice(0..(count as u64) * 8);
        let (sender, receiver) = std::sync::mpsc::sync_channel(1);
        slice.map_async(wgpu::MapMode::Read, move |v| sender.send(v).unwrap());
        let _ = device.poll(wgpu::PollType::wait_indefinitely());
        receiver.recv().unwrap().unwrap();

        // tick -> ns
        let period = queue.get_timestamp_period() as f64;
        let (gpu_busy_ns, gpu_span_ns) = {
            let data = slice.get_mapped_range();
            let ticks: &[u64] = bytemuck::cast_slice(&data);

            // 演算時間の重複削除
            let mut intervals: Vec<(u64, u64)> = ticks
                .chunks(2)
                .filter(|tick| tick[0] < tick[1])
                .map(|tick| (tick[0], tick[1]))
                .collect();
            intervals.sort_unstable();

            let mut busy_ticks: u64 = 0;
            let mut current_end = 0u64;
            for &(begin, end) in &intervals {
                if end > current_end {
                    busy_ticks += end - begin.max(current_end);
                    current_end = end;
                }
            }

            let busy_ns = (busy_ticks as f64 * period) as u64;
            let span_ns = match intervals.first() {
                Some(&(min_begin, _)) => ((current_end - min_begin) as f64 * period) as u64,
                None => 0,
            };
            (busy_ns, span_ns)
        };

        staging_buffer.unmap();
        self.reset();

        GpuProfileResult {
            gpu_busy_ns: gpu_busy_ns,
            gpu_span_ns: gpu_span_ns,
        }
    }

    fn reset(&self) {
        self.query_set_index.store(0, Ordering::Relaxed);
    }
}
