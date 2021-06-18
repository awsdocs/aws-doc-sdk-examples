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

    /// To get info about one instance
    #[structopt(short, long)]
    instance_id: Option<String>,

    /// Whether to display additional information
    #[structopt(short, long)]
    verbose: bool,
}

/// Lists the state of one or all of your Amazon EC2 instances.
/// # Arguments
///
/// * `[-i INSTANCE-ID]` - The ID of an instance.
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

        if instance_id.as_deref().unwrap_or_default() != "" {
            println!("Instance ID:        {:?}", instance_id);
        }
    }

    let config = Config::builder().region(&region).build();

    let client = Client::from_conf(config);

    if instance_id.as_deref().unwrap_or_default() == "" {
        let resp = client.describe_instances().send().await?;
        for reservation in resp.reservations.unwrap_or_default() {
            println!("Instances:");

            for instance in reservation.instances.unwrap_or_default() {
                println!("  {}", instance.instance_id.unwrap());
                println!("  State: {:?}", instance.state.unwrap().name.unwrap());
                println!();
            }
        }
    } else {
        let resp = client
            .describe_instances()
            .instance_ids(instance_id.unwrap())
            .send()
            .await?;
        for reservation in resp.reservations.unwrap_or_default() {
            for instance in reservation.instances.unwrap_or_default() {
                println!("State: {:?}", instance.state.unwrap().name.unwrap());
                println!();
            }
        }
    }

    Ok(())
}
