/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_s3::{ByteStream, Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use std::path::Path;
use std::process;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the bucket.
    #[structopt(short, long)]
    bucket: String,

    /// The name of the object in the bucket.
    #[structopt(short, long)]
    key: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists your buckets and uploads a file to a bucket.
/// # Arguments
///
/// * `-b BUCKET` - The bucket to which the file is uploaded.
/// * `-k KEY` - The name of the file to upload to the bucket.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        bucket,
        region,
        key,
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
        println!("Key:               {}", &key);
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let resp = client.list_buckets().send().await?;

    for bucket in resp.buckets.unwrap_or_default() {
        println!("bucket: {:?}", bucket.name.as_deref().unwrap_or_default())
    }

    println!();

    let body = ByteStream::from_path(Path::new("Cargo.toml")).await;

    match body {
        Ok(b) => {
            let resp = client
                .put_object()
                .bucket(&bucket)
                .key(&key)
                .body(b)
                .send()
                .await?;

            println!("Upload success. Version: {:?}", resp.version_id);

            let resp = client.get_object().bucket(bucket).key(key).send().await?;
            let data = resp.body.collect().await;
            println!("data: {:?}", data.unwrap().into_bytes());
        }
        Err(e) => {
            println!("Got an error DOING SOMETHING:");
            println!("{}", e);
            process::exit(1);
        }
    }

    Ok(())
}
