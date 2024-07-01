// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use std::time::Duration;

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_ec2::{client::Waiters, config::Region, meta::PKG_VERSION, Client, Error};
use aws_smithy_runtime_api::client::waiters::error::WaiterError;
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The ID of the instance to start.
    #[structopt(short, long)]
    instance_id: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Starts an instance.
// snippet-start:[ec2.rust.start-instance]
async fn start_instance(client: &Client, id: &str) -> Result<(), Error> {
    // start_instance has no unique errors to handle.
    client.start_instances().instance_ids(id).send().await?;

    println!("Waiting for instance to be running");

    let wait_for_running = client
        .wait_until_instance_running()
        .instance_ids(id)
        .wait(Duration::from_secs(60))
        .await;

    match wait_for_running {
        Ok(_) => println!("Instance is running"),
        Err(err) => match err {
            WaiterError::ExceededMaxWait(exceeded) => {
                println!(
                    "Exceeded max time waiting for instance to start. Exceeded {}s by {}s.",
                    exceeded.max_wait().as_secs(),
                    (exceeded.elapsed() - exceeded.max_wait()).as_secs()
                );
                return Ok(());
            }
            _ => return Err(err.into()),
        },
    }

    println!("Started instance.");

    Ok(())
}
// snippet-end:[ec2.rust.start-instance]

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
    } = Opt::parse();

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
        println!("Instance ID:        {}", instance_id);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    start_instance(&client, &instance_id).await
}
