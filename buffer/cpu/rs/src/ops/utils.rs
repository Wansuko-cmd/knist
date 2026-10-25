use rayon::{
    iter::{
        IndexedParallelIterator, IntoParallelRefIterator, IntoParallelRefMutIterator,
        ParallelIterator,
    },
    slice::ParallelSliceMut,
};

const MIN_CHUNK_SIZE: usize = 16;

pub(crate) trait FastForEachExt<T> {
    fn fast_map<D, F>(&self, threshold: usize, dst: &mut [D], f: F)
    where
        T: Sync,
        D: Send,
        F: Fn(&T) -> D + Sync;

    fn fast_for_each<F>(&mut self, threshold: usize, f: F)
    where
        T: Send,
        F: Fn(usize, &mut T) + Sync;

    fn fast_chunks_for_each<F>(&mut self, chunk_size: usize, threshold: usize, f: F)
    where
        T: Send,
        F: Fn(usize, &mut [T]) + Sync;
}

impl<T> FastForEachExt<T> for [T] {
    fn fast_map<D, F>(&self, threshold: usize, dst: &mut [D], f: F)
    where
        T: Sync,
        D: Send,
        F: Fn(&T) -> D + Sync,
    {
        if self.len() >= threshold {
            dst.par_iter_mut()
                .zip(self.par_iter())
                .for_each(|(r, v)| *r = f(v));
        } else {
            dst.iter_mut().zip(self.iter()).for_each(|(r, v)| *r = f(v));
        }
    }

    fn fast_for_each<F>(&mut self, threshold: usize, f: F)
    where
        T: Send,
        F: Fn(usize, &mut T) + Sync,
    {
        if self.len() >= threshold {
            self.par_iter_mut().enumerate().for_each(|(i, v)| f(i, v));
        } else {
            self.iter_mut().enumerate().for_each(|(i, v)| f(i, v));
        }
    }

    fn fast_chunks_for_each<F>(&mut self, chunk_size: usize, threshold: usize, f: F)
    where
        T: Send,
        F: Fn(usize, &mut [T]) + Sync,
    {
        if self.len() >= threshold && chunk_size >= MIN_CHUNK_SIZE {
            self.par_chunks_mut(chunk_size)
                .enumerate()
                .for_each(|(i, chunk)| f(i, chunk));
        } else {
            self.chunks_mut(chunk_size)
                .enumerate()
                .for_each(|(i, chunk)| f(i, chunk));
        }
    }
}
