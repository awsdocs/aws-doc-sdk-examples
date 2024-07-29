// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use std::time::Duration;

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_ec2::{client::Waiters, config::Region, meta::PKG_VERSION, types::Tag, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// An Amazon Machine Image ID (AMI ID) for the instance to be created.
    #[structopt(short, long)]
    ami_id: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Launches an instance. Also referred to as running or creating an instance.
// snippet-start:[ec2.rust.create-instance]
// snippet-start:[ec2.rust.run-instance]
// snippet-start:[ec2.rust.launch-instance]
// snippet-start:[rust.waiters.ec2.create-instance]
async fn create_instance(client: &Client, ami_id: &str) -> Result<(), Error> {
    let run_instances = client
        .run_instances()
        .image_id(ami_id)
        .instance_type(aws_sdk_ec2::types::InstanceType::T1Micro)
        .min_count(1)
        .max_count(1)
        .send()
        .await;

    let run_instances = match run_instances {
        Ok(run_instances) => run_instances,
        Err(err) => return Err(err.into()),
    };

    if run_instances.instances().is_empty() {
        println!("No instances created.");
        return Ok(());
    }

    let instance_id = run_instances.instances()[0].instance_id().unwrap();
    let response = client
        .create_tags()
        .resources(instance_id)
        .tags(
            Tag::builder()
                .key("Name")
                .value("From SDK Examples")
                .build(),
        )
        .send()
        .await;

    match response {
        Ok(_) => println!("Created {instance_id} and applied tags."),
        Err(err) => {
            println!("Error applying tags to {instance_id}: {err:?}");
            return Err(err.into());
        }
    }

    println!("Created instance.");

    let wait_for_created = client
        .wait_until_instance_exists()
        .set_instance_ids(
            run_instances
                .instances()
                .iter()
                .map(|i| i.instance_id.clone())
                .filter(Option::is_some)
                .collect(),
        )
        .wait(Duration::from_secs(6))
        .await;

    match wait_for_created {
        Ok(_) => {
            println!("Instance is created.")
        }
        Err(err) => {
            println!("Timed out waiting for instance to be created.");
            return Err(err.into());
        }
    };

    Ok(())
}
// snippet-end:[rust.waiters.ec2.create-instance]
// snippet-end:[ec2.rust.launch-instance]
// snippet-end:[ec2.rust.run-instance]
// snippet-end:[ec2.rust.create-instance]

/// Creates (launches, runs) an Amazon EC2 instance.
/// # Arguments
/// * `-a AMI_ID` - The Amazon Machine Image ID (AMI ID) for the instance to be created.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        region,
        ami_id,
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
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    create_instance(&client, ami_id.as_str()).await
}
