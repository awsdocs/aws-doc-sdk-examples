use std::{collections::HashMap, time::Duration};

use aws_sdk_dynamodb::{
    client::fluent_builders::CreateTable,
    model::{
        AttributeDefinition, KeySchemaElement, KeyType, ProvisionedThroughput, ScalarAttributeType,
        TableStatus, WriteRequest,
    },
    Client, Error,
};
use futures::future::join_all;
use log::info;

use super::Movie;

#[derive(Debug)]
pub struct TableNotReadyError {
    name: String,
}

impl std::fmt::Display for TableNotReadyError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(
            f,
            "Table was not ready after several attempts: {}",
            self.name
        )
    }
}

impl std::error::Error for TableNotReadyError {}

const CAPACITY: i64 = 10;

pub async fn initialize(client: &Client, table_name: &str) -> Result<(), Error> {
    eprintln!("Initializing Movies DynamoDB with {client:?}");

    if table_exists(client, table_name).await? {
        eprintln!("Found existing table {table_name}");
    } else {
        eprintln!("Table does not exist, creating {table_name}");
        create_table(client, table_name, "year", "title", CAPACITY)
            .send()
            .await?;
        await_table(client, table_name).await?;
        load_data(client, table_name).await?;
    }

    Ok(())
}

// Does table exist?
// snippet-start:[dynamodb.rust.movies-table_exists]
pub async fn table_exists(client: &Client, table: &str) -> Result<bool, Error> {
    info!("Checking for table: {table}");
    let table_list = client.list_tables().send().await;

    match table_list {
        Ok(list) => Ok(list.table_names().as_ref().unwrap().contains(&table.into())),
        Err(e) => Err(Error::Unhandled(Box::new(e))),
    }
}
// snippet-end:[dynamodb.rust.movies-table_exists]

// snippet-start:[dynamodb.rust.movies-create_table_request]
pub fn create_table(
    client: &Client,
    table_name: &str,
    primary_key: &str,
    sort_key: &str,
    capacity: i64,
) -> CreateTable {
    eprintln!("Creating table: {table_name} with capacity {capacity} and key structure {primary_key}:{sort_key}");
    client
        .create_table()
        .table_name(table_name)
        .key_schema(
            KeySchemaElement::builder()
                .attribute_name(primary_key)
                .key_type(KeyType::Hash)
                .build(),
        )
        .attribute_definitions(
            AttributeDefinition::builder()
                .attribute_name(primary_key)
                .attribute_type(ScalarAttributeType::N)
                .build(),
        )
        .key_schema(
            KeySchemaElement::builder()
                .attribute_name(sort_key)
                .key_type(KeyType::Range)
                .build(),
        )
        .attribute_definitions(
            AttributeDefinition::builder()
                .attribute_name(sort_key)
                .attribute_type(ScalarAttributeType::S)
                .build(),
        )
        .provisioned_throughput(
            ProvisionedThroughput::builder()
                .read_capacity_units(capacity)
                .write_capacity_units(capacity)
                .build(),
        )
}
// snippet-end:[dynamodb.rust.movies-create_table_request]

const TABLE_WAIT_POLLS: u64 = 6;
const TABLE_WAIT_TIMEOUT: u64 = 5; // Takes about 30 seconds in my experience
pub async fn await_table(client: &Client, table_name: &str) -> Result<(), Error> {
    // TODO: Use an adaptive backoff retry, rather than a sleeping loop.
    for _ in 0..TABLE_WAIT_POLLS {
        eprintln!("Checking if table is ready: {table_name}");
        if let Some(table) = client
            .describe_table()
            .table_name(table_name)
            .send()
            .await?
            .table()
        {
            if matches!(table.table_status, Some(TableStatus::Active)) {
                eprintln!("Table is ready");
                return Ok(());
            } else {
                eprintln!("Table is NOT ready")
            }
        }
        tokio::time::sleep(Duration::from_secs(TABLE_WAIT_TIMEOUT)).await;
    }

    Err(Error::Unhandled(Box::new(TableNotReadyError {
        name: table_name.to_string(),
    })))
}

// Must be less than 26
const CHUNK_SIZE: usize = 25;

pub async fn load_data(client: &Client, table_name: &str) -> Result<(), Error> {
    eprintln!("Loading data into table {table_name}");
    let data: Vec<Movie> =
        serde_json::from_str(include_str!("../../../../../resources/data/movies.json"))
            .expect("loading large movies dataset");

    let data_size = data.len();
    eprintln!("Loading {data_size} items in batches of {CHUNK_SIZE}");

    let ops = data
        .iter()
        .map(|v| {
            WriteRequest::builder()
                .set_put_request(Some(v.into()))
                .build()
        })
        .collect::<Vec<WriteRequest>>();

    let batches = ops
        .chunks(CHUNK_SIZE)
        .map(|chunk| write_batch(client, table_name, chunk));
    let batches_count = batches.len();

    eprintln!("Awaiting batches, count: {batches_count}");
    join_all(batches).await;

    Ok(())
}

pub async fn write_batch(
    client: &Client,
    table_name: &str,
    ops: &[WriteRequest],
) -> Result<(), Error> {
    assert!(
        ops.len() <= 25,
        "Cannot write more than 25 items in a batch"
    );
    let mut unprocessed = Some(HashMap::from([(table_name.to_string(), ops.to_vec())]));
    while unprocessed_count(unprocessed.as_ref(), table_name) > 0 {
        let count = unprocessed_count(unprocessed.as_ref(), table_name);
        eprintln!("Adding {count} unprocessed items");
        unprocessed = client
            .batch_write_item()
            .set_request_items(unprocessed)
            .send()
            .await?
            .unprocessed_items;
    }

    Ok(())
}

fn unprocessed_count(
    unprocessed: Option<&HashMap<String, Vec<WriteRequest>>>,
    table_name: &str,
) -> usize {
    unprocessed
        .map(|m| m.get(table_name).map(|v| v.len()).unwrap_or_default())
        .unwrap_or_default()
}
