/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_ec2::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional runtime information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Shows the scheduled events for the Amazon Elastic Compute Cloud (Amazon EC2) instances in the Region.
async fn show_events(reg: &'static str) {    
    let region_provider = RegionProviderChain::default_provider().or_else(reg);
    let config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&config);

    let resp = client.describe_instance_status().send().await;

    println!("Instances in region {}:", reg);
    println!();

    for status in resp.unwrap().instance_statuses.unwrap_or_default() {
        println!(
            "  Events scheduled for instance ID: {}",
            status.instance_id.as_deref().unwrap_or_default()
        );
        for event in status.events.unwrap_or_default() {
            println!("    Event ID:     {}", event.instance_event_id.unwrap());
            println!("    Description:  {}", event.description.unwrap());
            /* Event codes:
               InstanceReboot
               InstanceRetirement
               InstanceStop
               SystemMaintenance
               SystemReboot
               Unknown
            */
            println!("    Event code:   {}", event.code.unwrap().as_ref());
            println!();
        }
    }
}

/// Lists the events of your EC2 instances in all available regions.
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
        println!("EC2 client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    // Get list of available regions.
    let resp = client.describe_regions().send().await;

    // Show the events for that EC2 instances in that Region.
    for region in resp.unwrap().regions.unwrap_or_default() {
        let reg: &'static str = Box::leak(region.region_name.unwrap().into_boxed_str());
        show_events(reg).await;
    }

    Ok(())
}
