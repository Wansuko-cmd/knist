use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_average_d1(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::average_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_average_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::average_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_average_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::average_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_max_d1(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::max_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_max_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::max_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_max_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::max_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_min_d1(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::min_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_min_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::min_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_min_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::min_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_sum_d1(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::sum_d1(x, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_sum_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::sum_d2(x, xi as usize, xj as usize, axis as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_sum_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::reduction::sum_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        result,
        runtime,
    );
}
