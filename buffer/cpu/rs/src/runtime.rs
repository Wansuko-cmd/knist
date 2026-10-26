pub mod pool;

use crate::resource::buffer::CPUBuffer;
use crate::runtime::pool::BufferPool;

pub struct Runtime {
    buffer_pool: BufferPool,
}

impl Runtime {
    pub async fn new(pool_size: u64) -> Self {
        let buffer_pool = BufferPool::new(pool_size);

        Runtime {
            buffer_pool: buffer_pool,
        }
    }
}

impl Runtime {
    pub fn create_buffer(&self, size: usize) -> CPUBuffer {
        match self.buffer_pool.get(size) {
            Some(mut buffer) => {
                buffer.fill(0f32);
                buffer
            }
            None => CPUBuffer::create(size),
        }
    }

    pub fn release_buffer(&self, buffer: CPUBuffer) {
        self.buffer_pool.release(buffer);
    }

    pub fn init_buffer(&self, value: &[f32]) -> CPUBuffer {
        CPUBuffer::init(value)
    }
}
