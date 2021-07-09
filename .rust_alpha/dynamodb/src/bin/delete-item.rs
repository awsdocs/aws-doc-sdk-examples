/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::process;

use dynamodb::model::AttributeValue;
use dynamodb::{Client, Config, Region};

use aws_types::region::{EnvironmentProvider, ProvideRegion};

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The region
    #[structopt(short, long)]
    region: Option<String>,

    /// The table name
    #[structopt(short, long)]
    table: String,

    /// The key for the item in the table
    #[structopt(short, long)]
    key: String,

    /// The value of the item to delete from the table
    #[structopt(short, long)]
    value: String,

    /// Whether to display additional information
    #[structopt(short, long)]
    info: bool,
}

/// Deletes an item from an Amazon DynamoDB table.
/// The table schema must use the key as the primary key.
/// # Arguments
///
/// * `-t TABLE` - The name of the table.
/// * `-k KEY` - The table's primary key.
/// * `-v VALUE` - The value of the item's primary key.
/// * `[-r REGION]` - The region in which the table is created.
///   If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-i]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    let Opt {
        info,
        key,
        region,
        table,
        value,
    } = Opt::from_args();

    let region = EnvironmentProvider::new()
        .region()
        .or_else(|| region.as_ref().map(|region| Region::new(region.clone())))
        .unwrap_or_else(|| Region::new("us-west-2"));

    if info {
        println!("DynamoDB client version: {}", dynamodb::PKG_VERSION);
        println!("Region: {:?}", &region);
        println!("Table:  {}", table);
        println!("Key:    {}", key);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let config = Config::builder().region(region).build();

    let client = Client::from_conf(config);

    match client
        .delete_item()
        .table_name(table)
        .key(key, AttributeValue::S(value))
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
}
