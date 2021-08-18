/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the table.
    #[structopt(short, long)]
    table: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

/// Deletes a DynamoDB table.
/// # Arguments
///
/// * `-t TABLE` - The name of the table.
/// * `[-r REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let Opt {
        table,
        region,
        verbose,
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("DynamoDB client version: {}", PKG_VERSION);
        println!(
            "Region:                  {}",
            region.region().unwrap().as_ref()
        );
        println!("Table:                   {}", &table);
        println!();
    }

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

    client.delete_table().table_name(table).send().await?;

    println!("Deleted table");

    Ok(())
}
