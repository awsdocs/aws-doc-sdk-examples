/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_kinesis::types::Blob;
use aws_sdk_kinesis::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The data to add to the stream.
    #[structopt(short, long)]
    data: String,

    /// The name of the partition key.
    #[structopt(short, long)]
    key: String,

    /// The name of the stream.
    #[structopt(short, long)]
    stream_name: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Adds a record to a stream.
// snippet-start:[kinesis.rust.put-record]
async fn add_record(client: &Client, stream: &str, key: &str, data: &str) -> Result<(), Error> {
    let blob = Blob::new(data);

    client
        .put_record()
        .data(blob)
        .partition_key(key)
        .stream_name(stream)
        .send()
        .await?;

    println!("Put data into stream.");

    Ok(())
}
// snippet-end:[kinesis.rust.put-record]

/// Adds a record to an Amazon Kinesis data stream.
/// # Arguments
///
/// * `-s STREAM-NAME` - The name of the stream.
/// * `-k KEY-NAME` - The name of the partition key.
/// * `-d DATA` - The data to add.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        data,
        key,
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
        println!("Data:");
        println!();
        println!("{}", &data);
        println!();
        println!("Partition key:          {}", &key);
        println!("Stream name:            {}", &stream_name);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    add_record(&client, &stream_name, &key, &data).await
}
