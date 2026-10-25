use crate::ops::utils::FastForEachExt;
use crate::resource::buffer::CPUBuffer;

const EXP_D1_PAR_THRESHOLD: usize = 200_000;
const SIGMOID_D1_PAR_THRESHOLD: usize = 200_000;
const LN_D1_PAR_THRESHOLD: usize = 200_000;

const LOG2E: f32 = std::f32::consts::LOG2_E;

// f32 - f32(整数)をした時に精度を落とさないよう、二回に分けて引くときに使う
const LN2_HI: f32 = 0.693_359_375;
const LN2_LO: f32 = -2.121_944_4e-4;

// この範囲外はf32でオーバーフローする
const EXP_HI: f32 = 88.0;
const EXP_LO: f32 = -87.336_55;

// 小数部を落とす際に使う定数(1.5 * 2^23)
const MAGIC: f32 = 12_582_912.0;

pub fn exp_d1(x: &CPUBuffer, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    x.fast_map(EXP_D1_PAR_THRESHOLD, result, |&val| exp(val));
}

pub fn ln_d1(x: &CPUBuffer, e: f32, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    x.fast_map(LN_D1_PAR_THRESHOLD, result, |&val| (val + e).ln());
}

pub fn sigmoid_d1(x: &CPUBuffer, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    x.fast_map(SIGMOID_D1_PAR_THRESHOLD, result, |&val| {
        1f32 / (1f32 + exp(-val))
    });
}

pub fn pow_d1(x: &CPUBuffer, n: i32, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    for (&val, res) in x.iter().zip(result.iter_mut()) {
        *res = val.powi(n);
    }
}

pub fn sqrt_d1(x: &CPUBuffer, e: f32, result: &mut CPUBuffer) {
    assert_eq!(x.count(), result.count());
    for (&val, res) in x.iter().zip(result.iter_mut()) {
        *res = (val + e).sqrt();
    }
}

#[inline(always)]
fn exp(x: f32) -> f32 {
    // 整数部
    let n = (x * LOG2E + MAGIC) - MAGIC;
    let scale = f32::from_bits((((n as i32) + 127) << 23) as u32);

    // 小数部
    let r = x - n * LN2_HI - n * LN2_LO;

    // 係数はCephes Math Libraryのexpf実装参考
    let mut y = 1.987_569_1e-4f32;
    y = y * r + 1.398_199_9e-3;
    y = y * r + 8.333_452e-3;
    y = y * r + 4.166_579_6e-2;
    y = y * r + 1.666_666_5e-1;
    y = y * r + 5.000_000_1e-1;
    y = y * r * r + r + 1.0;

    match x {
        _ if x < EXP_LO => 0f32,
        _ if EXP_HI < x => f32::INFINITY,
        _ if x != x => x,
        _ => y * scale,
    }
}
