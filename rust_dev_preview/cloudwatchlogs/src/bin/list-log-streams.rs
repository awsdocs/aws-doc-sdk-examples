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

    /// The log group prefix.
    #[structopt(short, long)]
    prefix: String,

    /// The log group name.
    #[structopt(short, long)]
    group: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Lists the streams for a log group.
// snippet-start:[cloudwatchlogs.rust.list-log-streams]
async fn show_log_streams(
    client: &aws_sdk_cloudwatchlogs::Client,
    name: &str,
) -> Result<(), aws_sdk_cloudwatchlogs::Error> {
    let resp = client
        .describe_log_streams()
        .log_group_name(name)
        .send()
        .await?;
    let streams = resp.log_streams().unwrap_or_default();
    println!("Found {} streams:", streams.len());
    for stream in streams {
        println!("  {}", stream.log_stream_name().unwrap_or_default());
    }

    Ok(())
}
// snippet-end:[cloudwatchlogs.rust.list-log-streams]

/// Lists the log streams for a log group in the Region.
/// # Arguments
///
/// * `-g LOG-GROUP` - The name of the log group.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt {
        region,
        group,
        prefix,
        verbose,
    } = Opt::from_args();

    if verbose {
        tracing_subscriber::fmt::init();
    }

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    // Construct log group name
    let mut name: String = "/aws/".to_owned();
    name.push_str(&prefix);
    name.push('/');
    name.push_str(&group);

    if verbose {
        println!();
        println!("CloudWatchLogs client version: {}", PKG_VERSION);
        println!(
            "Region:                        {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Log group name:                {}", &name);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_log_streams(&client, &name).await
}
