// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_config::{BehaviorVersion, Region};
use aws_sdk_s3::{meta::PKG_VERSION, Client, Config, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Displays how many Amazon S3 buckets you have.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::parse();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!(
            "Region:            {}",
            region.as_deref().unwrap_or("not provided"),
        );
        println!();
    }

    let shared_config = aws_config::defaults(BehaviorVersion::latest()).load().await;

    // Construct an S3 client with customized retry configuration.
    let client = Client::new(&shared_config);

    // snippet-start:[rust.per_operation_region]
    let resp = client
        .list_buckets()
        .customize()
        .config_override(
            Config::builder().region(
                RegionProviderChain::first_try(region.map(Region::new))
                    .or_default_provider()
                    .or_else(Region::new("us-west-2"))
                    .region()
                    .await,
            ),
        )
        .send()
        .await?;
    // snippet-end:[rust.per_operation_region]
    let buckets = resp.buckets();

    println!("Found {} buckets in all regions.", buckets.len());

    Ok(())
}
