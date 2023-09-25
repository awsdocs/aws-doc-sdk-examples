/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

// snippet-start:[dynamodb.rust.list-tables-local]
use aws_sdk_dynamodb::{Client, Error};
use clap::Parser;
use dynamodb_code_examples::{make_config, scenario::list::list_tables, Opt};

/// Lists your tables in DynamoDB local.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let config = make_config(Opt::parse()).await?;
    let dynamodb_local_config = aws_sdk_dynamodb::config::Builder::from(&config)
        .endpoint_url(
            // 8000 is the default dynamodb port
            "http://localhost:8000",
        )
        .build();

    let client = Client::from_conf(dynamodb_local_config);
    list_tables(&client).await?;
    Ok(())
}
// snippet-end:[dynamodb.rust.list-tables-local]
