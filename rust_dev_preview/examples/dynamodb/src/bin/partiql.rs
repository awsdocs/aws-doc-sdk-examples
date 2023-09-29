/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_dynamodb::error::SdkError;
use aws_sdk_dynamodb::operation::create_table::CreateTableError;
use aws_sdk_dynamodb::operation::execute_statement::ExecuteStatementError;
use aws_sdk_dynamodb::types::{
    AttributeDefinition, AttributeValue, KeySchemaElement, KeyType, ProvisionedThroughput,
    ScalarAttributeType, TableStatus,
};
use aws_sdk_dynamodb::{config::Region, meta::PKG_VERSION, Client, Error};
use clap::Parser;
use rand::distributions::Alphanumeric;
use rand::{thread_rng, Rng};
use std::io::{stdin, Read};
use std::time::Duration;
use std::{iter, process};

#[derive(Debug, Parser)]
struct Opt {
    /// Whether to run in interactive mode (you have to press return between operations)
    #[structopt(short, long)]
    interactive: bool,

    /// The AWS Region
    #[structopt(short, long)]
    region: Option<String>,

    /// Activate verbose mode
    #[structopt(short, long)]
    verbose: bool,
}

/// Create a random, n-length string.
fn random_string(n: usize) -> String {
    let mut rng = thread_rng();
    iter::repeat(())
        .map(|()| rng.sample(Alphanumeric))
        .map(char::from)
        .take(n)
        .collect()
}

/// Create a new table.
// snippet-start:[dynamodb.rust.partiql-make_table]
async fn make_table(
    client: &Client,
    table: &str,
    key: &str,
) -> Result<(), SdkError<CreateTableError>> {
    let ad = AttributeDefinition::builder()
        .attribute_name(key)
        .attribute_type(ScalarAttributeType::S)
        .build()
        .expect("creating AttributeDefinition");

    let ks = KeySchemaElement::builder()
        .attribute_name(key)
        .key_type(KeyType::Hash)
        .build()
        .expect("creating KeySchemaElement");

    let pt = ProvisionedThroughput::builder()
        .read_capacity_units(10)
        .write_capacity_units(5)
        .build()
        .expect("creating ProvisionedThroughput");

    match client
        .create_table()
        .table_name(table)
        .key_schema(ks)
        .attribute_definitions(ad)
        .provisioned_throughput(pt)
        .send()
        .await
    {
        Ok(_) => Ok(()),
        Err(e) => Err(e),
    }
}
// snippet-end:[dynamodb.rust.partiql-make_table]

/// A struct for the arguments and returns from add_item and query_item.
#[derive(Clone)]
struct Item {
    table: String,
    key: String,
    value: String,
    first_name: String,
    last_name: String,
    age: String,
    utype: String,
}

/// Add an item to the table.
// snippet-start:[dynamodb.rust.partiql-add_item]
async fn add_item(client: &Client, item: Item) -> Result<(), SdkError<ExecuteStatementError>> {
    match client
        .execute_statement()
        .statement(format!(
            r#"INSERT INTO "{}" VALUE {{
                "{}": ?,
                "acount_type": ?,
                "age": ?,
                "first_name": ?,
                "last_name": ?
        }} "#,
            item.table, item.key
        ))
        .set_parameters(Some(vec![
            AttributeValue::S(item.utype),
            AttributeValue::S(item.age),
            AttributeValue::S(item.first_name),
            AttributeValue::S(item.last_name),
        ]))
        .send()
        .await
    {
        Ok(_) => Ok(()),
        Err(e) => Err(e),
    }
}
// snippet-end:[dynamodb.rust.partiql-add_item]

/// Query the table for an item matching the input values.
/// Returns true if the item is found; otherwise false.
// snippet-start:[dynamodb.rust.partiql-query_item]
async fn query_item(client: &Client, item: Item) -> bool {
    match client
        .execute_statement()
        .statement(format!(
            r#"SELECT * FROM "{}" WHERE "{}" = ?"#,
            item.table, item.key
        ))
        .set_parameters(Some(vec![AttributeValue::S(item.value)]))
        .send()
        .await
    {
        Ok(resp) => {
            if !resp.items().is_empty() {
                println!("Found a matching entry in the table:");
                println!("{:?}", resp.items.unwrap_or_default().pop());
                true
            } else {
                println!("Did not find a match.");
                false
            }
        }
        Err(e) => {
            println!("Got an error querying table:");
            println!("{}", e);
            process::exit(1);
        }
    }
}
// snippet-end:[dynamodb.rust.partiql-query_item]

// Deletes an item from a table.
// snippet-start:[dynamodb.rust.partiql-remove_item]
async fn remove_item(client: &Client, table: &str, key: &str, value: String) -> Result<(), Error> {
    client
        .execute_statement()
        .statement(format!(r#"DELETE FROM "{table}" WHERE "{key}" = ?"#))
        .set_parameters(Some(vec![AttributeValue::S(value)]))
        .send()
        .await?;

    println!("Deleted item.");

    Ok(())
}
// snippet-end:[dynamodb.rust.partiql-remove_item]

// Deletes a table.
// snippet-start:[dynamodb.rust.partiql-remove_table]
async fn remove_table(client: &Client, table: &str) -> Result<(), Error> {
    client.delete_table().table_name(table).send().await?;

    Ok(())
}
// snippet-end:[dynamodb.rust.partiql-remove_table]

/// Wait for the user to press Enter.
fn pause() {
    println!("Press Enter to continue.");
    stdin().read_exact(&mut [0]).unwrap();
}

/// Performs CRUD (create, read, update, delete) operations on a DynamoDB table and table item.
/// It creates a table, adds an item to the table, updates the item, deletes the item, and deletes the table.
/// The table name, primary key, and primary key value are all created as random strings.
///
/// # Arguments
///
/// * `[-i]` - Whether to pause between operations.
/// * `[-r REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt {
        interactive,
        region,
        verbose,
    } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    let shared_config = aws_config::from_env().region(region_provider).load().await;

    // Create 10-character random table name
    let table = random_string(10);

    // Create a 6-character random key name
    let key = random_string(6);

    // Create a 12-character random key value
    let value = random_string(12);

    // Specify first name, last name, age, and type
    let first_name = "DummyFirstName";
    let last_name = "DummyLastName";
    let age = "33";
    let utype = "standard_user";

    println!();

    if verbose {
        println!("DynamoDB client version: {}", PKG_VERSION);
        println!(
            "Region:                  {}",
            shared_config.region().unwrap()
        );
        println!("Table:                   {}", table);
        println!("Key:                     {}", key);
        println!("Value:                   {}", value);
        println!("First name:              {}", first_name);
        println!("Last name:               {}", last_name);
        println!("Age:                     {}", age);
        println!("User type:               {}", utype);

        println!();
    }

    let client = Client::new(&shared_config);

    /* Create table */
    println!("Creating the table.");
    match make_table(&client, &table, &key).await {
        Err(e) => {
            println!("Got an error creating the table:");
            println!("{}", e);
            process::exit(1);
        }
        Ok(_) => {
            println!("Created the table.");
        }
    }

    println!("Waiting for table to be ready.");

    wait_for_ready_table(&client, &table).await?;

    println!("Table is now ready to use.");

    if interactive {
        pause();
    }

    println!("Adding item to table.");

    let mut item = Item {
        table: table.clone(),
        key: key.clone(),
        value: value.clone(),
        first_name: first_name.to_string(),
        last_name: last_name.to_string(),
        age: age.to_string(),
        utype: utype.to_string(),
    };

    add_item(&client, item.clone()).await?;
    println!("Added item to table.");

    if interactive {
        pause();
    }

    item.age = "44".to_string();

    /* Update the item. */
    println!("Modifying table item to change age to 44.");

    add_item(&client, item.clone()).await?;

    println!("Modified table item.");

    if interactive {
        pause();
    }

    /* Get item and compare it with the one we added */
    println!("Comparing table item to original value.");

    query_item(&client, item).await;

    if interactive {
        pause();
    }

    /* Delete the item. */
    println!("Deleting item.");
    remove_item(&client, &table, &key, value.clone()).await?;

    if interactive {
        pause();
    }

    /* Delete the table. */
    println!("Deleting table.");
    remove_table(&client, &table).await?;
    println!();

    Ok(())
}

/// Poll the DescribeTable operation once per second until the table exists.
async fn wait_for_ready_table(client: &Client, table_name: &str) -> Result<(), Error> {
    loop {
        if let Some(table) = client
            .describe_table()
            .table_name(table_name)
            .send()
            .await?
            .table()
        {
            if !matches!(table.table_status, Some(TableStatus::Creating)) {
                break;
            }
        }
        tokio::time::sleep(Duration::from_secs(1)).await;
    }
    Ok(())
}
