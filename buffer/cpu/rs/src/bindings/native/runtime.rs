use crate::ops;
use crate::runtime::Runtime;

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_runtime_allocate(pool_size: i64) -> *mut Runtime {
    let runtime = ops::runtime::allocate(pool_size as u64);
    Box::into_raw(Box::new(runtime))
}

#[unsafe(no_mangle)]
pub extern "C" fn com_wsr_cpu_runtime_release(ptr: *mut Runtime) {
    unsafe { drop(Box::from_raw(ptr)) };
}
