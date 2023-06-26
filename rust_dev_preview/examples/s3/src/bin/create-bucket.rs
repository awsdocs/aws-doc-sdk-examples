/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::error::SdkError;
use aws_sdk_s3::operation::create_bucket::{CreateBucketError, CreateBucketOutput};
use aws_sdk_s3::types::{BucketLocationConstraint, CreateBucketConfiguration};
use aws_sdk_s3::{config::Region, meta::PKG_VERSION, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the bucket.
    #[structopt(short, long)]
    bucket: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Creates a bucket.
// snippet-start:[s3.rust.create-bucket]
async fn make_bucket(
    client: &Client,
    bucket: &str,
    region: &str,
) -> Result<CreateBucketOutput, SdkError<CreateBucketError>> {
    let constraint = BucketLocationConstraint::from(region);
    let cfg = CreateBucketConfiguration::builder()
        .location_constraint(constraint)
        .build();

    client
        .create_bucket()
        .create_bucket_configuration(cfg)
        .bucket(bucket)
        .send()
        .await
}
// snippet-end:[s3.rust.create-bucket]

#[cfg(test)]
mod test_make_bucket {
    use sdk_examples_test_utils::single_shot_client;

    use crate::make_bucket;

    #[tokio::test]
    async fn test_make_bucket() {
        let client = single_shot_client! {
            sdk: aws_sdk_s3,
            status: 200,
            headers: vec![("Location", "/test_bucket")],
            response: r#""#
        };

        let res = make_bucket(&client, "bucket", "region").await;

        assert!(res.is_ok(), "{res:?}");

        assert_eq!(res.unwrap().location(), Some("/test_bucket"))
    }
}

/// Creates an Amazon S3 bucket in the Region.
/// # Arguments
///
/// * `-b BUCKET` - The name of the bucket.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        bucket,
        verbose,
    } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    let r_rgr = region_provider.region().await.unwrap();
    let r_str = r_rgr.as_ref();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!(
            "Region:            {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Bucket:            {}", &bucket);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    make_bucket(&client, &bucket, r_str).await?;
    println!("Created bucket.");

    Ok(())
}
