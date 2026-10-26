use crate::{resource::buffer::GPUBuffer, runtime::profiler::GpuProfiler};

pub trait Task {
    fn recode(self, encoder: &mut wgpu::CommandEncoder, profiler: &GpuProfiler);
}

pub struct ComputeTask<'a> {
    pub label: &'static str,
    pub pipeline: &'a wgpu::ComputePipeline,
    pub bind_group: wgpu::BindGroup,
    pub workgroups: [u32; 3],
    pub params: Option<Vec<u8>>,
}

impl Task for ComputeTask<'_> {
    fn recode(self, encoder: &mut wgpu::CommandEncoder, profiler: &GpuProfiler) {
        let timestamp_writes = profiler.start();
        let mut compute_pass = encoder.begin_compute_pass(&wgpu::ComputePassDescriptor {
            label: Some(self.label),
            timestamp_writes: timestamp_writes,
        });

        compute_pass.set_pipeline(self.pipeline);
        compute_pass.set_bind_group(0, &self.bind_group, &[]);
        if let Some(value) = &self.params {
            compute_pass.set_immediates(0, value);
        }

        compute_pass.dispatch_workgroups(
            self.workgroups[0],
            self.workgroups[1],
            self.workgroups[2],
        );
    }
}

pub struct CopyTask<'a> {
    pub src: &'a GPUBuffer,
    pub src_offset: usize,
    pub dest: &'a GPUBuffer,
    pub dest_offset: usize,
    pub size: usize,
}

impl Task for CopyTask<'_> {
    fn recode(self, encoder: &mut wgpu::CommandEncoder, _profiler: &GpuProfiler) {
        encoder.copy_buffer_to_buffer(
            &self.src.buffer,
            (self.src_offset * GPUBuffer::SIZE_BYTES) as u64,
            &self.dest.buffer,
            (self.dest_offset * GPUBuffer::SIZE_BYTES) as u64,
            (self.size * GPUBuffer::SIZE_BYTES) as u64,
        );
    }
}

pub struct ClearTask<'a> {
    pub buffer: &'a GPUBuffer,
}

impl Task for ClearTask<'_> {
    fn recode(self, encoder: &mut wgpu::CommandEncoder, _profiler: &GpuProfiler) {
        encoder.clear_buffer(&self.buffer.buffer, 0, None);
    }
}
