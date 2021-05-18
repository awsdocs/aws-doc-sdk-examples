/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use rand::distributions::Alphanumeric;
use rand::{thread_rng, Rng};
use std::io::{stdin, Read};
use std::time::Duration;
use std::{iter, process};

use aws_http::AwsErrorRetryPolicy;
use aws_hyper::{SdkError, SdkSuccess};

use dynamodb::error::DescribeTableError;

use dynamodb::input::DescribeTableInput;

use dynamodb::model::{
    AttributeDefinition, AttributeValue, KeySchemaElement, KeyType, ProvisionedThroughput,
    ScalarAttributeType, Select, TableStatus,
};

use dynamodb::operation::DescribeTable;
use dynamodb::output::DescribeTableOutput;

use dynamodb::{Client, Config, Region};

use aws_types::region::{EnvironmentProvider, ProvideRegion};

use smithy_http::operation::Operation;
use smithy_http::retry::ClassifyResponse;
use smithy_types::retry::RetryKind;

use structopt::StructOpt;
use tracing_subscriber::fmt::format::FmtSpan;
use tracing_subscriber::fmt::SubscriberBuilder;

#[derive(Debug, StructOpt)]
struct Opt {
    /// Whether to run in interactive mode (you have to press return between operations)
    #[structopt(short, long)]
    interactive: bool,

    /// The region
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

/// Create a new table. It's remotely possible the random table name exists.
async fn create_table(client: &dynamodb::Client, table: &str, key: &str) {
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
        Ok(_) => println!(),
        Err(e) => {
            println!("Got an error creating the table:");
            println!("{}", e);
            process::exit(1);
        }
    }
}

/// For add_item and scan_item
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
async fn add_item(client: &dynamodb::Client, item: Item) {
    let user_av = AttributeValue::S(String::from(item.value));
    let type_av = AttributeValue::S(String::from(item.utype));
    let age_av = AttributeValue::S(String::from(item.age));
    let first_av = AttributeValue::S(String::from(item.first_name));
    let last_av = AttributeValue::S(String::from(item.last_name));

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
        Ok(_) => println!(),
        Err(e) => {
            println!("Got an error adding item to table:");
            println!("{}", e);
            process::exit(1);
        }
    }
}

/// Scan the table for an item matching the input values.
async fn scan(client: &dynamodb::Client, item: Item) {
    let user_av = AttributeValue::S(String::from(item.value));
    let type_av = AttributeValue::S(String::from(item.utype));
    let age_av = AttributeValue::S(String::from(item.age));
    let first_av = AttributeValue::S(String::from(item.first_name));
    let last_av = AttributeValue::S(String::from(item.last_name));

    let mut found_match = true;

    let resp = client
        .scan()
        .table_name(item.table)
        .select(Select::AllAttributes)
        .send()
        .await;

    let key = &item.key;

    match resp {
        Ok(r) => {
            let items = r.items.unwrap_or_default();
            for item in items {
                // Do key values match?
                match item.get(&String::from(key)) {
                    None => found_match = false,
                    Some(v) => {
                        if v != &user_av {
                            found_match = false;
                        }
                    }
                }

                // Do age values match?
                match item.get(&String::from("age")) {
                    None => found_match = false,
                    Some(v) => {
                        if v != &age_av {
                            found_match = false;
                        }
                    }
                }

                // Do first name values match?
                match item.get(&String::from("first_name")) {
                    None => found_match = false,
                    Some(v) => {
                        if v != &first_av {
                            found_match = false;
                        }
                    }
                }

                // Do last name values match?
                match item.get(&String::from("last_name")) {
                    None => found_match = false,
                    Some(v) => {
                        if v != &last_av {
                            found_match = false;
                        }
                    }
                }

                // Do account type values match?
                match item.get(&String::from("account_type")) {
                    None => found_match = false,
                    Some(v) => {
                        if v != &type_av {
                            found_match = false;
                        }
                    }
                }
            }
        }
        Err(e) => {
            println!("Got an error scanning the table:");
            println!("{}", e);
            process::exit(1);
        }
    };

    if !found_match {
        println!("Did not find matching entry in table");
    }
}

/// Delete an item from the table.
async fn delete_item(client: &dynamodb::Client, table: &str, key: &str, value: &str) {
    let user_av = AttributeValue::S(String::from(value));
    match client
        .delete_item()
        .table_name(table)
        .key(key, user_av)
        .send()
        .await
    {
        Ok(_) => println!(),
        Err(e) => {
            println!("Got an error trying to delete item:");
            println!("{}", e);
            process::exit(1);
        }
    }
}

/// Delete the table.
async fn delete_table(client: &dynamodb::Client, table: &str) {
    match client.delete_table().table_name(table).send().await {
        Ok(_) => println!(),
        Err(e) => {
            println!("Got an error deleting table:");
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
    println!();
    println!("Press Enter to continue");
    println!();
    stdin().read_exact(&mut [0]).unwrap();
}

/// Construct a `DescribeTable` request with a policy to retry every second until the table
/// is ready
fn wait_for_ready_table(
    table_name: &str,
    conf: &Config,
) -> Operation<DescribeTable, WaitForReadyTable<AwsErrorRetryPolicy>> {
    let operation = DescribeTableInput::builder()
        .table_name(table_name)
        .build(&conf)
        //.expect("valid input")
        //.make_operation(&conf)
        .expect("valid operation");
    let waiting_policy = WaitForReadyTable {
        inner: operation.retry_policy().clone(),
    };
    operation.with_retry_policy(waiting_policy)
}

#[tokio::main]
async fn main() {
    let Opt {
        interactive,
        region,
        verbose,
    } = Opt::from_args();

    let region = EnvironmentProvider::new()
        .region()
        .or_else(|| region.as_ref().map(|region| Region::new(region.clone())))
        .unwrap_or_else(|| Region::new("us-west-2"));

    // Create 10-charater random table name
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

    if verbose {
        println!("DynamoDB client version: {}\n", dynamodb::PKG_VERSION);
        println!("Table:  {}", table);
        println!("Key:    {}\n", key);
        println!("Value:  {}", value);

        SubscriberBuilder::default()
            .with_env_filter("info")
            .with_span_events(FmtSpan::CLOSE)
            .init();
    }

    let r = region.clone();

    let client = Client::from_env();

    /* Create table */
    println!();
    println!("Creating table {} in {:?}", table, r);
    create_table(&client, &table, &key).await;

    println!("Waiting for table to be ready");

    let raw_client = aws_hyper::Client::https();

    raw_client
        .call(wait_for_ready_table(&table, client.conf()))
        .await
        .expect("table should become ready");

    println!("Table is now ready to use");

    if interactive {
        pause();
    }

    println!();
    println!("Adding item to table");

    let mut item = Item {
        table: table.clone(),
        key: key.clone(),
        value: value.clone(),
        first_name: first_name.to_string(),
        last_name: last_name.to_string(),
        age: age.to_string(),
        utype: utype.to_string(),
    };

    add_item(&client, item.clone()).await;

    if interactive {
        pause();
    }

    item.age = "44".to_string();

    /* Update the item */
    println!("Modifying table item");

    add_item(&client, item.clone()).await;

    if interactive {
        pause();
    }

    /* Get item and compare it with the one we added */
    println!("Comparing table item to original value");

    scan(&client, item).await;

    if interactive {
        pause();
    }

    /* Delete item */
    println!();
    println!("Deleting item");
    delete_item(&client, &table, &key, &value).await;

    if interactive {
        pause();
    }

    /* Delete table */
    println!("Deleting table");
    delete_table(&client, &table).await;
}
