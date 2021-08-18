/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_ec2::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The ID of the instance to stop.
    #[structopt(short, long)]
    instance_id: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Starts an Amazon EC2 instance.
/// # Arguments
///
/// * `-i INSTANCE-ID` - The ID of the instances to start.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        instance_id,
        verbose,
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    if verbose {
        println!("EC2 client version: {}", PKG_VERSION);
        println!("Region:             {}", region.region().unwrap().as_ref());
        println!("Instance ID:        {}", instance_id);
        println!();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    client
        .start_instances()
        .instance_ids(instance_id)
        .send()
        .await?;

    println!("Started instance.");

    Ok(())
}
