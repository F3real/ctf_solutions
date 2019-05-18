#[macro_use]
extern crate criterion;
extern crate rand;
extern crate rand_xorshift;
extern crate timsort;


use criterion::black_box;
use criterion::Criterion;
use rand::{RngCore, SeedableRng};
use rand_xorshift::XorShiftRng;
use timsort::*;

fn criterion_benchmark(c: &mut Criterion) {
    c.bench_function("insertion sort", |b| {
        /* setup for sample */
        let seed = <XorShiftRng as SeedableRng>::Seed::default();
        let mut rng: XorShiftRng  = XorShiftRng::from_seed(seed);
        let mut arr = Vec::<u64>::with_capacity(1000);
        for _ in 0..1000 {
            arr.push(rng.next_u64());
        }

        /* measured code */
        b.iter(|| insertion_sort(black_box(&mut arr)));
    });
}

criterion_group!(benches, criterion_benchmark);
criterion_main!(benches);