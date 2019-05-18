
pub fn insertion_sort<T>(values: &mut [T])
where
    T: Ord,
{
    for i in 1..values.len() {
        let mut j = i;
        while j > 0 && values[j] < values[j - 1] {
            values.swap(j, j - 1);
            j = j - 1;
        }
    }
}

pub fn binary_insertion_sort<T>(values: &mut [T])
where
    T: Ord,
{
    for i in 1..values.len() {
        let mut j = i;
        let pos = values[..i]
            .binary_search(&values[i])
            .unwrap_or_else(|pos| pos);
        while j > pos {
            values.swap(j - 1, j);
            j = j - 1;
        }
    }
}

const MIN_RUN: usize = 64;

pub fn get_minrun(len: usize) -> usize {
    let mut r = 0;
    let mut new_len = len;
    while new_len >= MIN_RUN {
        r |= new_len & 1;
        new_len >>= 1;
    }
    new_len + r
}

pub fn is_sorted<T>(values: &[T]) -> bool
where
    T: Ord,
{
    values.windows(2).all(|w| w[0] <= w[1])
}

/*                        Tests                                */
#[cfg(test)]
mod tests {
    extern crate rand;

    use super::*;
    use tests::rand::{distributions::Uniform, Rng};

    #[test]
    pub fn test_binary_insertion_sort() {
        let mut rng = rand::thread_rng();
        let range = Uniform::new(0, 100000);
        let mut arr = Vec::<i64>::with_capacity(1000);
        for _ in 0..1000 {
            arr.push(rng.sample(&range));
        }

        binary_insertion_sort(&mut arr);
        assert!(is_sorted(&arr));
    }

    #[test]
    pub fn test_insertion_sort() {
        let mut rng = rand::thread_rng();
        let range = Uniform::new(0, 100000);
        let mut arr = Vec::<i64>::with_capacity(1000);
        for _ in 0..1000 {
            arr.push(rng.sample(&range));
        }

        insertion_sort(&mut arr);
        assert!(is_sorted(&arr));
    }
}