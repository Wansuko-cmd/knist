use std::collections::HashMap;
use std::sync::Mutex;

use crate::resource::buffer::CPUBuffer;

pub struct BufferPool {
    mutex: Mutex<Inner>,
    size: u64,
}

struct Inner {
    pool: HashMap<u64, Vec<CPUBuffer>>,
    total_bytes: u64,
}

impl BufferPool {
    pub fn new(size: u64) -> Self {
        BufferPool {
            mutex: Mutex::new(Inner {
                pool: HashMap::new(),
                total_bytes: 0,
            }),
            size: size,
        }
    }

    pub fn get(&self, size: usize) -> Option<CPUBuffer> {
        let byte_size = (size * CPUBuffer::SIZE_BYTES) as u64;
        let mut mutex = self.mutex.lock().unwrap();
        let buffer = mutex.pool.get_mut(&byte_size).and_then(|list| list.pop());
        if buffer.is_some() {
            mutex.total_bytes -= byte_size;
        }
        buffer
    }

    pub fn release(&self, buffer: CPUBuffer) {
        let byte_size = (buffer.count() * CPUBuffer::SIZE_BYTES) as u64;
        let mut mutex = self.mutex.lock().unwrap();
        if mutex.total_bytes + byte_size <= self.size {
            mutex.total_bytes += byte_size;
            mutex.pool.entry(byte_size).or_default().push(buffer);
        }
    }
}
