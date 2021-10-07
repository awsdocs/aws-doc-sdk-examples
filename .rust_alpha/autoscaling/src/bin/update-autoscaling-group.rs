/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_autoscaling::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The name of the Amazon EC2 Auto Scaling group.
    #[structopt(short, long)]
    autoscaling_name: String,

    /// The new maximum size of the Auto Scaling group.
    #[structopt(short, long)]
    max_size: i32,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Updates the size of a group.
// snippet-start:[autoscaling.rust.update-autoscaling-group]
async fn update_group(client: &Client, name: &str, size: i32) -> Result<(), Error> {
    client
        .update_auto_scaling_group()
        .auto_scaling_group_name(name)
        .max_size(size)
        .send()
        .await?;

    println!("Updated AutoScaling group");

    Ok(())
}
// snippet-end:[autoscaling.rust.update-autoscaling-group]

/// Updates an Auto Scaling group in the Region to a new maximum size.
/// # Arguments
///
/// * `-a AUTOSCALING-NAME` - The name of the Auto Scaling group.
/// * `-m MAXIMUM-SiZE` - The mazimum size of the Auto Scaling group.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        autoscaling_name,
        max_size,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("Auto Scaling client version: {}", PKG_VERSION);
        println!(
            "Region:                      {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("AutoScaling group name:      {}", &autoscaling_name);
        println!("Max size:                    {}", &max_size);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    update_group(&client, &autoscaling_name, max_size).await
}
