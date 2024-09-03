// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::{config::Region, meta::PKG_VERSION, Client};
use clap::Parser;
use s3_code_examples::{copy_object, error::S3ExampleError};

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the source bucket.
    #[structopt(short, long)]
    source: String,

    /// The name of the destination bucket.
    #[structopt(short, long)]
    destination: String,

    /// The object to delete.
    #[structopt(short, long)]
    key: String,

    /// The new name of the object in the destination bucket.
    #[structopt(short, long)]
    name: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Copies an object from one Amazon S3 bucket to another.
/// # Arguments
///
/// * `-s SOURCE` - The name of the source bucket.
/// * `-d DESTINATION` - The name of the destination bucket.
/// * `-k KEY` - The name of the object to copy.
/// * `-n NAME` - The new name of the object in the destination bucket.
///   If not supplied, the name remains the same.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), S3ExampleError> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        source,
        destination,
        key,
        name,
        verbose,
    } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    let new_name: String = name.unwrap_or_else(|| key.clone());

    if verbose {
        println!("S3 client version:  {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Source bucket:      {}", &source);
        println!("Source key:         {}", &key);
        println!("Destination bucket: {}", &destination);
        println!("Destination key:    {}", &new_name);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    copy_object(&client, &source, &destination, &key, &new_name).await?;

    println!("Object copied.");

    Ok(())
}

#[cfg(test)]
mod test_cp_object {
    use sdk_examples_test_utils::single_shot_client;

    use crate::copy_object;

    #[tokio::test]
    async fn test_cp_object() {
        let client = single_shot_client! {
            sdk: aws_sdk_s3,
            status: 200,
            response: r#""#
        };

        let response = copy_object(
            &client,
            "source_bucket",
            "destination_bucket",
            "source_object",
            "destination_object",
        )
        .await;

        assert!(response.is_ok(), "{response:?}");
    }
}
