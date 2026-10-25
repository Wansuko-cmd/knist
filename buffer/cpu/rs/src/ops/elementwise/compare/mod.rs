use crate::resource::buffer::CPUBuffer;

pub mod r#where;

pub fn greater_than_d1_to_d0(x: &CPUBuffer, y: f32, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    for (&val, res) in x.iter().zip(result.iter_mut()) {
        *res = if val > y { 1f32 } else { 0f32 };
    }
}

pub fn greater_than_d1_to_d1(x: &CPUBuffer, y: &CPUBuffer, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    assert_eq!(y.count(), result.count());
    for (i, value) in result.iter_mut().enumerate() {
        *value = if x[i] > y[i] { 1f32 } else { 0f32 };
    }
}

pub fn less_than_d1_to_d0(x: &CPUBuffer, y: f32, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    for (&val, res) in x.iter().zip(result.iter_mut()) {
        *res = if val < y { 1f32 } else { 0f32 };
    }
}

pub fn less_than_d1_to_d1(x: &CPUBuffer, y: &CPUBuffer, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    assert_eq!(y.count(), result.count());
    for (i, value) in result.iter_mut().enumerate() {
        *value = if x[i] < y[i] { 1f32 } else { 0f32 };
    }
}

pub fn equals_d1_to_d0(x: &CPUBuffer, y: f32, atol: f32, rtol: f32, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    let tolerance = atol + rtol * y.abs();
    for (&val, res) in x.iter().zip(result.iter_mut()) {
        *res = if (val - y).abs() <= tolerance {
            1f32
        } else {
            0f32
        };
    }
}

pub fn equals_d1_to_d1(x: &CPUBuffer, y: &CPUBuffer, atol: f32, rtol: f32, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    assert_eq!(y.count(), result.count());
    for (i, value) in result.iter_mut().enumerate() {
        let tolerance = atol + rtol * y[i].abs();
        *value = if (x[i] - y[i]).abs() <= tolerance {
            1f32
        } else {
            0f32
        };
    }
}
