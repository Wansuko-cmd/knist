use crate::ops;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_runtime_allocate(
    pool_size: i64,
    enable_profiler: bool,
) -> *mut Runtime {
    let runtime = ops::runtime::allocate(pool_size as u64, enable_profiler);
    Box::into_raw(Box::new(runtime))
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_runtime_release(ptr: *mut Runtime) {
    unsafe { drop(Box::from_raw(ptr)) };
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_runtime_flush(runtime: *const Runtime) {
    let runtime = unsafe { &*runtime };
    runtime.flush();
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_runtime_sync(runtime: *const Runtime) {
    let runtime = unsafe { &*runtime };
    runtime.flush();
    runtime.wait();
}
