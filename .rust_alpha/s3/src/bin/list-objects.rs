/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use s3::{Client, Config, Region};

use aws_types::region::ProvideRegion;

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The bucket containing objects to list.
    #[structopt(short, long)]
    bucket: String,

    /// The AWS Region.
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the objects in an Amazon S3 bucket.
/// # Arguments
///
/// * `[-b BUCKET]` - The bucket containing the objects to list.
/// * `[-d DEFAULT-REGION]` - The region containing the buckets.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-g]` - Whether to display buckets in all regions.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        bucket,
        default_region,
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

    let mut num_objects: i32 = 0;

    match client.list_objects().bucket(&bucket).send().await {
        Ok(resp) => {
            println!("\nObjects in {}:\n", &bucket);

            let objects = resp.contents.unwrap_or_default();

            for object in &objects {
                match &object.key {
                    None => {}
                    Some(k) => {
                        println!("Name:          {}", k);
                        num_objects += 1;
                    }
                }

                println!("Size:          {} bytes", &object.size);

                match &object.storage_class {
                    None => {}
                    Some(sc) => {
                        println!("Storage class: {}\n", sc.as_str());
                    }
                }
            }

            println!("\nFound {} object(s) in {}", num_objects, bucket);
        }
        Err(e) => {
            println!("Got an error listing objects:");
            println!("{}", e);
            process::exit(1);
        }
    };
}
