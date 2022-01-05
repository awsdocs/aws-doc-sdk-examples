/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_kinesis::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the stream.
    #[structopt(short, long)]
    stream_name: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Display stream information.
// snippet-start:[kinesis.rust.describe-stream]
async fn show_stream(client: &Client, stream: &str) -> Result<(), Error> {
    let resp = client.describe_stream().stream_name(stream).send().await?;

    let desc = resp.stream_description.unwrap();

    println!("Stream description:");
    println!("  Name:              {}:", desc.stream_name.unwrap());
    println!("  Status:            {:?}", desc.stream_status.unwrap());
    println!("  Open shards:       {:?}", desc.shards.unwrap().len());
    println!(
        "  Retention (hours): {}",
        desc.retention_period_hours.unwrap()
    );
    println!("  Encryption:        {:?}", desc.encryption_type.unwrap());

    Ok(())
}
// snippet-end:[kinesis.rust.describe-stream]

/// Displays some information about an Amazon Kinesis data stream in the Region.
/// # Arguments
///
/// * `-s STREAM-NAME` - The name of the stream to describe.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        stream_name,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("Kinesis client version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Stream name:            {}", &stream_name);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_stream(&client, &stream_name).await
}
