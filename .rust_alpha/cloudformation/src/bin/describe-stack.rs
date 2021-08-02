/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_cloudformation::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
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

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("CloudFormation version: {}", PKG_VERSION);
        println!(
            "Region:                 {}",
            region.region().unwrap().as_ref()
        );
        println!("Stack:                  {}", &stack_name);
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    // Return an error if stack_name does not exist
    let resp = client
        .describe_stacks()
        .stack_name(stack_name)
        .send()
        .await?;

    // Otherwise we get a list of stacks that match the stack_name.
    // The list should only have one item, so just access is via pop().
    let status = resp.stacks.unwrap_or_default().pop().unwrap().stack_status;

    println!("Stack status: {:?}", status);

    println!();

    Ok(())
}
