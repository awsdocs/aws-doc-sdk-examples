/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::{Client, Error};

// List your tables.
// snippet-start:[dynamodb.rust.list-tables]
pub async fn list_tables(client: &Client) -> Result<Vec<String>, Error> {
    let paginator = client.list_tables().into_paginator().items().send();
    let table_names = paginator.collect::<Result<Vec<_>, _>>().await?;

    println!("Tables:");

    for name in &table_names {
        println!("  {}", name);
    }

    println!("Found {} tables", table_names.len());
    Ok(table_names)
}
// snippet-end:[dynamodb.rust.list-tables]

#[cfg(test)]
mod test_list_tables {
    use sdk_examples_test_utils::single_shot_client;

    #[tokio::test]
    async fn test_list_tables() {
        let client = single_shot_client! {
            sdk: aws_sdk_dynamodb,
            status: 200,
            response: r#"{"TableNames":["a","b","c"]}"#
        };

        let tables = super::list_tables(&client).await;

        assert!(tables.is_ok(), "{tables:?}");
        assert_eq!(tables.unwrap(), vec!["a", "b", "c"]);
    }
}

// List only 10 of your tables.
// snippet-start:[dynamodb.rust.list10-tables]
pub async fn list_tables_limit_10(
    client: &aws_sdk_dynamodb::Client,
) -> Result<(), aws_sdk_dynamodb::Error> {
    let resp = client.list_tables().limit(10).send().await?;

    println!("Tables:");

    let names = resp.table_names();

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
pub async fn list_tables_iterative(client: &Client) -> Result<Vec<String>, Error> {
    // snippet-start:[dynamodb.rust.list-more-tables-list]
    let mut resp = client.list_tables().limit(10).send().await?;
    let mut names = resp.table_names.unwrap_or_default();
    let len = names.len();

    let mut num_tables = len;

    println!("Tables:");

    for name in &names {
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

        let mut more_names = resp.table_names.unwrap_or_default();
        num_tables += more_names.len();

        for name in &more_names {
            println!("  {}", name);
        }
        names.append(&mut more_names);
    }

    println!();
    println!("Found {} tables", num_tables);

    Ok(names)
    // snippet-end:[dynamodb.rust.list-more-tables-list]
}
// snippet-end:[dynamodb.rust.list-more-tables]

#[cfg(test)]
mod test_list_more_tables {
    use sdk_examples_test_utils::test_event;

    use super::list_tables_iterative;

    #[tokio::test]
    async fn test_list_tables_iterative() {
        let client = aws_sdk_dynamodb::Client::from_conf(
            sdk_examples_test_utils::client_config!(aws_sdk_dynamodb)
                .http_client(
                    aws_smithy_runtime::client::http::test_util::StaticReplayClient::new(vec![
                        test_event!(
                            "",
                            (
                                200,
                                r#"{"LastEvaluatedTableName":"c","TableNames":["a","b","c"]}"#
                            )
                        ),
                        test_event!(
                            "",
                            (
                                200,
                                r#"{"LastEvaluatedTableName":"f","TableNames":["d","e","f"]}"#
                            )
                        ),
                        test_event!("", (200, r#"{"TableNames":["g","h"]}"#)),
                    ]),
                )
                .build(),
        );

        let resp = list_tables_iterative(&client).await;

        assert!(resp.is_ok(), "{resp:?}");
        assert_eq!(resp.unwrap(), vec!["a", "b", "c", "d", "e", "f", "g", "h"]);
    }
}

// Lists up to 10 tables and indicates whether there are more.
// snippet-start:[dynamodb.rust.are-more-tables]
pub async fn list_tables_are_more(client: &Client) -> Result<(), Error> {
    // snippet-start:[dynamodb.rust.are-more-tables-limit]
    let resp = client.list_tables().limit(10).send().await?;
    // snippet-end:[dynamodb.rust.are-more-tables-limit]

    println!("Tables:");

    let names = resp.table_names();

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
pub async fn list_items(client: &Client, table: &str, page_size: Option<i32>) -> Result<(), Error> {
    let page_size = page_size.unwrap_or(10);
    let items: Result<Vec<_>, _> = client
        .scan()
        .table_name(table)
        .limit(page_size)
        .into_paginator()
        .items()
        .send()
        .collect()
        .await;

    println!("Items in table (up to {page_size}):");
    for item in items? {
        println!("   {:?}", item);
    }

    Ok(())
}
// snippet-end:[dynamodb.rust.list-items]
