// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

// snippet-start:[dynamodb.rust.list-tables-local]
use aws_config::BehaviorVersion;
use aws_sdk_dynamodb::{Client, Error};

/// Lists your tables in DynamoDB local.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let config = aws_config::defaults(BehaviorVersion::latest())
        .test_credentials()
        .load()
        .await;
    let dynamodb_local_config = aws_sdk_dynamodb::config::Builder::from(&config)
        // Override the endpoint in the config to use a local dynamodb server.
        .endpoint_url(
            // DynamoDB run locally uses port 8000 by default.
            "http://localhost:8000",
        )
        .build();

    let client = Client::from_conf(dynamodb_local_config);

    let resp = client.list_tables().send().await?;

    println!("Found {} tables", resp.table_names().len());
    for name in resp.table_names() {
        println!("  {}", name);
    }

    Ok(())
}
// snippet-end:[dynamodb.rust.list-tables-local]
