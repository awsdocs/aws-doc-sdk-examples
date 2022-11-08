/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::{Client, Error};
use tokio_stream::StreamExt;

// List your tables.
// snippet-start:[dynamodb.rust.list-tables]
pub async fn list_tables(client: &Client) -> Result<(), Error> {
    let paginator = client.list_tables().into_paginator().items().send();
    let table_names = paginator.collect::<Result<Vec<_>, _>>().await?;

    println!("Tables:");

    for name in &table_names {
        println!("  {}", name);
    }

    println!("Found {} tables", table_names.len());
    Ok(())
}
// snippet-end:[dynamodb.rust.list-tables]

// List only 10 of your tables.
// snippet-start:[dynamodb.rust.list10-tables]
pub async fn list_tables_limit_10(
    client: &aws_sdk_dynamodb::Client,
) -> Result<(), aws_sdk_dynamodb::Error> {
    let resp = client.list_tables().limit(10).send().await?;

    println!("Tables:");

    let names = resp.table_names().unwrap_or_default();

    for name in names {
        println!("  {}", name);
    }

    println!();
    println!("Found {} tables", names.len());

    Ok(())
}
// snippet-end:[dynamodb.rust.list10-tables]

// List your tables 10 at a time.
// snippet-start:[dynamodb.rust.list-more-tables]
pub async fn list_tables_iterative(client: &Client) -> Result<(), Error> {
    // snippet-start:[dynamodb.rust.list-more-tables-list]
    let mut resp = client.list_tables().limit(10).send().await?;
    let names = resp.table_names.unwrap_or_default();
    let len = names.len();

    let mut num_tables = len;

    println!("Tables:");

    for name in names {
        println!("  {}", name);
    }

    while resp.last_evaluated_table_name.is_some() {
        println!("-- more --");
        resp = client
            .list_tables()
            .limit(10)
            .exclusive_start_table_name(
                resp.last_evaluated_table_name
                    .as_deref()
                    .unwrap_or_default(),
            )
            .send()
            .await?;

        let names = resp.table_names.unwrap_or_default();
        num_tables += names.len();

        for name in names {
            println!("  {}", name);
        }
    }

    println!();
    println!("Found {} tables", num_tables);

    Ok(())
    // snippet-end:[dynamodb.rust.list-more-tables-list]
}
// snippet-end:[dynamodb.rust.list-more-tables]

// Lists up to 10 tables and indicates whether there are more.
// snippet-start:[dynamodb.rust.are-more-tables]
pub async fn list_tables_are_more(client: &Client) -> Result<(), Error> {
    // snippet-start:[dynamodb.rust.are-more-tables-limit]
    let resp = client.list_tables().limit(10).send().await?;
    // snippet-end:[dynamodb.rust.are-more-tables-limit]

    println!("Tables:");

    let names = resp.table_names().unwrap_or_default();

    for name in names {
        println!("  {}", name);
    }

    println!();
    println!("Found {} tables", names.len());

    // snippet-start:[dynamodb.rust.are-more-tables-more]
    if resp.last_evaluated_table_name.is_some() {
        println!("There are more tables");
    }
    // snippet-end:[dynamodb.rust.are-more-tables-more]

    Ok(())
}
// snippet-end:[dynamodb.rust.are-more-tables]

// Lists the items in a table.
// snippet-start:[dynamodb.rust.list-items]
pub async fn list_items(client: &Client, table: &str) -> Result<(), Error> {
    let items: Result<Vec<_>, _> = client
        .scan()
        .table_name(table)
        .into_paginator()
        .items()
        .send()
        .collect()
        .await;

    println!("Items in table:");
    for item in items? {
        println!("   {:?}", item);
    }

    Ok(())
}
// snippet-end:[dynamodb.rust.list-items]
