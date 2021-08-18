/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_kinesis::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// THe name of the stream.
    #[structopt(short, long)]
    stream_name: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Creates an Amazon Kinesis data stream.
/// # Arguments
///
/// * `-s STREAM-NAME` - The name of the stream.
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

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Kinesis client version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region.region().unwrap().as_ref()
        );
        println!("Stream name:            {}", &stream_name);
        println!();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    client
        .create_stream()
        .stream_name(stream_name)
        .shard_count(4)
        .send()
        .await?;

    println!("Created stream");

    Ok(())
}
