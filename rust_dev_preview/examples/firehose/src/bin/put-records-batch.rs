/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_firehose::error::SdkError;
use aws_sdk_firehose::operation::put_record_batch::{PutRecordBatchError, PutRecordBatchOutput};
use aws_sdk_firehose::primitives::Blob;
use aws_sdk_firehose::types::Record;
use aws_sdk_firehose::{config::Region, meta::PKG_VERSION, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,

    /// Whether to display additional information.
    #[structopt(long)]
    firehose_stream: String,
}

// Displays the STS AssumeRole Arn.
// snippet-start:[firehose.rust.put_record_batch]
async fn put_record_batch(
    client: &Client,
    stream: &str,
    data: Vec<Record>,
) -> Result<PutRecordBatchOutput, SdkError<PutRecordBatchError>> {
    client
        .put_record_batch()
        .delivery_stream_name(stream)
        .set_records(Some(data))
        .send()
        .await
}
// snippet-end:[firehose.rust.put_record_batch]

/// Assumes another role and display some information about the role assumed
///
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[--firehose-stream STREAM_NAME]` - The ARN of the IAM role to assume.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        verbose,
        firehose_stream,
    } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("Firehose client version: {}", PKG_VERSION);
        println!(
            "Region:                  {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    // initialize a list of 500 records and add a single record to it
    let mut data = Vec::with_capacity(500);
    let payload = String::from("Some random payload");
    let tmp = Some(Blob::new(payload.to_string()));
    data.push(
        Record::builder()
            .set_data(tmp)
            .build()
            .expect("Failed to create a new record"),
    );

    let resp = put_record_batch(&client, &firehose_stream, data).await;
    match resp {
        Ok(_) => Ok(()),
        Err(e) => Err(Error::from(e)),
    }
}
