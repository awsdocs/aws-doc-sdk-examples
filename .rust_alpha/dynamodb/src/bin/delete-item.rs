/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_dynamodb::model::AttributeValue;
use aws_sdk_dynamodb::{Client, Error, Region, PKG_VERSION};
use std::process;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// The name of the table.
    #[structopt(short, long)]
    table: String,

    /// The key for the item in the table.
    #[structopt(short, long)]
    key: String,

    /// The value of the item to delete from the table.
    #[structopt(short, long)]
    value: String,

    /// Whether to display additional information.
    #[structopt(short, long)]
    info: bool,
}

// Deletes an item from the table.
// snippet-start:[dynamodb.rust.delete-item]
async fn delete_item(client: &Client, table: &str, key: &str, value: &str) -> Result<(), Error> {
    match client
        .delete_item()
        .table_name(table)
        .key(key, AttributeValue::S(value.into()))
        .send()
        .await
    {
        Ok(_) => println!("Deleted item from table"),
        Err(e) => {
            println!("Got an error deleting item from table:");
            println!("{}", e);
            process::exit(1);
        }
    };

    Ok(())
}
// snippet-end:[dynamodb.rust.delete-item]

/// Deletes an item from an Amazon DynamoDB table.
/// The table schema must use the key as the primary key.
/// # Arguments
///
/// * `-t TABLE` - The name of the table.
/// * `-k KEY` - The table's primary key.
/// * `-v VALUE` - The value of the item's primary key.
/// * `[-r REGION]` - The region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-i]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        info,
        key,
        region,
        table,
        value,
    } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if info {
        println!("DynamoDB client version: {}", PKG_VERSION);
        println!(
            "Region:                  {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Table:                   {}", &table);
        println!("Key:                     {}", &key);
        println!("Value:                   {}", &value);
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    delete_item(&client, &table, &key, &value).await
}
