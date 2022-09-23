/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::{config, Client, Error, Region, RetryConfig, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The number of (re)tries.
    #[structopt(short, long, default_value = "2")]
    tries: u32,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Shows your buckets.
async fn show_num_buckets(client: &Client) -> Result<(), Error> {
    let resp = client.list_buckets().send().await?;
    let buckets = resp.buckets().unwrap_or_default();

    println!("Found {} buckets in all regions.", buckets.len());

    Ok(())
}

/// Displays how many Amazon S3 buckets you have.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-t TRIES]` - The number of times to (re)try the request.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        tries,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!(
            "Region:            {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Retries:           {}", &tries);
        println!();
    }

    assert_ne!(tries, 0, "You cannot set zero retries.");

    // snippet-start:[custom_retries.rust.set_retries]
    let shared_config = aws_config::from_env().region(region_provider).load().await;

    // Construct an S3 client with customized retry configuration.
    let client = Client::from_conf(
        // Start with the shared environment configuration.
        config::Builder::from(&shared_config)
            // Set max attempts.
            // If tries is 1, there are no retries.
            .retry_config(RetryConfig::default().with_max_attempts(tries))
            .build(),
    );
    // snippet-end:[custom_retries.rust.set_retries]

    show_num_buckets(&client).await
}
