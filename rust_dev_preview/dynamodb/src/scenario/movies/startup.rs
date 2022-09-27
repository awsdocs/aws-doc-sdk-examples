use std::{collections::HashMap, time::Duration};

use aws_sdk_dynamodb::{
    client::fluent_builders::{CreateTable, PutItem},
    model::{
        AttributeDefinition, KeySchemaElement, KeyType, ProvisionedThroughput, PutRequest,
        ScalarAttributeType, TableStatus, WriteRequest,
    },
    Client, Error,
};
use log::info;
use serde_json::Value;

use super::TABLE_NAME;

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

pub async fn initialize(client: &Client) -> Result<(), Error> {
    if !table_exists(client, TABLE_NAME).await? {
        create_table(client, TABLE_NAME, "title", "year", 10)
            .send()
            .await?;
    }
    await_table(client, TABLE_NAME).await?;

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
    info!("Creating table: {table_name} with capacity {capacity} and key structure {primary_key}:{sort_key}");
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

pub async fn await_table(client: &Client, table_name: &str) -> Result<(), Error> {
    // TODO: Use an adaptive backoff retry, rather than a sleeping loop.
    for _ in 0..3 {
        info!("Checking if table is ready: {table_name}");
        if let Some(table) = client
            .describe_table()
            .table_name(table_name)
            .send()
            .await?
            .table()
        {
            if !matches!(table.table_status, Some(TableStatus::Creating)) {
                info!("Table is ready: {table_name}");
                return Ok(());
            }
        }
        tokio::time::sleep(Duration::from_secs(1)).await;
    }
    Err(Error::Unhandled(Box::new(TableNotReadyError {
        name: table_name.to_string(),
    })))
}

pub async fn load_data(client: &Client) -> Result<(), Error> {
    let data = match serde_json::from_str(include_str!("../../../../../resources/data/movies.json"))
        .expect("loading large movies dataset")
    {
        Value::Array(inner) => inner,
        data => panic!("data must be an array, got: {:?}", data),
    };

    let ops = data
        .iter()
        .map(|value| WriteRequest::builder().set_item().build());
    // .collect::<Vec<WriteRequest>>();

    let mut batches = Vec::with_capacity(ops.len() / 25);
    while let batch = ops.take(25) {
        batches.push(write_batch(client, batch.collect::<Vec<WriteRequest>>()));
    }

    Ok(())
}

pub async fn write_batch(client: &Client, ops: Vec<WriteRequest>) -> Result<(), Error> {
    assert!(
        ops.len() <= 25,
        "Cannot write more than 25 items in a batch"
    );
    let mut unprocessed = Some(HashMap::from([(TABLE_NAME.to_string(), ops)]));
    while unprocessed
        .as_ref()
        .map(|m| m.get(TABLE_NAME).map(|v| v.len()).unwrap_or_default())
        .unwrap_or_default()
        > 0
    {
        unprocessed = client
            .batch_write_item()
            .set_request_items(unprocessed)
            .send()
            .await?
            .unprocessed_items;
    }

    Ok(())
}
