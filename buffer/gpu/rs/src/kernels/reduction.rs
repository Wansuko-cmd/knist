use wgpu::Device;

use crate::{kernels::task::ComputeTask, resource::buffer::GPUBuffer};

pub struct Reduction {
    device: Device,

    average_d1: wgpu::ComputePipeline,
    max_d1: wgpu::ComputePipeline,
    min_d1: wgpu::ComputePipeline,
    sum_d1: wgpu::ComputePipeline,

    average_d2_axis0: wgpu::ComputePipeline,
    average_d2_axis1: wgpu::ComputePipeline,
    max_d2_axis0: wgpu::ComputePipeline,
    max_d2_axis1: wgpu::ComputePipeline,
    min_d2_axis0: wgpu::ComputePipeline,
    min_d2_axis1: wgpu::ComputePipeline,
    sum_d2_axis0: wgpu::ComputePipeline,
    sum_d2_axis1: wgpu::ComputePipeline,

    average_d3: wgpu::ComputePipeline,
    max_d3: wgpu::ComputePipeline,
    min_d3: wgpu::ComputePipeline,
    sum_d3: wgpu::ComputePipeline,

    bind_group_layout: wgpu::BindGroupLayout,
}

impl Reduction {
    const WORKGROUP_SIZE: u32 = 256;

    pub fn new(device: &Device) -> Self {
        let shader_d1 = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Reduction::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("reduction/collection_d1.wgsl").into()),
        });
        let shader_d2 = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Reduction::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("reduction/collection_d2.wgsl").into()),
        });
        let shader_d3 = device.create_shader_module(wgpu::ShaderModuleDescriptor {
            label: Some("Reduction::new"),
            source: wgpu::ShaderSource::Wgsl(include_str!("reduction/collection_d3.wgsl").into()),
        });

        let bind_group_layout = device.create_bind_group_layout(&wgpu::BindGroupLayoutDescriptor {
            label: Some("Reduction::new"),
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

        let immediate_size = std::mem::size_of::<D2Params>().max(std::mem::size_of::<D3Params>());
        let pipeline_layout = device.create_pipeline_layout(&wgpu::PipelineLayoutDescriptor {
            label: Some("Reduction::new"),
            bind_group_layouts: &[&bind_group_layout],
            immediate_size: immediate_size as u32,
        });

        Reduction {
            device: device.clone(),
            average_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("average_d1"),
                layout: Some(&pipeline_layout),
                module: &shader_d1,
                entry_point: Some("average_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            max_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("max_d1"),
                layout: Some(&pipeline_layout),
                module: &shader_d1,
                entry_point: Some("max_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            min_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("min_d1"),
                layout: Some(&pipeline_layout),
                module: &shader_d1,
                entry_point: Some("min_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            sum_d1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("sum_d1"),
                layout: Some(&pipeline_layout),
                module: &shader_d1,
                entry_point: Some("sum_d1"),
                compilation_options: Default::default(),
                cache: None,
            }),

            average_d2_axis0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("average_d2_axis0"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("average_d2_axis0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            average_d2_axis1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("average_d2_axis1"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("average_d2_axis1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            max_d2_axis0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("max_d2_axis0"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("max_d2_axis0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            max_d2_axis1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("max_d2_axis1"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("max_d2_axis1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            min_d2_axis0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("min_d2_axis0"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("min_d2_axis0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            min_d2_axis1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("min_d2_axis1"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("min_d2_axis1"),
                compilation_options: Default::default(),
                cache: None,
            }),
            sum_d2_axis0: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("sum_d2_axis0"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("sum_d2_axis0"),
                compilation_options: Default::default(),
                cache: None,
            }),
            sum_d2_axis1: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("sum_d2_axis1"),
                layout: Some(&pipeline_layout),
                module: &shader_d2,
                entry_point: Some("sum_d2_axis1"),
                compilation_options: Default::default(),
                cache: None,
            }),

            average_d3: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("average_d3"),
                layout: Some(&pipeline_layout),
                module: &shader_d3,
                entry_point: Some("average_d3"),
                compilation_options: Default::default(),
                cache: None,
            }),
            max_d3: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("max_d3"),
                layout: Some(&pipeline_layout),
                module: &shader_d3,
                entry_point: Some("max_d3"),
                compilation_options: Default::default(),
                cache: None,
            }),
            min_d3: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("min_d3"),
                layout: Some(&pipeline_layout),
                module: &shader_d3,
                entry_point: Some("min_d3"),
                compilation_options: Default::default(),
                cache: None,
            }),
            sum_d3: device.create_compute_pipeline(&wgpu::ComputePipelineDescriptor {
                label: Some("sum_d3"),
                layout: Some(&pipeline_layout),
                module: &shader_d3,
                entry_point: Some("sum_d3"),
                compilation_options: Default::default(),
                cache: None,
            }),
            bind_group_layout: bind_group_layout,
        }
    }
}

impl Reduction {
    pub fn average_d1<'a>(&'a self, x: &GPUBuffer, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task_d1("average_d1", &self.average_d1, x, result)
    }

    pub fn max_d1<'a>(&'a self, x: &GPUBuffer, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task_d1("max_d1", &self.max_d1, x, result)
    }

    pub fn min_d1<'a>(&'a self, x: &GPUBuffer, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task_d1("min_d1", &self.min_d1, x, result)
    }

    pub fn sum_d1<'a>(&'a self, x: &GPUBuffer, result: &GPUBuffer) -> ComputeTask<'a> {
        self.create_task_d1("sum_d1", &self.sum_d1, x, result)
    }

    fn create_task_d1<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: &GPUBuffer,
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

        let workgroups = [1; 3];

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: None,
        }
    }
}

#[repr(C)]
#[derive(Copy, Clone, bytemuck::Pod, bytemuck::Zeroable)]
struct D2Params {
    xi: u32,
    xj: u32,
    _pad1: u32,
    _pad2: u32,
}

impl Reduction {
    pub fn average_d2_axis0<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2(
            "average_d2_axis0",
            &self.average_d2_axis0,
            x,
            xi,
            xj,
            result,
        )
    }

    pub fn average_d2_axis1<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2(
            "average_d2_axis1",
            &self.average_d2_axis1,
            x,
            xi,
            xj,
            result,
        )
    }

    pub fn max_d2_axis0<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2("max_d2_axis0", &self.max_d2_axis0, x, xi, xj, result)
    }

    pub fn max_d2_axis1<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2("max_d2_axis1", &self.max_d2_axis1, x, xi, xj, result)
    }

    pub fn min_d2_axis0<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2("min_d2_axis0", &self.min_d2_axis0, x, xi, xj, result)
    }

    pub fn min_d2_axis1<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2("min_d2_axis1", &self.min_d2_axis1, x, xi, xj, result)
    }

    pub fn sum_d2_axis0<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2("sum_d2_axis0", &self.sum_d2_axis0, x, xi, xj, result)
    }

    pub fn sum_d2_axis1<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d2("sum_d2_axis1", &self.sum_d2_axis1, x, xi, xj, result)
    }

    fn create_task_d2<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;
        let params = D2Params {
            xi: xi as u32,
            xj: xj as u32,
            _pad1: 0,
            _pad2: 0,
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
                    resource: result.buffer.as_entire_binding(),
                },
            ],
        });

        let workgroups = result.workgroup_count(Reduction::WORKGROUP_SIZE);

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(&params).to_vec()),
        }
    }
}

#[repr(C)]
#[derive(Copy, Clone, bytemuck::Pod, bytemuck::Zeroable)]
struct D3Params {
    xi: u32,
    xj: u32,
    xk: u32,
    _pad: u32,
}

impl Reduction {
    pub fn average_d3<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        xk: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d3("average_d3", &self.average_d3, x, xi, xj, xk, result)
    }

    pub fn max_d3<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        xk: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d3("max_d3", &self.max_d3, x, xi, xj, xk, result)
    }

    pub fn min_d3<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        xk: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d3("min_d3", &self.min_d3, x, xi, xj, xk, result)
    }

    pub fn sum_d3<'a>(
        &'a self,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        xk: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        self.create_task_d3("sum_d3", &self.sum_d3, x, xi, xj, xk, result)
    }

    fn create_task_d3<'a>(
        &'a self,
        label: &'static str,
        pipeline: &'a wgpu::ComputePipeline,
        x: &GPUBuffer,
        xi: usize,
        xj: usize,
        xk: usize,
        result: &GPUBuffer,
    ) -> ComputeTask<'a> {
        let device = &self.device;
        let params = D3Params {
            xi: xi as u32,
            xj: xj as u32,
            xk: xk as u32,
            _pad: 0,
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
                    resource: result.buffer.as_entire_binding(),
                },
            ],
        });

        let workgroups = result.workgroup_count(Reduction::WORKGROUP_SIZE);

        ComputeTask {
            label: label,
            pipeline: pipeline,
            bind_group: bind_group,
            workgroups: workgroups,
            params: Some(bytemuck::bytes_of(&params).to_vec()),
        }
    }
}
