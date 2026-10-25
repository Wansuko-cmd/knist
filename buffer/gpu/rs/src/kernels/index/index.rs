use wgpu::Device;

use crate::{kernels::task::ComputeTask, resource::buffer::GPUBuffer};

pub struct Index {
    device: Device,

    gather: wgpu::ComputePipeline,
    scatter_add: wgpu::ComputePipeline,

    bind_group_layout: wgpu::BindGroupLayout,
}

impl Index {
    pub fn new(device: &Device) -> Self {
        let gather_shader = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Index::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("gather.wgsl").into()),
        });
        let scatter_shader = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Index::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("scatter.wgsl").into()),
        });

        let bind_group_layout = device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
            label: Some("Index::new"),
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
            label: Some("Index::new"),
            bind_group_layouts: &[&bind_group_layout],
            immediate_size: std::mem::size_of::<Params>() as u32,
        });

        Index {
            device: device.clone(),
            gather: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("gather"),
                layout: Some(&pipeline_layout),
                module: &gather_shader,
                entry_point: Some("gather"),
                compilation_options: Default::default(),
                cache: None,
            }),
            scatter_add: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("scatter_add"),
                layout: Some(&pipeline_layout),
                module: &scatter_shader,
                entry_point: Some("scatter_add"),
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
    i: u32,
    j: u32,
    k: u32,
    b: u32,
}

impl Index {
    const WORKGROUP_SIZE: u32 = 256;

    pub fn gather<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        i: usize,
        j: usize,
        k: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let label = "gather";
        let device = &self.device;
        let params = Params {
            i: i as u32,
            j: j as u32,
            k: k as u32,
            b: 0,
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

        let workgroups = result.workgroup_count(Index::WORKGROUP_SIZE);

        ComputeTask {
            label: label,
            pipeline: &self.gather,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(&params).to_vec()),
        }
    }

    pub fn scatter_add<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        i: usize,
        j: usize,
        k: usize,
        b: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let label = "scatter_add";
        let device = &self.device;
        let params = Params {
            i: i as u32,
            j: j as u32,
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

        let workgroups = x.workgroup_count(Index::WORKGROUP_SIZE);

        ComputeTask {
            label: label,
            pipeline: &self.scatter_add,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(&params).to_vec()),
        }
    }
}
