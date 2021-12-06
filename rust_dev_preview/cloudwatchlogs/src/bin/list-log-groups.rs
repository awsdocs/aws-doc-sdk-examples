/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_cloudwatchlogs::{Client, Error, Region, PKG_VERSION};
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

// Lists your log groups.
// snippet-start:[cloudwatchlogs.rust.list-log-groups]
async fn show_log_groups(
    client: &aws_sdk_cloudwatchlogs::Client,
) -> Result<(), aws_sdk_cloudwatchlogs::Error> {
    let resp = client.describe_log_groups().send().await?;
    let groups = resp.log_groups().unwrap_or_default();
    let num_groups = groups.len();
    for group in groups {
        println!("  {}", group.log_group_name().unwrap_or_default());
    }

    println!();
    println!("Found {} log groups.", num_groups);

    Ok(())
}
// snippet-end:[cloudwatchlogs.rust.list-log-groups]

/// Lists your log groups in the Region.
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
        println!("CloudWatchLogs client version: {}", PKG_VERSION);
        println!(
            "Region:                        {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_log_groups(&client).await
}
