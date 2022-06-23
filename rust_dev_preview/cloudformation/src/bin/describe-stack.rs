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

    /// The name of the AWS CloudFormation stack.
    #[structopt(short, long)]
    stack_name: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Lists the status of a stack.
// snippet-start:[cloudformation.rust.describe-stack]
async fn describe_stack(client: &Client, name: &str) -> Result<(), Error> {
    // Return an error if stack_name does not exist
    let resp = client.describe_stacks().stack_name(name).send().await?;

    // Otherwise we get an array of stacks that match the stack_name.
    // The array should only have one item, so just access it via first().
    let status = resp
        .stacks()
        .unwrap_or_default()
        .first()
        .unwrap()
        .stack_status();

    println!("Stack status: {}", status.unwrap().as_ref());

    println!();

    Ok(())
}
// snippet-end:[cloudformation.rust.describe-stack]

/// Retrieves the status of a CloudFormation stack in the Region.
/// # Arguments
///
/// * `-s STACK-NAME` - The name of the stack.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        region,
        stack_name,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("CloudFormation version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Stack:                  {}", &stack_name);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    describe_stack(&client, &stack_name).await
}
