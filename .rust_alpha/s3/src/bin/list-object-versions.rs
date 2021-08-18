/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_s3::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
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

/// Lists the versions of the objects in an Amazon S3 bucket.
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
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("S3 client version: {}", PKG_VERSION);
        println!("Region:            {}", region.region().unwrap().as_ref());
        println!("Bucket:            {}", &bucket);
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let resp = client.list_object_versions().bucket(&bucket).send().await?;

    for version in resp.versions.unwrap_or_default() {
        println!("{}", version.key.as_deref().unwrap_or_default());
        println!(
            "  version ID: {}",
            version.version_id.as_deref().unwrap_or_default()
        );
        println!();
    }

    Ok(())
}
