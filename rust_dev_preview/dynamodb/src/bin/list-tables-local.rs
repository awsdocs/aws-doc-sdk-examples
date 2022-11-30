/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

// snippet-start:[dynamodb.rust.list-tables-local]
use aws_sdk_dynamodb::{Client, Endpoint, Error};
use dynamodb_code_examples::{make_config, scenario::list::list_tables, Opt};
use structopt::StructOpt;

/// Lists your tables in DynamoDB local.
#[tokio::main]
async fn main() -> Result<(), Error> {
    let config = make_config(Opt::from_args()).await?;
    let dynamodb_local_config = aws_sdk_dynamodb::config::Builder::from(&config)
        .endpoint_resolver(
            // 8000 is the default dynamodb port
            Endpoint::immutable("http://localhost:8000").expect("Invalid endpoint"),
        )
        .build();

    let client = Client::from_conf(dynamodb_local_config);
    list_tables(&client).await
}
// snippet-end:[dynamodb.rust.list-tables-local]
