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

    /// To get info about one instance.
    #[structopt(short, long)]
    instance_id: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the state of one or all of your Amazon EC2 instances.
/// # Arguments
///
/// * `[-i INSTANCE-ID]` - The ID of an instance.
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

    println!();

    if verbose {
        println!("EC2 client version: {}", PKG_VERSION);
        println!("Region:             {}", region.region().unwrap().as_ref());

        if instance_id.is_some() {
            println!("Instance ID:        {:?}", instance_id);
        }

        println!();
    }

    let config = Config::builder().region(region).build();
    let client = Client::from_conf(config);

    let mut ids: Vec<String> = Vec::new();
    let id_opt: std::option::Option<std::vec::Vec<std::string::String>>;

    match instance_id {
        None => id_opt = None,
        Some(i) => {
            ids.push(i);
            id_opt = Some(ids);
        }
    }

    let resp = client
        .describe_instances()
        .set_instance_ids(id_opt)
        .send()
        .await?;

    for reservation in resp.reservations.unwrap_or_default() {
        for instance in reservation.instances.unwrap_or_default() {
            println!("Instance ID: {}", instance.instance_id.unwrap());
            println!("State:       {:?}", instance.state.unwrap().name.unwrap());
            println!();
        }
    }

    Ok(())
}
