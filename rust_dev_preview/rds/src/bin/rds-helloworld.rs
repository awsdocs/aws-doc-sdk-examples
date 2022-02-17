/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_rds::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Lists your instances.
// snippet-start:[rds.rust.rds-helloworld]
async fn show_instances(client: &Client) -> Result<(), Error> {
    let result = client.describe_db_instances().send().await?;

    for db_instance in result.db_instances().unwrap_or_default() {
        println!(
            "DB instance identifier: {:?}",
            db_instance
                .db_instance_identifier()
                .expect("instance should have identifiers")
        );
        println!(
            "DB instance class:      {:?}",
            db_instance
                .db_instance_class()
                .expect("instance should have class")
        );
        println!(
            "DB instance engine:     {:?}",
            db_instance.engine().expect("instance should have engine")
        );
        println!(
            "DB instance status:     {:?}",
            db_instance
                .db_instance_status()
                .expect("instance should have status")
        );
        println!(
            "DB instance endpoint:   {:?}",
            db_instance
                .endpoint()
                .expect("instance should have endpoint")
        );
        println!();
    }

    Ok(())
}
// snippet-end:[rds.rust.rds-helloworld]

/// Displays information about your Amazon Relational Database Service (Amazon RDS) instances in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The region in which the client is created.
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
        println!("RDS client version: {}", PKG_VERSION);
        println!(
            "Region:             {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_instances(&client).await
}
