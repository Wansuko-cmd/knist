use wgpu::Device;

use crate::{kernels::task::ComputeTask, resource::buffer::GPUBuffer};

pub struct Generator {
    device: Device,

    random_d1: wgpu::ComputePipeline,

    bind_group_layout: wgpu::BindGroupLayout,
}

impl Generator {
    pub fn new(device: &Device) -> Self {
        let shader = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Generator::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("generator.wgsl").into()),
        });

        let bind_group_layout = device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
            label: Some("Generator::new"),
            entries: &[wgpu::BindGroupLayoutEntry {
                binding: 0,
                visibility: wgpu::ShaderStages::COMPUTE,
                ty: wgpu::BindingType::Buffer {
                    ty: wgpu::BufferBindingType::Storage { read_only: false },
                    has_dynamic_offset: false,
                    min_binding_size: None,
                },
                count: None,
            }],
        });

        let pipeline_layout = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
            label: Some("Generator::new"),
            bind_group_layouts: &[&bind_group_layout],
            immediate_size: std::mem::size_of::<Params>() as u32,
        });

        Generator {
            device: device.clone(),
            random_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("random_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("random_d1"),
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
    from: f32,
    until: f32,
    seed: u32,
    _pad: u32,
}

impl Generator {
    const WORKGROUP_SIZE: u32 = 64;

    pub fn random_d1<'a>(
        &'a self,
        from: f32,
        until: f32,
        seed: u32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;
        let params = Params {
            from: from,
            until: until,
            seed: seed,
            _pad: 0,
        };

        let bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            label: Some("random_d1"),
            layout: &self.bind_group_layout,
            entries: &[wgpu::BindGroupEntry {
                binding: 0,
                resource: result.buffer.as_entire_binding(),
            }],
        });

        let workgroups = result.workgroup_count(Generator::WORKGROUP_SIZE);

        ComputeTask {
            label: "random_d1",
            pipeline: &self.random_d1,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(&params).to_vec()),
        }
    }
}
