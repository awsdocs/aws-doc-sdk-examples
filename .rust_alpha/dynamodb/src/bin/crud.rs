/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_http::AwsErrorRetryPolicy;
use aws_hyper::{SdkError, SdkSuccess};
use aws_sdk_dynamodb::error::DescribeTableError;
use aws_sdk_dynamodb::input::DescribeTableInput;
use aws_sdk_dynamodb::model::{
    AttributeDefinition, AttributeValue, KeySchemaElement, KeyType, ProvisionedThroughput,
    ScalarAttributeType, Select, TableStatus,
};
use aws_sdk_dynamodb::operation::DescribeTable;
use aws_sdk_dynamodb::output::DescribeTableOutput;
use aws_sdk_dynamodb::{Client, Config, Error, Region, PKG_VERSION};
use aws_types::region;
use aws_types::region::ProvideRegion;
use rand::distributions::Alphanumeric;
use rand::{thread_rng, Rng};
use smithy_http::operation::Operation;
use smithy_http::retry::ClassifyResponse;
use smithy_types::retry::RetryKind;
use std::io::{stdin, Read};
use std::time::Duration;
use std::{iter, process};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
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

/// Create a random, n-length string
fn random_string(n: usize) -> String {
    let mut rng = thread_rng();
    iter::repeat(())
        .map(|()| rng.sample(Alphanumeric))
        .map(char::from)
        .take(n)
        .collect()
}

/// Create a new table.
async fn make_table(
    client: &Client,
    table: &str,
    key: &str,
) -> Result<(), aws_hyper::SdkError<aws_sdk_dynamodb::error::CreateTableError>> {
    let ad = AttributeDefinition::builder()
        .attribute_name(key)
        .attribute_type(ScalarAttributeType::S)
        .build();

    let ks = KeySchemaElement::builder()
        .attribute_name(key)
        .key_type(KeyType::Hash)
        .build();

    let pt = ProvisionedThroughput::builder()
        .read_capacity_units(10)
        .write_capacity_units(5)
        .build();

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

/// For add_item and query_item
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
async fn add_item(
    client: &Client,
    item: Item,
) -> Result<(), SdkError<aws_sdk_dynamodb::error::PutItemError>> {
    let user_av = AttributeValue::S(item.value);
    let type_av = AttributeValue::S(item.utype);
    let age_av = AttributeValue::S(item.age);
    let first_av = AttributeValue::S(item.first_name);
    let last_av = AttributeValue::S(item.last_name);

    match client
        .put_item()
        .table_name(item.table)
        .item(item.key, user_av)
        .item("account_type", type_av)
        .item("age", age_av)
        .item("first_name", first_av)
        .item("last_name", last_av)
        .send()
        .await
    {
        Ok(_) => Ok(()),
        Err(e) => Err(e),
    }
}

/// Query the table for an item matching the input values.
/// Returns true if the item is found; otherwise false.
async fn query_item(client: &Client, item: Item) -> bool {
    let value = &item.value;
    let key = &item.key;
    let user_av = AttributeValue::S(value.to_string());

    match client
        .query()
        .table_name(item.table)
        .key_condition_expression("#key = :value".to_string())
        .expression_attribute_names("#key".to_string(), key.to_string())
        .expression_attribute_values(":value".to_string(), user_av)
        .select(Select::AllAttributes)
        .send()
        .await
    {
        Ok(resp) => {
            if resp.count > 0 {
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

/// Hand-written waiter to retry every second until the table is out of `Creating` state
#[derive(Clone)]
struct WaitForReadyTable<R> {
    inner: R,
}

impl<R> ClassifyResponse<SdkSuccess<DescribeTableOutput>, SdkError<DescribeTableError>>
    for WaitForReadyTable<R>
where
    R: ClassifyResponse<SdkSuccess<DescribeTableOutput>, SdkError<DescribeTableError>>,
{
    fn classify(
        &self,
        response: Result<&SdkSuccess<DescribeTableOutput>, &SdkError<DescribeTableError>>,
    ) -> RetryKind {
        match self.inner.classify(response) {
            RetryKind::NotRetryable => (),
            other => return other,
        };
        match response {
            Ok(SdkSuccess { parsed, .. }) => {
                if parsed
                    .table
                    .as_ref()
                    .unwrap()
                    .table_status
                    .as_ref()
                    .unwrap()
                    == &TableStatus::Creating
                {
                    RetryKind::Explicit(Duration::from_secs(1))
                } else {
                    RetryKind::NotRetryable
                }
            }
            _ => RetryKind::NotRetryable,
        }
    }
}

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
    } = Opt::from_args();

    let region = region::ChainProvider::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

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
            region.region().unwrap().as_ref()
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

    let conf = Config::builder().region(region).build();
    let client = Client::from_conf(conf);

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

    let raw_client = aws_hyper::Client::https();

    raw_client
        .call(wait_for_ready_table(&table, client.conf()))
        .await
        .expect("table should become ready.");

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

    /* Update the item */
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

    /* Delete item */
    println!("Deleting item.");
    let user_av = AttributeValue::S(value);
    client
        .delete_item()
        .table_name(&table)
        .key(key, user_av)
        .send()
        .await?;

    println!("Deleted item.");

    if interactive {
        pause();
    }

    /* Delete table */
    println!("Deleting table.");
    client.delete_table().table_name(&table).send().await?;
    println!("Deleted table.");
    println!();

    Ok(())
}

/// Construct a `DescribeTable` request with a policy to retry every second until the table
/// is ready
fn wait_for_ready_table(
    table_name: &str,
    conf: &Config,
) -> Operation<DescribeTable, WaitForReadyTable<AwsErrorRetryPolicy>> {
    let operation = DescribeTableInput::builder()
        .table_name(table_name)
        .build()
        .expect("valid input")
        .make_operation(conf)
        .expect("valid operation");
    let waiting_policy = WaitForReadyTable {
        inner: operation.retry_policy().clone(),
    };
    operation.with_retry_policy(waiting_policy)
}
