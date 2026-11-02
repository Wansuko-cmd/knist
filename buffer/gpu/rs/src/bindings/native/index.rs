use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_gather(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    i: i32,
    j: i32,
    k: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::index::gather(x, y, i as usize, j as usize, k as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_scatter_add(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    i: i32,
    j: i32,
    k: i32,
    b: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::index::scatter_add(
        x, y, i as usize, j as usize, k as usize, b as usize, result, runtime,
    );
}
