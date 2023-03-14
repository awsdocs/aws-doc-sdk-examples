/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use crate::scenario::error::Error;
use aws_sdk_dynamodb::{
    operation::{delete_item::DeleteItemOutput, delete_table::DeleteTableOutput},
    types::AttributeValue,
    Client,
};

// Deletes an item from the table.
// snippet-start:[dynamodb.rust.delete-item]
pub async fn delete_item(
    client: &Client,
    table: &str,
    key: &str,
    value: &str,
) -> Result<DeleteItemOutput, Error> {
    match client
        .delete_item()
        .table_name(table)
        .key(key, AttributeValue::S(value.into()))
        .send()
        .await
    {
        Ok(out) => {
            println!("Deleted item from table");
            Ok(out)
        }
        Err(e) => Err(Error::unhandled(e)),
    }
}
// snippet-end:[dynamodb.rust.delete-item]

// Delete a table.
// snippet-start:[dynamodb.rust.delete-table]
pub async fn delete_table(client: &Client, table: &str) -> Result<DeleteTableOutput, Error> {
    let resp = client.delete_table().table_name(table).send().await;

    match resp {
        Ok(out) => {
            println!("Deleted table");
            Ok(out)
        }
        Err(e) => Err(Error::Unhandled(e.into())),
    }
}
// snippet-end:[dynamodb.rust.delete-table]

#[cfg(test)]
mod test {
    use super::delete_item;
    use sdk_examples_test_utils::single_shot_client;

    // snippet-start:[dynamodb.rust.delete-item.test_err]
    #[tokio::test]
    async fn test_delete_item_err() {
        let client = single_shot_client! {
            sdk: aws_sdk_dynamodb,
            status: 500,
            response: r#""#
        };

        let resp = delete_item(&client, "test_table", "id", "test").await;

        assert!(resp.is_err(), "{resp:?}");
    }
    // snippet-end:[dynamodb.rust.delete-item.test_err]
}
