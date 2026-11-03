use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_mat_mul(
    x: *const GPUBuffer,
    trans_x: bool,
    y: *const GPUBuffer,
    trans_y: bool,
    m: i32,
    n: i32,
    k: i32,
    b: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::linalg::mat_mul::mat_mul(
        x, trans_x, y, trans_y, m as usize, n as usize, k as usize, b as usize, result, runtime,
    );
}
