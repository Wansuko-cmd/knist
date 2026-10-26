use wgpu::Device;

use crate::{kernels::task::ComputeTask, resource::buffer::GPUBuffer};

pub struct MatMul {
    device: Device,

    mat_mul_nn: wgpu::ComputePipeline,
    mat_mul_nt: wgpu::ComputePipeline,
    mat_mul_tn: wgpu::ComputePipeline,
    mat_mul_tt: wgpu::ComputePipeline,

    bind_group_layout: wgpu::BindGroupLayout,
}

impl MatMul {
    pub fn new(device: &Device) -> Self {
        let shader = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("MatMul::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("mat_mul.wgsl").into()),
        });

        let bind_group_layout = device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
            label: Some("MatMul::new"),
            entries: &[
                wgpu::BindGroupLayoutEntry {
                    binding: 0,
                    visibility: wgpu::ShaderStages::COMPUTE,
                    ty: wgpu::BindingType::Buffer {
                        ty: wgpu::BufferBindingType::Storage { read_only: true },
                        has_dynamic_offset: false,
                        min_binding_size: None,
                    },
                    count: None,
                },
                wgpu::BindGroupLayoutEntry {
                    binding: 1,
                    visibility: wgpu::ShaderStages::COMPUTE,
                    ty: wgpu::BindingType::Buffer {
                        ty: wgpu::BufferBindingType::Storage { read_only: true },
                        has_dynamic_offset: false,
                        min_binding_size: None,
                    },
                    count: None,
                },
                wgpu::BindGroupLayoutEntry {
                    binding: 2,
                    visibility: wgpu::ShaderStages::COMPUTE,
                    ty: wgpu::BindingType::Buffer {
                        ty: wgpu::BufferBindingType::Storage { read_only: false },
                        has_dynamic_offset: false,
                        min_binding_size: None,
                    },
                    count: None,
                },
            ],
        });

        let pipeline_layout = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
            label: Some("MatMul::new"),
            bind_group_layouts: &[&bind_group_layout],
            immediate_size: std::mem::size_of::<Params>() as u32,
        });

        MatMul {
            device: device.clone(),
            mat_mul_nn: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("mat_mul_nn"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("mat_mul_nn"),
                compilation_options: Default::default(),
                cache: None,
            }),
            mat_mul_nt: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("mat_mul_nt"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("mat_mul_nt"),
                compilation_options: Default::default(),
                cache: None,
            }),
            mat_mul_tn: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("mat_mul_tn"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("mat_mul_tn"),
                compilation_options: Default::default(),
                cache: None,
            }),
            mat_mul_tt: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("mat_mul_tt"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("mat_mul_tt"),
                compilation_options: Default::default(),
                cache: None,
            }),
            bind_group_layout: bind_group_layout,
        }
    }
}

#[repr(C)]
#[derive(Copy, Clone, bytemuck::Pod, bytemuck::Zeroable)]
struct Params {
    m: u32,
    n: u32,
    k: u32,
    b: u32,
}

impl MatMul {
    pub fn mat_mul_nn<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        m: usize,
        n: usize,
        k: usize,
        b: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task("mat_mul_nn", &self.mat_mul_nn, x, y, m, n, k, b, result)
    }

    pub fn mat_mul_nt<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        m: usize,
        n: usize,
        k: usize,
        b: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task("mat_mul_nt", &self.mat_mul_nt, x, y, m, n, k, b, result)
    }

    pub fn mat_mul_tn<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        m: usize,
        n: usize,
        k: usize,
        b: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task("mat_mul_tn", &self.mat_mul_tn, x, y, m, n, k, b, result)
    }

    pub fn mat_mul_tt<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        m: usize,
        n: usize,
        k: usize,
        b: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task("mat_mul_tt", &self.mat_mul_tt, x, y, m, n, k, b, result)
    }

    fn create_task<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: &GPUBuffer,
        y: &GPUBuffer,
        m: usize,
        n: usize,
        k: usize,
        b: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;
        let params = Params {
            m: m as u32,
            n: n as u32,
            k: k as u32,
            b: b as u32,
        };

        let bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            label: Some(label),
            layout: &self.bind_group_layout,
            entries: &[
                wgpu::BindGroupEntry {
                    binding: 0,
                    resource: x.buffer.as_entire_binding(),
                },
                wgpu::BindGroupEntry {
                    binding: 1,
                    resource: y.buffer.as_entire_binding(),
                },
                wgpu::BindGroupEntry {
                    binding: 2,
                    resource: result.buffer.as_entire_binding(),
                },
            ],
        });

        let workgroups = [((n + 63) / 64) as u32, ((m + 63) / 64) as u32, b as u32];

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(&params).to_vec()),
        }
    }
}
