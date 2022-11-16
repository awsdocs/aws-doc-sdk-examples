/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use crate::scenario::error::Error;
use aws_sdk_dynamodb::{model::AttributeValue, Client};

// Deletes an item from the table.
// snippet-start:[dynamodb.rust.delete-item]
pub async fn delete_item(
    client: &Client,
    table: &str,
    key: &str,
    value: &str,
) -> Result<(), Error> {
    match client
        .delete_item()
        .table_name(table)
        .key(key, AttributeValue::S(value.into()))
        .send()
        .await
    {
        Ok(_) => {
            println!("Deleted item from table");
            Ok(())
        }
        Err(e) => Err(Error::unhandled(e)),
    }
}
// snippet-end:[dynamodb.rust.delete-item]

// Delete a table.
// snippet-start:[dynamodb.rust.delete-table]
pub async fn delete_table(client: &Client, table: &str) -> Result<(), Error> {
    client.delete_table().table_name(table).send().await?;

    println!("Deleted table");

    Ok(())
}
// snippet-end:[dynamodb.rust.delete-table]
