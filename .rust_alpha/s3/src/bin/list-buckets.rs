/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use s3::{Client, Config, Region};

use aws_types::region::ProvideRegion;

use structopt::StructOpt;
// use tracing_subscriber::fmt::format::FmtSpan;
// use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The default region
    #[structopt(short, long)]
    default_region: Option<String>,

    /// Whether to show all buckets
    #[structopt(short, long)]
    global: bool,

    /// Whether to display additional information
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists your Amazon S3 buckets
/// # Arguments
///
/// * `[-d DEFAULT-REGION]` - The region containing the buckets.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-g]` - Whether to display buckets in all regions.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        default_region,
        global,
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

        // SubscriberBuilder::default()
        //    .with_env_filter("info")
        //    .with_span_events(FmtSpan::CLOSE)
        //    .init();
    }

    let config = Config::builder().region(&region).build();

    let client = Client::from_conf(config);

    let mut num_buckets = 0;

    match client.list_buckets().send().await {
        Ok(resp) => {
            println!("\nBuckets:\n");

            let buckets = resp.buckets.unwrap_or_default();

            for bucket in &buckets {
                match &bucket.name {
                    None => {}
                    Some(b) => {
                        if global {
                            println!("{}", b);
                            num_buckets += 1;
                        } else {
                            match client.get_bucket_location().bucket(b).send().await {
                                Ok(resp) => match resp.location_constraint {
                                    None => {
                                        if verbose {
                                            println!("Could not get a location for {}", b);
                                        }
                                    }
                                    Some(c) => {
                                        if verbose {
                                            println!("Location for {}: {}", b, c.as_str());
                                        }
                                        if c.as_str() == r {
                                            println!("{}", b);
                                            num_buckets += 1;
                                        }
                                    }
                                },
                                Err(e) => {
                                    println!("Got an error listing buckets:");
                                    println!("{}", e);
                                    process::exit(1);
                                }
                            };
                        }
                    }
                }
            }

            if global {
                println!("\nFound {} buckets globally", num_buckets);
            } else {
                println!("\nFound {} buckets in {}", num_buckets, r);
            }
        }
        Err(e) => {
            println!("Got an error listing buckets:");
            println!("{}", e);
            process::exit(1);
        }
    };
}
