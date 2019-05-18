extern crate timsort;

use timsort::*;

pub fn main() {
    let mut arr = [1, 0, 2, 4, 100, 3, 101, 9, 102, 3];

    //insertion_sort(&mut arr);
    binary_insertion_sort(&mut arr);
    if is_sorted(&arr) {
        println!("Sorted!");
    }
}

