use wgpu::Device;

use crate::{kernels::task::ComputeTask, resource::buffer::GPUBuffer};

pub struct Compare {
    device: Device,

    gt_d1_to_d0: wgpu::ComputePipeline,
    gt_d1_to_d1: wgpu::ComputePipeline,
    lt_d1_to_d0: wgpu::ComputePipeline,
    lt_d1_to_d1: wgpu::ComputePipeline,
    eq_d1_to_d0: wgpu::ComputePipeline,
    eq_d1_to_d1: wgpu::ComputePipeline,
    where_d0_to_d0: wgpu::ComputePipeline,
    where_d0_to_d1: wgpu::ComputePipeline,
    where_d1_to_d0: wgpu::ComputePipeline,
    where_d1_to_d1: wgpu::ComputePipeline,

    bind_group_layout: wgpu::BindGroupLayout,
}

impl Compare {
    pub fn new(device: &Device) -> Self {
        let shader = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Compare::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("compare.wgsl").into()),
        });

        let bind_group_layout = device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
            label: Some("Compare::new"),
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
                        ty: wgpu::BufferBindingType::Storage { read_only: true },
                        has_dynamic_offset: false,
                        min_binding_size: None,
                    },
                    count: None,
                },
                wgpu::BindGroupLayoutEntry {
                    binding: 3,
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
            label: Some("Compare::new"),
            bind_group_layouts: &[&bind_group_layout],
            immediate_size: std::mem::size_of::<Params>() as u32,
        });

        Compare {
            device: device.clone(),
            gt_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("gt_d1_to_d0"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("gt_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            gt_d1_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("gt_d1_to_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("gt_d1_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            lt_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("lt_d1_to_d0"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("lt_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            lt_d1_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("lt_d1_to_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("lt_d1_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            eq_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("eq_d1_to_d0"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("eq_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            eq_d1_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("eq_d1_to_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("eq_d1_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            where_d0_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("where_d0_to_d0"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("where_d0_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            where_d0_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("where_d0_to_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("where_d0_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            where_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("where_d1_to_d0"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("where_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            where_d1_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("where_d1_to_d1"),
                layout: Some(&pipeline_layout),
                module: &shader,
                entry_point: Some("where_d1_to_d1"),
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
    x_val: f32,
    y_val: f32,
    atol: f32,
    rtol: f32,
}

impl Default for Params {
    fn default() -> Self {
        Params {
            x_val: 0f32,
            y_val: 0f32,
            atol: 0f32,
            rtol: 0f32,
        }
    }
}

impl Compare {
    const WORKGROUP_SIZE: u32 = 256;

    pub fn gt_d1_to_d0<'a>(&'a self, x: &GPUBuffer, y: f32, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task(
            "gt_d1_to_d0",
            &self.gt_d1_to_d0,
            // condition, yは使わない
            x,
            x,
            x,
            &Params {
                y_val: y,
                ..Params::default()
            },
            result,
        )
    }

    pub fn gt_d1_to_d1<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "gt_d1_to_d1",
            &self.gt_d1_to_d1,
            // conditionは使わない
            x,
            x,
            y,
            &Params::default(),
            result,
        )
    }

    pub fn lt_d1_to_d0<'a>(&'a self, x: &GPUBuffer, y: f32, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task(
            "lt_d1_to_d0",
            &self.lt_d1_to_d0,
            // condition, yは使わない
            x,
            x,
            x,
            &Params {
                y_val: y,
                ..Params::default()
            },
            result,
        )
    }

    pub fn lt_d1_to_d1<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "lt_d1_to_d1",
            &self.lt_d1_to_d1,
            // conditionは使わない
            x,
            x,
            y,
            &Params::default(),
            result,
        )
    }

    pub fn eq_d1_to_d0<'a>(
        &'a self,
        x: &GPUBuffer,
        y: f32,
        atol: f32,
        rtol: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "eq_d1_to_d0",
            &self.eq_d1_to_d0,
            // condition, yは使わない
            x,
            x,
            x,
            &Params {
                y_val: y,
                atol: atol,
                rtol: rtol,
                ..Params::default()
            },
            result,
        )
    }

    pub fn eq_d1_to_d1<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        atol: f32,
        rtol: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "eq_d1_to_d1",
            &self.eq_d1_to_d1,
            // conditionは使わない
            x,
            x,
            y,
            &Params {
                atol: atol,
                rtol: rtol,
                ..Params::default()
            },
            result,
        )
    }

    pub fn where_d0_to_d0<'a>(
        &'a self,
        condition: &GPUBuffer,
        x: f32,
        y: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "where_d0_to_d0",
            &self.where_d0_to_d0,
            // x, yは使わない
            condition,
            condition,
            condition,
            &Params {
                x_val: x,
                y_val: y,
                ..Params::default()
            },
            result,
        )
    }

    pub fn where_d0_to_d1<'a>(
        &'a self,
        condition: &GPUBuffer,
        x: f32,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "where_d0_to_d1",
            &self.where_d0_to_d1,
            // xは使わない
            condition,
            y,
            y,
            &Params {
                x_val: x,
                ..Params::default()
            },
            result,
        )
    }

    pub fn where_d1_to_d0<'a>(
        &'a self,
        condition: &GPUBuffer,
        x: &GPUBuffer,
        y: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "where_d1_to_d1",
            &self.where_d1_to_d0,
            // yは使わない
            condition,
            x,
            x,
            &Params {
                y_val: y,
                ..Params::default()
            },
            result,
        )
    }

    pub fn where_d1_to_d1<'a>(
        &'a self,
        condition: &GPUBuffer,
        x: &GPUBuffer,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task(
            "where_d1_to_d1",
            &self.where_d1_to_d1,
            condition,
            x,
            y,
            &Params::default(),
            result,
        )
    }

    fn create_task<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        condition: &GPUBuffer,
        x: &GPUBuffer,
        y: &GPUBuffer,
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
                    resource: condition.buffer.as_entire_binding(),
                },
                wgpu::BindGroupEntry {
                    binding: 1,
                    resource: x.buffer.as_entire_binding(),
                },
                wgpu::BindGroupEntry {
                    binding: 2,
                    resource: y.buffer.as_entire_binding(),
                },
                wgpu::BindGroupEntry {
                    binding: 3,
                    resource: result.buffer.as_entire_binding(),
                },
            ],
        });

        let workgroups = result.workgroup_count(Compare::WORKGROUP_SIZE);

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(params).to_vec()),
        }
    }
}
