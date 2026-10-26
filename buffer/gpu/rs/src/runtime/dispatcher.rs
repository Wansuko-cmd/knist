use crate::{kernels::task::Task, runtime::profiler::GpuProfiler};

pub struct Dispatcher {
    pub count: usize,
    active_encoder: Option<wgpu::CommandEncoder>,
}

impl Dispatcher {
    pub fn new() -> Self {
        Dispatcher {
            count: 0,
            active_encoder: None,
        }
    }

    pub fn dispatch<T: Task>(&mut self, task: T, device: &wgpu::Device, profiler: &GpuProfiler) {
        let encoder = self.active_encoder.get_or_insert_with(|| {
            device.create_command_encoder(&wgpu::CommandEncoderDescriptor {
                label: Some("Dispatcher::dispatch"),
            })
        });

        task.recode(encoder, profiler);
        self.count += 1;
    }

    pub fn submit(&mut self, queue: &wgpu::Queue) {
        if let Some(encoder) = self.active_encoder.take() {
            queue.submit(Some(encoder.finish()));
            self.active_encoder = None;
            self.count = 0;
        };
    }
}
