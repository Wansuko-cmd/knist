use std::ops::{Deref, DerefMut};

pub struct CPUBuffer {
    pub value: Vec<f32>,
}

impl CPUBuffer {
    pub const SIZE_BYTES: usize = std::mem::size_of::<f32>();

    pub fn create(size: usize) -> CPUBuffer {
        let value = vec![0f32; size];
        CPUBuffer { value: value }
    }

    pub fn init(value: &[f32]) -> CPUBuffer {
        CPUBuffer {
            value: value.to_vec(),
        }
    }

    pub fn write(&mut self, index: usize, value: f32) {
        self.value[index] = value
    }

    pub fn count(&self) -> usize {
        self.value.len()
    }
}

impl Deref for CPUBuffer {
    type Target = [f32];

    fn deref(&self) -> &[f32] {
        &self.value
    }
}

impl DerefMut for CPUBuffer {
    fn deref_mut(&mut self) -> &mut [f32] {
        &mut self.value
    }
}

impl PartialEq for CPUBuffer {
    fn eq(&self, other: &Self) -> bool {
        self.value == other.value
    }
}
