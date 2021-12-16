/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_s3::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
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

// Copy an object from one bucket to another.
// snippet-start:[bin.rust.copy-object]
async fn cp_object(
    client: &Client,
    source: &str,
    destination: &str,
    key: &str,
    new_name: &str,
) -> Result<(), Error> {
    let mut src: String = "".to_owned();
    src.push_str(source);
    src.push('/');
    src.push_str(key);

    client
        .copy_object()
        .bucket(destination)
        .copy_source(src)
        .key(new_name)
        .send()
        .await?;

    println!("Object copied.");

    Ok(())
}
// snippet-end:[bin.rust.copy-object]

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
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        source,
        destination,
        key,
        name,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    let new_name: String;

    if name == None {
        new_name = key.clone();
    } else {
        new_name = name.unwrap().clone();
    }

    if verbose {
        println!("S3 client version:  {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Source bucket:      {}", &source);
        println!("Destination bucket: {}", &destination);
        println!("Source key:         {}", &key);
        println!("Destination key:    {}", &new_name);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    cp_object(&client, &source, &destination, &key, &new_name).await
}
