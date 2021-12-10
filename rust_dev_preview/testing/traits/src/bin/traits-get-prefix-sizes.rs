/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
// So we can refer to the S3 SDK as s3 for the rest of this example.
use aws_sdk_s3 as s3;
use aws_sdk_s3::{Client, Region, PKG_VERSION};
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

    /// The prefix to the bucket name.
    #[structopt(short, long)]
    prefix: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

pub struct ListObjectsResult {
    pub objects: Vec<s3::model::Object>,
    pub continuation_token: Option<String>,
    pub has_more: bool,
}

#[async_trait::async_trait]
pub trait ListObjects {
    async fn list_objects(
        &self,
        bucket: &str,
        prefix: &str,
        continuation_token: Option<String>,
    ) -> ListObjectsResult;
}

// Get the size of the files.
async fn determine_prefix_file_size(
    // Take a reference to our trait object instead of the S3 client.
    list_objects_impl: &dyn ListObjects,
    bucket: &str,
    prefix: &str,
) -> Result<usize, Box<dyn Error + Send + Sync + 'static>> {
    let mut next_token: Option<String> = None;
    let mut total_size_bytes = 0;
    loop {
        let result = list_objects_impl
            .list_objects(bucket, prefix, next_token)
            .await;

        // Keep track of the size of the files we got back.
        for object in result.objects {
            total_size_bytes += object.size() as usize;
        }

        // Handle pagination, and break out of the loop if there are no more pages.
        next_token = result.continuation_token;
        if !result.has_more {
            break;
        }
    }

    Ok(total_size_bytes)
}

#[derive(Clone, Debug)]
pub struct S3ListObjects {
    s3: s3::Client,
}

impl S3ListObjects {
    pub fn new(s3: s3::Client) -> Self {
        Self { s3 }
    }
}

#[async_trait::async_trait]
impl ListObjects for S3ListObjects {
    async fn list_objects(
        &self,
        bucket: &str,
        prefix: &str,
        continuation_token: Option<String>,
    ) -> Result<ListObjectsResult, Box<dyn Error + Send + Sync + 'static>> {
        let response = self
            .s3
            .list_objects_v2()
            .bucket(bucket)
            .prefix(prefix)
            .set_continuation_token(continuation_token)
            .send()
            .await?;
        Ok(ListObjectsResult {
            objects: response
                .contents()
                .unwrap_or_default()
                .iter()
                .cloned()
                .collect(),
            continuation_token: response.continuation_token().map(|t| t.to_string()),
            has_more: response.is_truncated,
        })
    }
}

/// Get the size of the files with a name starting with a prefix.
/// # Arguments
///
/// * `-b BUCKET` - The name of the bucket containing the files.
/// * `-p PREFIX` - The prefix.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Box<dyn Error + Send + Sync + 'static>> {
    tracing_subscriber::fmt::init();

    let Opt {
        bucket,
        prefix,
        region,
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
        println!("Bucket:            {}", &bucket);
        println!("Prefix:            {}", &prefix);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    let size = determine_prefix_file_size(, &bucket, &prefix).await?;

    println!("The # of bytes: {}", size);

    Ok(())
}
