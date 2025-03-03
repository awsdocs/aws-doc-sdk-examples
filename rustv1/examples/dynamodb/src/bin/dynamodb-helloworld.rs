// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_dynamodb::types::{
    AttributeDefinition, BillingMode, KeySchemaElement, KeyType, ScalarAttributeType,
};
use aws_sdk_dynamodb::{config::Region, meta::PKG_VERSION, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Lists your tables.
// snippet-start:[dynamodb.rust.dynamodb-helloworld-list_tables]
async fn list_tables(client: &Client) -> Result<(), Error> {
    let tables = client.list_tables().send().await?;

    println!("Current DynamoDB tables: {:?}", tables);

    Ok(())
}
// snippet-end:[dynamodb.rust.dynamodb-helloworld-list_tables]

// Creates the test-table table.
// snippet-start:[dynamodb.rust.dynamodb-helloworld-create_table]
async fn create_table(client: &Client) -> Result<(), Error> {
    let ks = KeySchemaElement::builder()
        .attribute_name("k")
        .key_type(KeyType::Hash)
        .build()
        .expect("creating KeySchemaElement");

    let ad = AttributeDefinition::builder()
        .attribute_name("k")
        .attribute_type(ScalarAttributeType::S)
        .build()
        .expect("creating AttributeDefinition");

    let new_table = client
        .create_table()
        .table_name("test-table")
        .key_schema(ks)
        .attribute_definitions(ad)
        .billing_mode(BillingMode::PayPerRequest)
        .send()
        .await?;
    println!(
        "new table: {:#?}",
        &new_table.table_description().unwrap().table_arn().unwrap()
    );

    Ok(())
}
// snippet-end:[dynamodb.rust.dynamodb-helloworld-create_table]

/// Lists your DynamoDB tables and creates the table **test_table**.
/// # Arguments
///
/// * `[-r REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt { region, verbose } = Opt::parse();

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

    list_tables(&client).await?;

    create_table(&client).await
}
