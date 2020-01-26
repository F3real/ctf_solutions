#![feature(maybe_uninit_ref)]
extern crate winapi;

use std::mem::{size_of, MaybeUninit};

use winapi::shared::minwindef::{DWORD, FALSE, FILETIME, HMODULE, TRUE};
use winapi::um::handleapi::CloseHandle;
use winapi::um::processthreadsapi::{GetProcessTimes, GetSystemTimes, OpenProcess};
use winapi::um::psapi::{EnumProcessModules, EnumProcesses, GetModuleBaseNameA};
use winapi::um::winnt::{HANDLE, PROCESS_QUERY_INFORMATION, PROCESS_VM_READ};

const MAX_FILENAME: usize = 260;

macro_rules! filetime_to_u64 {
    ($x:expr) => {
        $x.get_ref().dwLowDateTime as u64 + (($x.get_ref().dwHighDateTime as u64) << 32);
    };
}

fn get_process_ids(capacity: usize) -> Result<Vec<DWORD>, u32> {
    let mut v = Vec::<DWORD>::with_capacity(capacity);
    let mut lpcb_needed: DWORD = 0;

    unsafe {
        // EnumProcesses should never write more then given capacity
        if EnumProcesses(
            v.as_mut_ptr(),
            (size_of::<DWORD>() * capacity) as DWORD,
            &mut lpcb_needed,
        ) == TRUE
        {
            let new_size = lpcb_needed as usize / size_of::<DWORD>();
            v.set_len(new_size);
            v.resize_with(new_size, Default::default);
            Ok(v)
        } else {
            Err(1)
        }
    }
}

fn get_process_handle(pid: DWORD) -> Option<HANDLE> {
    let p_handle = unsafe { OpenProcess(PROCESS_QUERY_INFORMATION | PROCESS_VM_READ, FALSE, pid) };
    if p_handle.is_null() {
        None
    } else {
        Some(p_handle)
    }
}

fn get_process_name(p_handle: HANDLE) -> Result<String, u32> {
    let mut lph_module = MaybeUninit::<HMODULE>::uninit();
    let mut cb_needed: DWORD = 0;
    unsafe {
        // Should return base module
        if EnumProcessModules(
            p_handle,
            lph_module.as_mut_ptr(),
            size_of::<HMODULE>() as u32,
            &mut cb_needed,
        ) != 0
        {
            let mut v: Vec<u8> = Vec::with_capacity(MAX_FILENAME);
            let name_len = GetModuleBaseNameA(
                p_handle,
                lph_module.assume_init(),
                v.as_mut_ptr() as *mut i8,
                MAX_FILENAME as u32,
            ) as usize;
            if name_len > 0 {
                if name_len < MAX_FILENAME {
                    v.set_len(name_len);
                    v.resize_with(name_len, Default::default);
                }
                return Ok(String::from_utf8(v).unwrap());
            }
        }
        Err(1)
    }
}

struct Process {
    name: String,
    pid: DWORD,
    handle: HANDLE,
}

struct CPUMonitor {
    prev_kernel_time: u64,
    prev_user_time: u64,
    processes: Vec<Process>,
    process_times: std::collections::HashMap<DWORD, (u64, u64)>,
}

fn build_process(pid: DWORD) -> Result<Process, u32> {
    if let Some(p_handle) = get_process_handle(pid) {
        let p_name = match get_process_name(p_handle) {
            Ok(name) => name,
            Err(_) => String::new(),
        };
        Ok(Process {
            name: p_name,
            pid,
            handle: p_handle,
        })
    } else {
        Err(1)
    }
}

fn build_cpu_monitor(process_pids: &[DWORD]) -> CPUMonitor {
    let mut processes = Vec::<Process>::with_capacity(process_pids.len());
    for pid in process_pids {
        if let Ok(process) = build_process(*pid) {
            processes.push(process);
        }
    }
    CPUMonitor {
        prev_kernel_time: 0,
        prev_user_time: 0,
        processes,
        process_times: std::collections::HashMap::new(),
    }
}

impl CPUMonitor {
    fn update(&mut self) -> bool {
        let time_diff;

        unsafe {
            let mut kernel_time = MaybeUninit::<FILETIME>::uninit();
            let mut user_time = MaybeUninit::<FILETIME>::uninit();
            if GetSystemTimes(
                MaybeUninit::<FILETIME>::uninit().as_mut_ptr(),
                kernel_time.as_mut_ptr(),
                user_time.as_mut_ptr(),
            ) == 0
            {
                return false;
            }
            let new_kernel_time = filetime_to_u64!(kernel_time);
            let new_user_time = filetime_to_u64!(user_time);
            time_diff =
                new_kernel_time - self.prev_kernel_time + new_user_time - self.prev_user_time;
            self.prev_kernel_time = new_kernel_time;
            self.prev_user_time = new_user_time;
        }

        let mut p_kernel_time = MaybeUninit::<FILETIME>::uninit();
        let mut p_user_time = MaybeUninit::<FILETIME>::uninit();

        for process in &self.processes {
            unsafe {
                if GetProcessTimes(
                    process.handle,
                    MaybeUninit::<FILETIME>::uninit().as_mut_ptr(),
                    MaybeUninit::<FILETIME>::uninit().as_mut_ptr(),
                    p_kernel_time.as_mut_ptr(),
                    p_user_time.as_mut_ptr(),
                ) == 0
                {
                    return false;
                }
                let proc_new_kernel = filetime_to_u64!(p_kernel_time);
                let proc_new_user = filetime_to_u64!(p_user_time);

                if self.process_times.contains_key(&process.pid) {
                    let (proc_prev_kernel, proc_prev_user) = self.process_times[&process.pid];
                    let process_time_dif =
                        proc_new_kernel - proc_prev_kernel + proc_new_user - proc_prev_user;
                    println!(
                        "ID: {:>5}\t CPU: {:>.02}%\t Name: {}",
                        process.pid,
                        (100_f64 * process_time_dif as f64 / time_diff as f64),
                        process.name
                    );
                }
                self.process_times
                    .insert(process.pid, (proc_new_kernel, proc_new_user));
            }
        }
        true
    }
}

impl Drop for CPUMonitor {
    fn drop(&mut self) {
        for process in &self.processes {
            unsafe {
                CloseHandle(process.handle);
            }
        }
    }
}

fn main() {
    if let Ok(process_pids) = get_process_ids(1024) {
        let mut cpu_monitor = build_cpu_monitor(&process_pids);
        cpu_monitor.update();
        std::thread::sleep(std::time::Duration::from_millis(500));
        cpu_monitor.update();
        println! {"Total number of processes: {}", process_pids.len()}
    }
}
