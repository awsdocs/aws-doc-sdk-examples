// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
use super::Movie;
use crate::scenario::error::Error;
use aws_sdk_dynamodb::{
    operation::create_table::builders::CreateTableFluentBuilder,
    types::{
        AttributeDefinition, KeySchemaElement, KeyType, ScalarAttributeType, TableStatus,
        WriteRequest,
    },
    Client,
};
use futures::future::join_all;
use std::{collections::HashMap, time::Duration};
use tracing::{debug, info, trace};

#[tracing::instrument(level = "trace")]
pub async fn initialize(client: &Client, table_name: &str) -> Result<(), Error> {
    info!("Initializing Movies DynamoDB in {table_name}");

    if table_exists(client, table_name).await? {
        info!("Found existing table {table_name}");
    } else {
        info!("Table does not exist, creating {table_name}");
        create_table(client, table_name, "year", "title")?
            .send()
            .await?;
        await_table(client, table_name).await?;
        load_data(client, table_name).await?;
    }

    Ok(())
}

#[tracing::instrument(level = "trace")]
// Does table exist?
// snippet-start:[dynamodb.rust.movies-does_table_exist]
pub async fn table_exists(client: &Client, table: &str) -> Result<bool, Error> {
    debug!("Checking for table: {table}");
    let table_list = client.list_tables().send().await;

    match table_list {
        Ok(list) => Ok(list.table_names().contains(&table.into())),
        Err(e) => Err(e.into()),
    }
}
// snippet-end:[dynamodb.rust.movies-does_table_exist]

#[tracing::instrument(level = "trace")]
// snippet-start:[dynamodb.rust.movies-create_table_request]
pub fn create_table(
    client: &Client,
    table_name: &str,
    primary_key: &str,
    sort_key: &str,
) -> Result<CreateTableFluentBuilder, Error> {
    info!("Creating table: {table_name} key structure {primary_key}:{sort_key}");
    Ok(client
        .create_table()
        .table_name(table_name)
        .key_schema(
            KeySchemaElement::builder()
                .attribute_name(primary_key)
                .key_type(KeyType::Hash)
                .build()
                .expect("Failed to build KeySchema"),
        )
        .attribute_definitions(
            AttributeDefinition::builder()
                .attribute_name(primary_key)
                .attribute_type(ScalarAttributeType::N)
                .build()
                .expect("Failed to build attribute definition"),
        )
        .key_schema(
            KeySchemaElement::builder()
                .attribute_name(sort_key)
                .key_type(KeyType::Range)
                .build()
                .expect("Failed to build KeySchema"),
        )
        .attribute_definitions(
            AttributeDefinition::builder()
                .attribute_name(sort_key)
                .attribute_type(ScalarAttributeType::S)
                .build()
                .expect("Failed to build attribute definition"),
        )
        .billing_mode(aws_sdk_dynamodb::types::BillingMode::PayPerRequest))
}
// snippet-end:[dynamodb.rust.movies-create_table_request]

const TABLE_WAIT_POLLS: u64 = 6;
const TABLE_WAIT_TIMEOUT: u64 = 5; // Takes about 30 seconds in my experience.
pub async fn await_table(client: &Client, table_name: &str) -> Result<(), Error> {
    // TODO: Use an adaptive backoff retry, rather than a sleeping loop.
    for _ in 0..TABLE_WAIT_POLLS {
        debug!("Checking if table is ready: {table_name}");
        if let Some(table) = client
            .describe_table()
            .table_name(table_name)
            .send()
            .await?
            .table()
        {
            if matches!(table.table_status, Some(TableStatus::Active)) {
                debug!("Table is ready");
                return Ok(());
            } else {
                debug!("Table is NOT ready")
            }
        }
        tokio::time::sleep(Duration::from_secs(TABLE_WAIT_TIMEOUT)).await;
    }

    Err(Error::table_not_ready(table_name))
}

// Must be less than 26.
const CHUNK_SIZE: usize = 25;

pub async fn load_data(client: &Client, table_name: &str) -> Result<(), Error> {
    debug!("Loading data into table {table_name}");
    let data: Vec<Movie> = serde_json::from_str(include_str!("../../../moviedata.json"))
        .expect("loading large movies dataset");

    let data_size = data.len();
    trace!("Loading {data_size} items in batches of {CHUNK_SIZE}");

    let ops = data
        .iter()
        .map(|v| {
            WriteRequest::builder()
                .set_put_request(Some(
                    v.try_into().expect("Failed to convert movie to PutRequest"),
                ))
                .build()
        })
        .collect::<Vec<WriteRequest>>();

    let batches = ops
        .chunks(CHUNK_SIZE)
        .map(|chunk| write_batch(client, table_name, chunk));
    let batches_count = batches.len();

    trace!("Awaiting batches, count: {batches_count}");
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
        trace!("Adding {count} unprocessed items");
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
