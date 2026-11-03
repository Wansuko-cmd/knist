use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_profiler_take_cpu(runtime: *const Runtime) -> i64 {
    let runtime = unsafe { &*runtime };
    runtime.cpu_profiler.take() as i64
}

/// result: 長さ2のi64配列 [gpu_busy_ns, gpu_span_ns]
#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_gpu_profiler_take_gpu(runtime: *const Runtime, result: *mut i64) {
    let runtime = unsafe { &*runtime };
    let r = runtime.gpu_profiler.take(&runtime.device, &runtime.queue);
    let out = unsafe { std::slice::from_raw_parts_mut(result, 2) };
    out[0] = r.gpu_busy_ns as i64;
    out[1] = r.gpu_span_ns as i64;
}
