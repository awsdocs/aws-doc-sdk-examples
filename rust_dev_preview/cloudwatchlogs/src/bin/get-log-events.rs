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

    /// The log group name.
    #[structopt(short, long)]
    group: String,

    /// The log stream name.
    #[structopt(short, long)]
    stream: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Lists your log events.
// snippet-start:[cloudwatchlogs.rust.get-log-events]
async fn show_log_events(
    client: &aws_sdk_cloudwatchlogs::Client,
    group: &str,
    stream: &str,
) -> Result<(), aws_sdk_cloudwatchlogs::Error> {
    let log_events = client
        .get_log_events()
        .log_group_name(group)
        .log_stream_name(stream)
        .send()
        .await?;
    let events = log_events.events().unwrap_or_default();
    println!("Found {} events:", events.len());
    for event in events {
        println!("message: {}", event.message().unwrap_or_default());
    }

    Ok(())
}
// snippet-end:[cloudwatchlogs.rust.get-log-events]

/// Lists the events for a log stream in the Region.
/// # Arguments
///
/// * `-g LOG-GROUP` - The name of the log group.
/// * `-s LOG-STREAM` - The name of the log stream.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt {
        region,
        group,
        stream,
        verbose,
    } = Opt::from_args();

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
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Log group name:         {}", &group);
        println!("Log stream name:         {}", &stream);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_log_events(&client, &group, &stream).await
}
