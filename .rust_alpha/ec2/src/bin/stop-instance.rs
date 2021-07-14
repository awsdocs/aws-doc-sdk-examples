/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use ec2::{Client, Config, Error, Region};

use aws_types::region::ProvideRegion;

use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The default region
    #[structopt(short, long)]
    default_region: Option<String>,

    /// The ID of the instance to stop
    #[structopt(short, long)]
    instance_id: String,

    /// Whether to display additional information
    #[structopt(short, long)]
    verbose: bool,
}

/// Stops an Amazon EC2 instance.
/// # Arguments
///
/// * `-i INSTANCE-ID` - The ID of the instances to stop.
/// * `[-d DEFAULT-REGION]` - The AWS Region in which the client is created.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        default_region,
        instance_id,
        verbose,
    } = Opt::from_args();

    let region = default_region
        .as_ref()
        .map(|region| Region::new(region.clone()))
        .or_else(|| aws_types::region::default_provider().region())
        .unwrap_or_else(|| Region::new("us-west-2"));

    if verbose {
        println!("EC2 client version: {}", ec2::PKG_VERSION);
        println!("Region:             {:?}", &region);
        println!("Instance ID:        {:?}", &instance_id);
    }

    let config = Config::builder().region(&region).build();

    let client = Client::from_conf(config);
    client
        .stop_instances()
        .instance_ids(instance_id)
        .send()
        .await?;
    println!("Stopped instance");
    println!();

    Ok(())
}
