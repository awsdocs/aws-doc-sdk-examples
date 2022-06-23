/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
// snippet-start:[testing.rust.intro-import]
// So we can refer to the S3 package as s3 for the rest of the example.
use aws_sdk_s3 as s3;
// snippet-end:[testing.rust.intro-import]
use std::error::Error;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the bucket.
    #[structopt(short, long)]
    bucket: String,

    /// The bucket prefix.
    #[structopt(short, long)]
    prefix: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// The two testing approaches imported as modules below
mod enums;
mod traits;

// snippet-start:[testing.rust.intro-function]
// Lists all objects in an S3 bucket with the given prefix, and adds up their size.
async fn determine_prefix_file_size(
    s3: s3::Client,
    bucket: &str,
    prefix: &str,
) -> Result<usize, Box<dyn Error + Send + Sync + 'static>> {
    let mut next_token: Option<String> = None;
    let mut total_size_bytes = 0;
    loop {
        let response = s3
            .list_objects_v2()
            .bucket(bucket)
            .prefix(prefix)
            .set_continuation_token(next_token.take())
            .send()
            .await?;

        // Add up the file sizes we got back
        if let Some(contents) = response.contents() {
            for object in contents {
                total_size_bytes += object.size() as usize;
            }
        }

        // Handle pagination, and break the loop if there are no more pages
        next_token = response.continuation_token().map(|t| t.to_string());
        if !response.is_truncated() {
            break;
        }
    }
    Ok(total_size_bytes)
}
// snippet-end:[testing.rust.intro-function]

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error + Send + Sync + 'static>> {
    tracing_subscriber::fmt::init();

    let Opt {
        bucket,
        prefix,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(s3::Region::new))
        .or_default_provider()
        .or_else(s3::Region::new("us-west-2"));

    println!();

    if verbose {
        println!("S3 client version: {}", s3::PKG_VERSION);
        println!(
            "Region:            {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Bucket:            {}", &bucket);
        println!("Prefix:            {}", &prefix);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = s3::Client::new(&shared_config);

    let total_size = determine_prefix_file_size(client, &bucket, &prefix).await?;
    println!("Total size: {}", total_size);

    Ok(())
}
