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

    /// Whether to force the deletion.
    #[structopt(short, long)]
    force: bool,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Updates an Auto Scaling group in the Region to the specified maximum size.
/// # Arguments
///
/// * `-a AUTOSCALING-NAME` - The name of the Auto Scaling group.
/// * - [-f] - Whether to force the deletion.
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        autoscaling_name,
        force,
        region,
        verbose,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    println!();

    if verbose {
        println!("Auto Scaling client version: {}", PKG_VERSION);
        println!(
            "Region:                      {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Auto Scaling group name:     {}", &autoscaling_name);
        println!("Force deletion?:             {}", &force);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    client
        .delete_auto_scaling_group()
        .auto_scaling_group_name(autoscaling_name)
        .set_force_delete(force.then(|| true))
        .send()
        .await?;

    println!("Deleted Auto Scaling group");
    Ok(())
}
