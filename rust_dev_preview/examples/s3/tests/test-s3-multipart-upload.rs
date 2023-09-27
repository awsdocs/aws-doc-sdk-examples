/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */
use assert_cmd::prelude::*;
use std::process::Command;

#[ignore]
#[tokio::test]
async fn test_it_runs() {
    let mut cmd = Command::cargo_bin("s3-multipart-upload").unwrap();
    let output = cmd.unwrap();
    println!("{}", std::str::from_utf8(&output.stdout).unwrap());
}
