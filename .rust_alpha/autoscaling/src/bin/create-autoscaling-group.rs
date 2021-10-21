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

    /// The ID of the EC2 instance to add to the Auto Scaling group.
    #[structopt(short, long)]
    instance_id: String,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Creates a group.
// snippet-start:[autoscaling.rust.create-autoscaling-group]
async fn create_group(client: &Client, name: &str, id: &str) -> Result<(), Error> {
    client
        .create_auto_scaling_group()
        .auto_scaling_group_name(name)
        .instance_id(id)
        .min_size(1)
        .max_size(5)
        .send()
        .await?;

    println!("Created AutoScaling group");

    Ok(())
}
// snippet-end:[autoscaling.rust.create-autoscaling-group]

/// Creates an Auto Scaling group in the Region.
/// # Arguments
///
/// * `-a AUTOSCALING-NAME` - The name of the Auto Scaling group.
/// * `-i INSTANCE-ID` - The ID of the EC2 instance to add to the Auto Scaling group.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        autoscaling_name,
        instance_id,
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
        println!("Auto Scaling group name:     {}", &autoscaling_name);
        println!("Instance ID:                 {}", &instance_id);

        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    create_group(&client, &autoscaling_name, &instance_id).await
}
