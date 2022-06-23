/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_dynamodb::{Client, Error, Region, PKG_VERSION};
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

// List your tables 10 at a time.
// snippet-start:[dynamodb.rust.list-more-tables]
async fn list_tables(client: &Client) -> Result<(), Error> {
    // snippet-start:[dynamodb.rust.list-more-tables-list]
    let mut resp = client.list_tables().limit(10).send().await?;
    let names = resp.table_names.unwrap_or_default();
    let len = names.len();

    let mut num_tables = len;

    println!("Tables:");

    for name in names {
        println!("  {}", name);
    }

    while resp.last_evaluated_table_name != None {
        println!("-- more --");
        resp = client
            .list_tables()
            .limit(10)
            .exclusive_start_table_name(
                resp.last_evaluated_table_name
                    .as_deref()
                    .unwrap_or_default(),
            )
            .send()
            .await?;

        let names = resp.table_names.unwrap_or_default();
        num_tables += names.len();

        for name in names {
            println!("  {}", name);
        }
    }

    println!();
    println!("Found {} tables", num_tables);

    Ok(())
    // snippet-end:[dynamodb.rust.list-more-tables-list]
}
// snippet-end:[dynamodb.rust.list-more-tables]

/// Lists your DynamoDB tables.
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
        println!("DynamoDB client version: {}", PKG_VERSION);
        println!(
            "Region:                  {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    list_tables(&client).await
}
