use wgpu::Device;

use crate::{kernels::task::ComputeTask, resource::buffer::GPUBuffer};

pub struct Math {
    device: Device,

    exp_d1: wgpu::ComputePipeline,
    ln_d1: wgpu::ComputePipeline,
    sigmoid_d1: wgpu::ComputePipeline,
    pow_d1: wgpu::ComputePipeline,
    sqrt_d1: wgpu::ComputePipeline,

    bind_group_layout: wgpu::BindGroupLayout,
}

impl Math {
    pub fn new(device: &Device) -> Self {
        let shader = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Math::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("math.wgsl").into()),
        });

        let bind_group_layout = device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
            label: Some("Math::new"),
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
                        ty: wgpu::BufferBindingType::Storage { read_only: false },
                        has_dynamic_offset: false,
                        min_binding_size: None,
                    },
                    count: None,
                },
            ],
        });

        let pipeline_layout = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
            label: Some("Math::new"),
            bind_group_layouts: &[&bind_group_layout],
            immediate_size: std::mem::size_of::<Params>() as u32,
        });

        Math {
            device: device.clone(),
            exp_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("exp_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("exp_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            ln_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("ln_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("ln_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            sigmoid_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("sigmoid_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("sigmoid_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            pow_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("pow_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("pow_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            sqrt_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("sqrt_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("sqrt_d1"),
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
    e: f32,
    n: f32,
    _pad: [f32; 2],
}

impl Default for Params {
    fn default() -> Self {
        Params {
            e: 0f32,
            n: 0f32,
            _pad: [0f32; 2],
        }
    }
}

impl Math {
    const WORKGROUP_SIZE: u32 = 64;

    pub fn exp_d1<'a>(&'a self, x: &GPUBuffer, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task("exp_d1", &self.exp_d1, x, &Params::default(), result)
    }

    pub fn ln_d1<'a>(&'a self, x: &GPUBuffer, e: f32, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task(
            "ln_d1",
            &self.ln_d1,
            x,
            &Params {
                e: e,
                ..Params::default()
            },
            result,
        )
    }

    pub fn sigmoid_d1<'a>(&'a self, x: &GPUBuffer, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task(
            "sigmoid_d1",
            &self.sigmoid_d1,
            x,
            &Params::default(),
            result,
        )
    }

    pub fn pow_d1<'a>(&'a self, x: &GPUBuffer, n: f32, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task(
            "pow_d1",
            &self.pow_d1,
            x,
            &Params {
                n: n,
                ..Params::default()
            },
            result,
        )
    }

    pub fn sqrt_d1<'a>(&'a self, x: &GPUBuffer, e: f32, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task(
            "sqrt_d1",
            &self.sqrt_d1,
            x,
            &Params {
                e: e,
                ..Params::default()
            },
            result,
        )
    }

    fn create_task<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: &GPUBuffer,
        params: &Params,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;

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
                    resource: result.buffer.as_entire_binding(),
                },
            ],
        });

        let workgroups = result.workgroup_count(Math::WORKGROUP_SIZE);

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(params).to_vec()),
        }
    }
}
