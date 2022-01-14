/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_autoscalingplans::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information
    #[structopt(short, long)]
    verbose: bool,
}

// Lists your plans.
// snippet-start:[autoscalingplans.rust.describe-scaling-plans]
async fn list_plans(client: &Client) -> Result<(), Error> {
    let response = client.describe_scaling_plans().send().await?;

    if let Some(plans) = response.scaling_plans() {
        println!("Auto Scaling Plans:");
        for plan in plans {
            println!("{:?}\n", plan);
        }
    }

    Ok(())
}
// snippet-end:[autoscalingplans.rust.describe-scaling-plans]

/// Lists your Amazon Autoscaling plans.
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

    if verbose {
        println!("Auto Scaling Plans client version: {}", PKG_VERSION);
        println!(
            "Region:                            {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    list_plans(&client).await
}
