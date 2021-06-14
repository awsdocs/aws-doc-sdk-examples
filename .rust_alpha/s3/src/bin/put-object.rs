/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use s3::{Client, Config, Region};

use aws_types::region::ProvideRegion;

use smithy_http::byte_stream::ByteStream;
use std::path::Path;

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The bucket to which the object is added.
    #[structopt(short, long)]
    bucket: String,

    /// The AWS Region.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// The name of the object.
    #[structopt(short, long)]
    key: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Adds an object (file) to an Amazon S3 bucket.
/// # Arguments
///
/// * `-b BUCKET` - The name of the bucket.
/// * `-k KEY` - The name of the object (file).
/// * `[-d DEFAULT-REGION]` - The region containing the bucket.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        bucket,
        default_region,
        key,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("S3 client version: {}", s3::PKG_VERSION);
        println!("AWS Region:        {:?}", &region);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(&region).build();

    let client = Client::from_conf(config);

    // Snarf body of file into bytes
    let f = Path::new(&key);
    let bs = ByteStream::from_path(&f).await.expect(&key);

    match client
        .put_object()
        .bucket(&bucket)
        .key(&key)
        .body(bs)
        .send()
        .await
    {
        Ok(_) => {
            println!("Added file {} to bucket {}", key, bucket);
        }

        Err(e) => {
            println!("Got an error adding object to bucket:");
            println!("{}", e);
            process::exit(1);
        }
    };
}
