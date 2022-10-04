/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::net::SocketAddr;

use aws_sdk_dynamodb::{Client, Error};
use axum::extract::Extension;
use dynamodb_code_examples::scenario::movies::server::{make_app, movies_in_year};
use dynamodb_code_examples::scenario::movies::{
    shutdown::remove_table, startup::initialize, TABLE_NAME,
};
use tracing_subscriber::layer::SubscriberExt;

struct Server<'a> {
    client: &'a Client,
}

impl<'a> Drop for Server<'a> {
    fn drop(&mut self) {
        remove_table(self.client, TABLE_NAME).await?;
    }
}

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
    tracing_subscriber::registry()
        .with(tracing_subscriber::EnvFilter::new(
            std::env::var("RUST_LOG").unwrap_or_else(|_| "axum_api=debug".into()),
        ))
        .with(tracing_subscriber::fmt::layer());

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

    let app = make_app()
        .layer(Extension(client))
        .layer(Extension(TABLE_NAME));
    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
    tracing::debug!("Listening on {addr}");
    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .unwrap();

    Ok(())
}
