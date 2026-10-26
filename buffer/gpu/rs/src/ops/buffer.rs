use crate::{kernels::task::CopyTask, resource::buffer::GPUBuffer, runtime::Runtime};

pub fn create(size: usize, runtime: &Runtime) -> GPUBuffer {
    let _t = runtime.cpu_profiler.start();
    runtime.create_buffer(size)
}

pub fn init(value: &[f32], runtime: &Runtime) -> GPUBuffer {
    let _t = runtime.cpu_profiler.start();
    runtime.init_buffer(value)
}

pub fn release(buffer: GPUBuffer, runtime: &Runtime) {
    runtime.release_buffer(buffer);
}

pub fn read_all(buffer: &GPUBuffer, runtime: &mut Runtime) -> Vec<f32> {
    let size = buffer.count();
    let map_buffer = runtime.create_map_read_buffer(size);
    runtime.dispatch(CopyTask {
        src: buffer,
        src_offset: 0,
        dest: &map_buffer,
        dest_offset: 0,
        size: size,
    });
    runtime.flush();

    map_buffer.to_vec(&runtime.device)
}

pub fn write(buffer: &GPUBuffer, index: usize, value: f32, runtime: &mut Runtime) {
    runtime.flush();
    buffer.write(index, value, &runtime.queue);
}
