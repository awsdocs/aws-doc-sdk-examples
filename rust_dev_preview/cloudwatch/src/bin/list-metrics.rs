/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_cloudwatch::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// List metrics.
// snippet-start:[cloudwatch.rust.list-metrics]
async fn show_metrics(
    client: &aws_sdk_cloudwatch::Client,
) -> Result<(), aws_sdk_cloudwatch::Error> {
    let rsp = client.list_metrics().send().await?;
    let metrics = rsp.metrics().unwrap_or_default();

    let num_metrics = metrics.len();

    for metric in metrics {
        println!("Namespace: {}", metric.namespace().unwrap_or_default());
        println!("Name:      {}", metric.metric_name().unwrap_or_default());
        println!("Dimensions:");

        if let Some(dimension) = metric.dimensions.as_ref() {
            for d in dimension {
                println!("  Name:  {}", d.name().unwrap_or_default());
                println!("  Value: {}", d.value().unwrap_or_default());
                println!();
            }
        }

        println!();
    }

    println!("Found {} metrics.", num_metrics);

    Ok(())
}
// snippet-end:[cloudwatch.rust.list-metrics]

/// Lists your Amazon CloudWatch metrics in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt { region, verbose } = Opt::from_args();

    if verbose {
        tracing_subscriber::fmt::init();
    }

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    if verbose {
        println!();
        println!("CloudWatch client version: {}", PKG_VERSION);
        println!(
            "Region:                    {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_metrics(&client).await
}
