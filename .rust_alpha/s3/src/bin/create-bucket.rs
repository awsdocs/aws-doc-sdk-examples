/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use s3::{Client, Config, Region};

use s3::model::{BucketLocationConstraint, CreateBucketConfiguration};

use aws_types::region::ProvideRegion;

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The default region
    #[structopt(short, long)]
    default_region: Option<String>,

    /// The name of the bucket
    #[structopt(short, long)]
    name: String,

    /// Whether to display additional information
    #[structopt(short, long)]
    verbose: bool,
}

/// Creates an Amazon S3 bucket
/// # Arguments
///
/// * `-n NAME` - The name of the bucket.
/// * `[-d DEFAULT-REGION]` - The region containing the bucket.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        default_region,
        name,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    let r: &str = &region.as_ref();

    if verbose {
        println!("S3 client version: {}", s3::PKG_VERSION);
        println!("Region:            {:?}", &region);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(&region).build();

    let client = Client::from_conf(config);

    let constraint = BucketLocationConstraint::from(r);
    let cfg = CreateBucketConfiguration::builder()
        .location_constraint(constraint)
        .build();

    match client
        .create_bucket()
        .create_bucket_configuration(cfg)
        .bucket(&name)
        .send()
        .await
    {
        Ok(_) => {
            println!("Created bucket {}", name);
        }

        Err(e) => {
            println!("Got an error creating bucket:");
            println!("{}", e);
            process::exit(1);
        }
    };
}
