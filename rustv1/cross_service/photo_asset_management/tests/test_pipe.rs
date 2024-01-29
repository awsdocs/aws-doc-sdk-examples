// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use chrono::NaiveDateTime;
use std::io::Read;
use streaming_zip::Archive;

const READ_SIZE: usize = 2048;

#[tokio::main]
async fn main() -> Result<(), anyhow::Error> {
    let pipe = pipe::pipe();
    let mut zip_writer = Archive::new(pipe.1);
    let mut pipe = pipe.0;
    let mut body = [0u8; READ_SIZE];
    zip_writer.start_new_file(
        "name".into(),
        NaiveDateTime::from_timestamp_micros(0).unwrap(),
        streaming_zip::CompressionMode::Deflate(8),
        false,
    )?;
    zip_writer.append_data("abc123".as_bytes())?;
    zip_writer.finish_file()?;
    zip_writer.finish()?;
    while pipe.read(&mut body)? > 0 {
        let body = Vec::from(body);
        eprintln!("{body:?}");
    }
    Ok(())
}
