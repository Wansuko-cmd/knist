use crate::ops;
use crate::resource::buffer::GPUBuffer;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_transpose_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::transpose_d2(x, xi as usize, xj as usize, result, runtime);
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_transpose_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis_i: i32,
    axis_j: i32,
    axis_k: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::transpose_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis_i as usize,
        axis_j as usize,
        axis_k as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_transpose_d4(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    axis_i: i32,
    axis_j: i32,
    axis_k: i32,
    axis_l: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::transpose_d4(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        axis_i as usize,
        axis_j as usize,
        axis_k as usize,
        axis_l as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_slice_d1(
    x: *const GPUBuffer,
    start: i32,
    end: i32,
    step: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::slice_d1(
        x,
        start as usize,
        end as usize,
        step as isize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_slice_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::slice_d2(
        x,
        xi as usize,
        xj as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_slice_d3(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::slice_d3(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_copy_into_d1(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    start: i32,
    end: i32,
    step: i32,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::copy_into_d1(
        x,
        result,
        start as usize,
        end as usize,
        step as isize,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_copy_into_d2(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    ri: i32,
    rj: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::copy_into_d2(
        x,
        result,
        ri as usize,
        rj as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_copy_into_d3(
    x: *const GPUBuffer,
    result: *const GPUBuffer,
    ri: i32,
    rj: i32,
    rk: i32,
    axis: i32,
    start: i32,
    end: i32,
    step: i32,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::copy_into_d3(
        x,
        result,
        ri as usize,
        rj as usize,
        rk as usize,
        axis as usize,
        start as usize,
        end as usize,
        step as isize,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_flip_d3(
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

    ops::shape::flip_d3(
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
pub extern "C" fn com_wsr_gpu_unfold_d1(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    b: i32,
    window: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::unfold_d1(
        x,
        xi as usize,
        xj as usize,
        b as usize,
        window as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_unfold_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    b: i32,
    window: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::unfold_d2(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        b as usize,
        window as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_fold_d1(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    b: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::fold_d1(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        b as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_fold_d2(
    x: *const GPUBuffer,
    xi: i32,
    xj: i32,
    xk: i32,
    xl: i32,
    b: i32,
    stride: i32,
    dilation: i32,
    padding: i32,
    result: *const GPUBuffer,
    runtime: *mut Runtime,
) {
    let x = unsafe { &*x };
    let result = unsafe { &*result };
    let runtime = unsafe { &mut *runtime };

    ops::shape::fold_d2(
        x,
        xi as usize,
        xj as usize,
        xk as usize,
        xl as usize,
        b as usize,
        stride as usize,
        dilation as usize,
        padding as usize,
        result,
        runtime,
    );
}
