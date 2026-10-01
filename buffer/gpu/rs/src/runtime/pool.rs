use std::collections::{HashMap, VecDeque};
use std::sync::Mutex;

use crate::resource::buffer::GPUBuffer;

pub struct BufferPool {
    mutex: Mutex<Inner>,
    max_bytes: u64,
}

struct Inner {
    // バッファサイズ to (連番 to GPUBuffer)[古い順]
    pool: HashMap<u64, VecDeque<(u64, GPUBuffer)>>,
    next_seq: u64,
    num_of_buffer: usize,
    total_bytes: u64,
}

impl Inner {
    fn new() -> Self {
        Inner { pool: HashMap::new(), next_seq: 0, num_of_buffer: 0, total_bytes: 0 }
    }

    fn take(&mut self, byte_size: u64) -> Option<GPUBuffer> {
        let list = self.pool.get_mut(&byte_size)?;
        let (_, buffer) = list.pop_back()?;
        if list.is_empty() {
            self.pool.remove(&byte_size);
        }
        self.num_of_buffer -= 1;
        self.total_bytes -= byte_size;
        Some(buffer)
    }

    fn push(&mut self, byte_size: u64, buffer: GPUBuffer) {
        let seq = self.next_seq;
        self.next_seq += 1;
        self.pool.entry(byte_size).or_default().push_back((seq, buffer));
        self.num_of_buffer += 1;
        self.total_bytes += byte_size;
    }

    fn pop_oldest(&mut self) -> Option<GPUBuffer> {
        // もっとも古いbufferがあるsizeを取得
        let (_, oldest_size) = self
            .pool
            .iter()
            .filter_map(|(size, list)| list.front().map(|(seq, _)| (*seq, *size)))
            .min()?;
        let list = self.pool.get_mut(&oldest_size)?;
        let (_, buffer) = list.pop_front()?;
        if list.is_empty() {
            self.pool.remove(&oldest_size);
        }
        self.num_of_buffer -= 1;
        self.total_bytes -= oldest_size;
        Some(buffer)
    }
}

impl BufferPool {
    // 最大バッファ数(多すぎると遅くなる)
    const MAX_NUM_OF_BUFFER: usize = 4096;

    pub fn new(max_bytes: u64) -> Self {
        BufferPool { mutex: Mutex::new(Inner::new()), max_bytes }
    }

    pub fn get(&self, size: usize) -> Option<GPUBuffer> {
        let byte_size = (size * GPUBuffer::SIZE_BYTES) as u64;
        self.mutex.lock().unwrap().take(byte_size)
    }

    pub fn release(&self, buffer: GPUBuffer) {
        let byte_size = buffer.buffer.size();
        if byte_size > self.max_bytes {
            return;
        }

        // 用済みのbufferをpoolに追加
        let mut inner = self.mutex.lock().unwrap();
        inner.push(byte_size, buffer);

        // poolが溢れた分古い値を削除
        while inner.num_of_buffer > Self::MAX_NUM_OF_BUFFER || inner.total_bytes > self.max_bytes {
            inner.pop_oldest();
        }
    }
}
