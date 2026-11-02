use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d0_to_d1(
    x: f32,
    y: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d0_to_d1(x as f32, &y, &result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d1_to_d0(
    x: *const GPUBuffer,
    y: f32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d1_to_d0(&x, y as f32, &result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d1_to_d1(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d1_to_d1(&x, &y, &result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d1_to_d2(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    yi: i32,
    yj: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d1_to_d2(
        &x,
        &y,
        yi as usize,
        yj as usize,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d1_to_d3(
    x: *const GPUBuffer,
    y: *const GPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d1_to_d3(
        &x,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d2_to_d1(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    y: *const GPUBuffer,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d2_to_d1(
        &x,
        xi as usize,
        xj as usize,
        &y,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d2_to_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    y: *const GPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    axis1: i32,
    axis2: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d2_to_d3(
        &x,
        xi as usize,
        xj as usize,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis1 as usize,
        axis2 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d3_to_d1(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    y: *const GPUBuffer,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d3_to_d1(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        &y,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d3_to_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    y: *const GPUBuffer,
    yi: i32,
    yj: i32,
    axis1: i32,
    axis2: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d3_to_d2(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        &y,
        yi as usize,
        yj as usize,
        axis1 as usize,
        axis2 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d3_to_d4(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    y: *const GPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    yl: i32,
    axis1: i32,
    axis2: i32,
    axis3: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d3_to_d4(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        yl as usize,
        axis1 as usize,
        axis2 as usize,
        axis3 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d4_to_d1(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    y: *const GPUBuffer,
    axis: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d4_to_d1(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        &y,
        axis as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d4_to_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    y: *const GPUBuffer,
    yi: i32,
    yj: i32,
    axis1: i32,
    axis2: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d4_to_d2(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        &y,
        yi as usize,
        yj as usize,
        axis1 as usize,
        axis2 as usize,
        &result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_plus_d4_to_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    y: *const GPUBuffer,
    yi: i32,
    yj: i32,
    yk: i32,
    axis1: i32,
    axis2: i32,
    axis3: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let y = unsafe { &*y };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::elementwise::operation::plus_with_d4_to_d3(
        &x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        &y,
        yi as usize,
        yj as usize,
        yk as usize,
        axis1 as usize,
        axis2 as usize,
        axis3 as usize,
        &result,
        runtime,
    );
}
