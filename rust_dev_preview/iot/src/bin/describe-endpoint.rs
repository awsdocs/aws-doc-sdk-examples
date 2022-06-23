/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_iot::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The endpoint type.
    #[structopt(short, long)]
    endpoint_type: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Displays the address of an endpoint.
// snippet-start:[iot.rust.describe-endpoint]
async fn show_address(client: &Client, endpoint_type: &str) -> Result<(), Error> {
    let resp = client
        .describe_endpoint()
        .endpoint_type(endpoint_type)
        .send()
        .await?;

    println!("Endpoint address: {}", resp.endpoint_address.unwrap());

    println!();

    Ok(())
}
// snippet-end:[iot.rust.describe-endpoint]

/// Returns a unique endpoint specific to the AWS account making the call, in the Region.
///
/// # Arguments
///
/// * `-e ENDPOINT-TYPE - The type of endpoint.
///   Must be one of:
///   - iot:Data - Returns a VeriSign signed data endpoint.
///   - iot:Data-ATS - Returns an ATS signed data endpoint.
///   - iot:CredentialProvider - Returns an AWS IoT credentials provider API endpoint.
//    - iot:Jobs - Returns an AWS IoT device management Jobs API endpoint.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        endpoint_type,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("IoT client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Endpoint type:      {}", &endpoint_type);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_address(&client, &endpoint_type).await
}
