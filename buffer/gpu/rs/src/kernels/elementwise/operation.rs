use wgpu::Device;

use crate::{kernels::task::ComputeTask, resource::buffer::GPUBuffer};

pub struct Operation {
    device: Device,

    plus_d0_to_d1: wgpu::ComputePipeline,
    plus_d1_to_d0: wgpu::ComputePipeline,
    minus_d0_to_d1: wgpu::ComputePipeline,
    minus_d1_to_d0: wgpu::ComputePipeline,
    times_d0_to_d1: wgpu::ComputePipeline,
    times_d1_to_d0: wgpu::ComputePipeline,
    div_d0_to_d1: wgpu::ComputePipeline,
    div_d1_to_d0: wgpu::ComputePipeline,

    plus_d4: wgpu::ComputePipeline,
    minus_d4: wgpu::ComputePipeline,
    times_d4: wgpu::ComputePipeline,
    div_d4: wgpu::ComputePipeline,

    bind_group_layout_d0: wgpu::BindGroupLayout,
    bind_group_layout_d4: wgpu::BindGroupLayout,
}

impl Operation {
    pub fn new(device: &Device) -> Self {
        let shader_d0 = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Operation::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("operation_d0.wgsl").into()),
        });
        let bind_group_layout_d0 =
            device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
                label: Some("Operation::new"),
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
        let pipeline_layout_d0 = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
            label: Some("Operation::new"),
            bind_group_layouts: &[&bind_group_layout_d0],
            immediate_size: std::mem::size_of::<ParamsD0>() as u32,
        });

        let shader_d4 = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Operation::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("operation_d4.wgsl").into()),
        });
        let bind_group_layout_d4 =
            device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
                label: Some("Operation::new"),
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
        let pipeline_layout_d4 = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
            label: Some("Operation::new"),
            bind_group_layouts: &[&bind_group_layout_d4],
            immediate_size: std::mem::size_of::<ParamsD4>() as u32,
        });

        Operation {
            device: device.clone(),
            plus_d0_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("plus_d0_to_d1"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("plus_d0_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            plus_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("plus_d1_to_d0"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("plus_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            minus_d0_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("minus_d0_to_d1"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("minus_d0_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            minus_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("minus_d1_to_d0"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("minus_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            times_d0_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("times_d0_to_d1"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("times_d0_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            times_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("times_d1_to_d0"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("times_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            div_d0_to_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("div_d0_to_d1"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("div_d0_to_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            div_d1_to_d0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("div_d1_to_d0"),
                layout: Some(&pipeline_layout_d0),
                module: &shader_d0,
                entry_point: Some("div_d1_to_d0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            plus_d4: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("plus_d4"),
                layout: Some(&pipeline_layout_d4),
                module: &shader_d4,
                entry_point: Some("plus_d4"),
                compilation_options: Default::default(),
                cache: None,
            }),
            minus_d4: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("minus_d4"),
                layout: Some(&pipeline_layout_d4),
                module: &shader_d4,
                entry_point: Some("minus_d4"),
                compilation_options: Default::default(),
                cache: None,
            }),
            times_d4: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("times_d4"),
                layout: Some(&pipeline_layout_d4),
                module: &shader_d4,
                entry_point: Some("times_d4"),
                compilation_options: Default::default(),
                cache: None,
            }),
            div_d4: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("div_d4"),
                layout: Some(&pipeline_layout_d4),
                module: &shader_d4,
                entry_point: Some("div_d4"),
                compilation_options: Default::default(),
                cache: None,
            }),
            bind_group_layout_d0: bind_group_layout_d0,
            bind_group_layout_d4: bind_group_layout_d4,
        }
    }
}

#[repr(C)]
#[derive(Copy, Clone, bytemuck::Pod, bytemuck::Zeroable)]
struct ParamsD0 {
    d0: f32,
    _pad1: u32,
    _pad2: u32,
    _pad3: u32,
}

impl Operation {
    const WORKGROUP_SIZE_D0: u32 = 256;

    pub fn plus_d0_to_d1<'a>(
        &'a self,
        x: f32,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d0_to_d1("plus_d0_to_d1", &self.plus_d0_to_d1, x, y, result)
    }

    pub fn plus_d1_to_d0<'a>(
        &'a self,
        x: &GPUBuffer,
        y: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d1_to_d0("plus_d1_to_d0", &self.plus_d1_to_d0, x, y, result)
    }

    pub fn minus_d0_to_d1<'a>(
        &'a self,
        x: f32,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d0_to_d1("minus_d0_to_d1", &self.minus_d0_to_d1, x, y, result)
    }

    pub fn minus_d1_to_d0<'a>(
        &'a self,
        x: &GPUBuffer,
        y: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d1_to_d0("minus_d1_to_d0", &self.minus_d1_to_d0, x, y, result)
    }

    pub fn times_d0_to_d1<'a>(
        &'a self,
        x: f32,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d0_to_d1("times_d0_to_d1", &self.times_d0_to_d1, x, y, result)
    }

    pub fn times_d1_to_d0<'a>(
        &'a self,
        x: &GPUBuffer,
        y: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d1_to_d0("times_d1_to_d0", &self.times_d1_to_d0, x, y, result)
    }

    pub fn div_d0_to_d1<'a>(
        &'a self,
        x: f32,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d0_to_d1("div_d0_to_d1", &self.div_d0_to_d1, x, y, result)
    }

    pub fn div_d1_to_d0<'a>(
        &'a self,
        x: &GPUBuffer,
        y: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d1_to_d0("div_d1_to_d0", &self.div_d1_to_d0, x, y, result)
    }

    fn create_task_d0_to_d1<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: f32,
        y: &GPUBuffer,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;

        let params = &ParamsD0 {
            d0: x,
            _pad1: 0,
            _pad2: 0,
            _pad3: 0,
        };
        let bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            label: Some(label),
            layout: &self.bind_group_layout_d0,
            entries: &[
                wgpu::BindGroupEntry {
                    binding: 0,
                    resource: y.buffer.as_entire_binding(),
                },
                wgpu::BindGroupEntry {
                    binding: 1,
                    resource: result.buffer.as_entire_binding(),
                },
            ],
        });

        let workgroups = result.workgroup_count(Operation::WORKGROUP_SIZE_D0);

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(params).to_vec()),
        }
    }

    fn create_task_d1_to_d0<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: &GPUBuffer,
        y: f32,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;

        let params = &ParamsD0 {
            d0: y,
            _pad1: 0,
            _pad2: 0,
            _pad3: 0,
        };
        let bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            label: Some(label),
            layout: &self.bind_group_layout_d0,
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

        let workgroups = result.workgroup_count(Operation::WORKGROUP_SIZE_D0);

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(params).to_vec()),
        }
    }
}

#[repr(C)]
#[derive(Copy, Clone, bytemuck::Pod, bytemuck::Zeroable)]
struct ParamsD4 {
    result_shape: [u32; 4],
    x_stride: [u32; 4],
    y_stride: [u32; 4],
}

impl Operation {
    const WORKGROUP_SIZE: u32 = 256;

    pub fn plus_d4<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        result_shape: [u32; 4],
        x_stride: [u32; 4],
        y_stride: [u32; 4],
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d4(
            "plus_d4",
            &self.plus_d4,
            x,
            y,
            &ParamsD4 {
                result_shape: result_shape,
                x_stride: x_stride,
                y_stride: y_stride,
            },
            result,
        )
    }

    pub fn minus_d4<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        result_shape: [u32; 4],
        x_stride: [u32; 4],
        y_stride: [u32; 4],
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d4(
            "minus_d4",
            &self.minus_d4,
            x,
            y,
            &ParamsD4 {
                result_shape: result_shape,
                x_stride: x_stride,
                y_stride: y_stride,
            },
            result,
        )
    }

    pub fn times_d4<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        result_shape: [u32; 4],
        x_stride: [u32; 4],
        y_stride: [u32; 4],
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d4(
            "times_d4",
            &self.times_d4,
            x,
            y,
            &ParamsD4 {
                result_shape: result_shape,
                x_stride: x_stride,
                y_stride: y_stride,
            },
            result,
        )
    }

    pub fn div_d4<'a>(
        &'a self,
        x: &GPUBuffer,
        y: &GPUBuffer,
        result_shape: [u32; 4],
        x_stride: [u32; 4],
        y_stride: [u32; 4],
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d4(
            "div_d4",
            &self.div_d4,
            x,
            y,
            &ParamsD4 {
                result_shape: result_shape,
                x_stride: x_stride,
                y_stride: y_stride,
            },
            result,
        )
    }

    fn create_task_d4<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: &GPUBuffer,
        y: &GPUBuffer,
        params: &ParamsD4,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;

        let bind_group = device.create_bind_group(&wgpu::BindGroupDescriptor {
            label: Some(label),
            layout: &self.bind_group_layout_d4,
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

        let workgroups = result.workgroup_count(Operation::WORKGROUP_SIZE);

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(params).to_vec()),
        }
    }
}
