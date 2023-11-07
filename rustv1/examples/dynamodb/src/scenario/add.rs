/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::types::AttributeValue;
use aws_sdk_dynamodb::{Client, Error};

pub struct Item {
    pub p_type: String,
    pub age: String,
    pub username: String,
    pub first: String,
    pub last: String,
}

#[derive(Debug, PartialEq)]
pub struct ItemOut {
    pub p_type: Option<AttributeValue>,
    pub age: Option<AttributeValue>,
    pub username: Option<AttributeValue>,
    pub first_name: Option<AttributeValue>,
    pub last_name: Option<AttributeValue>,
}

// Add an item to a table.
// snippet-start:[dynamodb.rust.add-item]
pub async fn add_item(client: &Client, item: Item, table: &String) -> Result<ItemOut, Error> {
    let user_av = AttributeValue::S(item.username);
    let type_av = AttributeValue::S(item.p_type);
    let age_av = AttributeValue::S(item.age);
    let first_av = AttributeValue::S(item.first);
    let last_av = AttributeValue::S(item.last);

    let request = client
        .put_item()
        .table_name(table)
        .item("username", user_av)
        .item("account_type", type_av)
        .item("age", age_av)
        .item("first_name", first_av)
        .item("last_name", last_av);

    println!("Executing request [{request:?}] to add item...");

    let resp = request.send().await?;

    let attributes = resp.attributes().unwrap();

    let username = attributes.get("username").cloned();
    let first_name = attributes.get("first_name").cloned();
    let last_name = attributes.get("last_name").cloned();
    let age = attributes.get("age").cloned();
    let p_type = attributes.get("p_type").cloned();

    println!(
        "Added user {:?}, {:?} {:?}, age {:?} as {:?} user",
        username, first_name, last_name, age, p_type
    );

    Ok(ItemOut {
        p_type,
        age,
        username,
        first_name,
        last_name,
    })
}
// snippet-end:[dynamodb.rust.add-item]

// snippet-start:[dynamodb.rust.add-item.test]
#[cfg(test)]
mod test {
    use aws_sdk_dynamodb::types::AttributeValue;
    use sdk_examples_test_utils::single_shot_client;

    use super::{add_item, Item, ItemOut};

    #[tokio::test]
    async fn test_add_item() {
        let client = single_shot_client! {
        sdk: aws_sdk_dynamodb,
        status: 200,
        response: r#"{"Attributes": {
                "p_type": {"S": "Brown"},
                "age": {"N": "27"},
                "username": {"S": "testuser"},
                "first_name": {"S": "Test"},
                "last_name": {"S": "User"}
            }}"#};

        let item = Item {
            username: "testuser".into(),
            p_type: "Brown".into(),
            age: "27".into(),
            first: "Test".into(),
            last: "User".into(),
        };

        let resp = add_item(&client, item, &"test_table".to_string()).await;

        assert!(resp.is_ok(), "{:?}", resp);
        let out = resp.unwrap();

        assert_eq!(
            out,
            ItemOut {
                p_type: Some(AttributeValue::S("Brown".to_string())),
                age: Some(AttributeValue::N("27".to_string())),
                username: Some(AttributeValue::S("testuser".to_string())),
                first_name: Some(AttributeValue::S("Test".to_string())),
                last_name: Some(AttributeValue::S("User".to_string()))
            }
        )
    }
}
// snippet-end:[dynamodb.rust.add-item.test]
