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

    /// Whether to display additional runtime information
    #[structopt(short, long)]
    verbose: bool,
}

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

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("CloudFormation client version: {}", PKG_VERSION);
        println!(
            "Region:                        {}",
            region.region().unwrap().as_ref()
        );
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    let stacks = client.list_stacks().send().await?;

    for s in stacks.stack_summaries.unwrap_or_default() {
        println!("{}", s.stack_name.as_deref().unwrap_or_default());
        println!("  Status: {:?}", s.stack_status.unwrap());
        println!();
    }

    Ok(())
}
