// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

// snippet-start:[localstack.rust.use-localstack]
use aws_config::BehaviorVersion;
use std::error::Error;

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    tracing_subscriber::fmt::init();

    // snippet-start:[localstack.rust.use-localstack.config]
    // set the environment variable `AWS_PROFILE=localstack` when running
    // the application to source `endpoint_url` and point the SDK at the
    // localstack instance
    let config = aws_config::defaults(BehaviorVersion::latest()).load().await;

    let s3_config = aws_sdk_s3::config::Builder::from(&config)
        .force_path_style(true)
        .build();

    let s3 = aws_sdk_s3::Client::from_conf(s3_config);
    // snippet-end:[localstack.rust.use-localstack.config]

    let resp = s3.list_buckets().send().await?;
    let buckets = resp.buckets();

    println!("Found {} buckets:", buckets.len());
    for bucket in buckets {
        println!("\t{:?}", bucket.name());
    }

    Ok(())
}
// snippet-end:[localstack.rust.use-localstack]
