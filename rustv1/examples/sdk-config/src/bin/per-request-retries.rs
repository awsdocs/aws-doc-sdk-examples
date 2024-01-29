// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::BehaviorVersion;
use aws_sdk_s3::config::retry::RetryConfig;
use aws_sdk_s3::{meta::PKG_VERSION, Client, Config, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The number of (re)tries.
    #[structopt(short, long, default_value = "2")]
    tries: u32,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Displays how many Amazon S3 buckets you have.
/// # Arguments
///
/// * `[-t TRIES]` - The number of times to (re)try the request.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { tries, verbose } = Opt::parse();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!("Max retries:       {}", &tries);
        println!();
    }

    assert_ne!(tries, 0, "You cannot set zero retries.");

    let shared_config = aws_config::defaults(BehaviorVersion::latest()).load().await;

    // Construct an S3 client with customized retry configuration.
    let client = Client::new(&shared_config);

    // snippet-start:[rust.per_operation_retry]
    let resp = client
        .list_buckets()
        .customize()
        .config_override(
            Config::builder().retry_config(RetryConfig::standard().with_max_attempts(tries)),
        )
        .send()
        .await?;
    // snippet-end:[rust.per_operation_retry]
    let buckets = resp.buckets();

    println!("Found {} buckets in all regions.", buckets.len());

    Ok(())
}
