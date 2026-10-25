use std::sync::mpsc;

use bytemuck;
use wgpu::{Device, Queue, util::DeviceExt};

pub struct GPUBuffer {
    pub buffer: wgpu::Buffer,
}

impl GPUBuffer {
    pub const SIZE_BYTES: usize = std::mem::size_of::<f32>();
    const MAX_GROUP_WIDTH: u32 = 65535;

    pub fn create(size: usize, device: &Device) -> GPUBuffer {
        let byte_size = (size * GPUBuffer::SIZE_BYTES) as u64;
        let buffer = device.create_buffer(&wgpu::BufferDescriptor {
            label: Some("GPUBuffer::create"),
            size: byte_size,
            usage: wgpu::BufferUsages::STORAGE
                | wgpu::BufferUsages::COPY_SRC
                | wgpu::BufferUsages::COPY_DST,
            mapped_at_creation: false,
        });
        GPUBuffer { buffer }
    }

    pub fn create_map_read(size: usize, device: &Device) -> GPUBuffer {
        let byte_size = (size * GPUBuffer::SIZE_BYTES) as u64;
        let buffer = device.create_buffer(&wgpu::BufferDescriptor {
            label: Some("GPUBufer::create_map_read"),
            size: byte_size,
            usage: wgpu::BufferUsages::MAP_READ | wgpu::BufferUsages::COPY_DST,
            mapped_at_creation: false,
        });
        GPUBuffer { buffer }
    }

    pub fn init(value: &[f32], device: &Device) -> GPUBuffer {
        let buffer = device.create_buffer_init(&wgpu::util::BufferInitDescriptor {
            label: Some("GPUBuffer::init"),
            contents: bytemuck::cast_slice(&value),
            usage: wgpu::BufferUsages::STORAGE
                | wgpu::BufferUsages::COPY_SRC
                | wgpu::BufferUsages::COPY_DST,
        });
        GPUBuffer { buffer }
    }

    pub fn copy_into(self, dest: &mut Vec<f32>, device: &Device) {
        let map_buffer_slice = self.buffer.slice(..);

        let (sender, receiver) = mpsc::sync_channel(1);
        map_buffer_slice.map_async(wgpu::MapMode::Read, move |v| sender.send(v).unwrap());
        let _ = device.poll(wgpu::PollType::wait_indefinitely());
        receiver.recv().unwrap().unwrap();

        let data = map_buffer_slice.get_mapped_range();
        dest.copy_from_slice(bytemuck::cast_slice(&data));
    }

    pub fn write(self: &Self, index: usize, value: f32, queue: &Queue) {
        let offset = (index * GPUBuffer::SIZE_BYTES) as u64;
        queue.write_buffer(&self.buffer, offset, bytemuck::bytes_of(&value));
    }

    pub fn count(self: &Self) -> usize {
        let byte_size = self.buffer.size() as usize;
        byte_size / GPUBuffer::SIZE_BYTES
    }

    pub fn workgroup_count(self: &Self, workgroup_size: u32) -> [u32; 3] {
        let count = self.count() as u32;
        let total = (count + workgroup_size - 1) / workgroup_size;
        if total < GPUBuffer::MAX_GROUP_WIDTH {
            [total, 1, 1]
        } else {
            let y = (total + GPUBuffer::MAX_GROUP_WIDTH - 1) / GPUBuffer::MAX_GROUP_WIDTH;
            let x = (total + y - 1) / y;
            [x, y, 1]
        }
    }
}

impl PartialEq for GPUBuffer {
    fn eq(&self, other: &Self) -> bool {
        self.buffer == other.buffer
    }
}
