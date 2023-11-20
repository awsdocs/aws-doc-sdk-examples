/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use crate::scenario::error::Error;
use aws_sdk_dynamodb::operation::create_table::CreateTableOutput;
use aws_sdk_dynamodb::types::{
    AttributeDefinition, KeySchemaElement, KeyType, ProvisionedThroughput, ScalarAttributeType,
};
use aws_sdk_dynamodb::Client;

// Create a table.
// snippet-start:[dynamodb.rust.create-table]
pub async fn create_table(
    client: &Client,
    table: &str,
    key: &str,
) -> Result<CreateTableOutput, Error> {
    let a_name: String = key.into();
    let table_name: String = table.into();

    let ad = AttributeDefinition::builder()
        .attribute_name(&a_name)
        .attribute_type(ScalarAttributeType::S)
        .build()
        .map_err(Error::BuildError)?;

    let ks = KeySchemaElement::builder()
        .attribute_name(&a_name)
        .key_type(KeyType::Hash)
        .build()
        .map_err(Error::BuildError)?;

    let pt = ProvisionedThroughput::builder()
        .read_capacity_units(10)
        .write_capacity_units(5)
        .build()
        .map_err(Error::BuildError)?;

    let create_table_response = client
        .create_table()
        .table_name(table_name)
        .key_schema(ks)
        .attribute_definitions(ad)
        .provisioned_throughput(pt)
        .send()
        .await;

    match create_table_response {
        Ok(out) => {
            println!("Added table {} with key {}", table, key);
            Ok(out)
        }
        Err(e) => {
            eprintln!("Got an error creating table:");
            eprintln!("{}", e);
            Err(Error::unhandled(e))
        }
    }
}
// snippet-end:[dynamodb.rust.create-table]

#[cfg(test)]
mod test {
    use sdk_examples_test_utils::single_shot_client;

    use super::create_table;

    // snippet-start:[dynamodb.rust.create-table.test]
    #[tokio::test]
    async fn test_create_table() {
        let client = single_shot_client! {
            sdk: aws_sdk_dynamodb,
            status: 200,
            response: r#""#
        };

        let resp = create_table(&client, "test_table", "test_key").await;

        assert!(resp.is_ok(), "{resp:?}");
    }
    // snippet-end:[dynamodb.rust.create-table.test]

    // snippet-start:[dynamodb.rust.create-table.test_err]
    #[tokio::test]
    async fn test_create_table_err() {
        let client = single_shot_client! {
            sdk: aws_sdk_dynamodb,
            status: 400,
            response: r#""#
        };

        let resp = create_table(&client, "test_table", "test_key").await;

        assert!(resp.is_err(), "{resp:?}");
    }
    // snippet-end:[dynamodb.rust.create-table.test_err]
}
