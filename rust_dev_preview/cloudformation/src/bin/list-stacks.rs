/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_cloudformation::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

// Lists your stacks.
// snippet-start:[cloudformation.rust.list-stacks]
async fn list_stacks(client: &Client) -> Result<(), Error> {
    let stacks = client.list_stacks().send().await?;

    for stack in stacks.stack_summaries().unwrap_or_default() {
        println!("{}", stack.stack_name().unwrap_or_default());
        println!("  Status: {:?}", stack.stack_status().unwrap());
        println!();
    }

    Ok(())
}
// snippet-end:[cloudformation.rust.list-stacks]

/// Lists the name and status of your AWS CloudFormation stacks in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    println!();

    if verbose {
        println!("CloudFormation client version: {}", PKG_VERSION);
        println!(
            "Region:                        {}",
            region_provider.region().await.unwrap().as_ref()
        );

        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    list_stacks(&client).await
}
