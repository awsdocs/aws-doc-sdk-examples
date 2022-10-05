/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::net::SocketAddr;

use aws_sdk_dynamodb::{Client, Error};
use axum::extract::Extension;
use dynamodb_code_examples::scenario::movies::server::{make_app, movies_in_year};
use dynamodb_code_examples::scenario::movies::{startup::initialize, TABLE_NAME};

async fn run(client: Client) {
    let app = make_app()
        .layer(Extension(client))
        .layer(Extension(TABLE_NAME));

    let addr = SocketAddr::from(([127, 0, 0, 1], 3000));
    eprintln!("Listening on {addr}");
    axum::Server::bind(&addr)
        .serve(app.into_make_service())
        .await
        .expect("Server crashed");
}

async fn verify(client: &Client) -> Result<(), Error> {
    let films_2222 = movies_in_year(&client, TABLE_NAME, 2222)
        .await
        .expect("Query should succeed");

    // this isn't back to the future, there are no movies from 2022
    assert_eq!(films_2222.len(), 0);

    let films_2013 = movies_in_year(&client, TABLE_NAME, 2013)
        .await
        .expect("Query should succeed");

    assert!(films_2013.len() > 0, "should get movies back");

    Ok(())
}

/// A DynamoDB backed movie database. Loads a bunch of movies
///
/// - Create table
/// - Wait for table to be ready
/// - Add a bunch of data
/// - Check the data is there
/// - Start an HTTP server to return subsets of those rows
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let config = aws_config::from_env().load().await;
    let client = Client::new(&config);

    initialize(&client, TABLE_NAME).await?;

    verify(&client).await?;

    run(client).await;

    Ok(())
}
