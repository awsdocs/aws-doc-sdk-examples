/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::client::fluent_builders::Query;
use aws_sdk_dynamodb::model::AttributeValue;
use aws_sdk_dynamodb::{Client, Error};
use dynamodb_code_examples::scenario::movies::server::{make_app, movies_in_year};
use dynamodb_code_examples::scenario::movies::{
    shutdown::remove_table, startup::initialize, TABLE_NAME,
};

/// A partial reimplementation of
/// <https://docs.amazonaws.cn/en_us/amazondynamodb/latest/developerguide/GettingStarted.Ruby.html>
/// in Rust
///
/// - Create table
/// - Wait for table to be ready
/// - Add a couple of rows
/// - Query for those rows
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let config = aws_config::from_env().load().await;
    let client = Client::new(&config);

    initialize(&client, TABLE_NAME).await?;

    let films_2222 = movies_in_year(&client, TABLE_NAME, 2222)
        .await
        .expect("Query should succeed");

    // this isn't back to the future, there are no movies from 2022
    assert_eq!(films_2222.len(), 0);

    let films_2013 = movies_in_year(&client, TABLE_NAME, 2013)
        .await
        .expect("Query should succeed");

    assert!(films_2013.len() > 0, "should get movies back");

    println!("Deleting table.");

    remove_table(&client, TABLE_NAME).await?;

    Ok(())
}
