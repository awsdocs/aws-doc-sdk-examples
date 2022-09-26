/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::{Client, Error};
use dynamodb_code_examples::{make_config, scenario::delete::delete_item, Opt as BaseOpt};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The name of the table.
    #[structopt(short, long)]
    table: String,

    /// The key for the item in the table.
    #[structopt(short, long)]
    key: String,

    /// The value of the item to delete from the table.
    #[structopt(short, long)]
    value: String,

    #[structopt(flatten)]
    base: BaseOpt,
}

/// Deletes an item from an Amazon DynamoDB table.
/// The table schema must use the key as the primary key.
/// # Arguments
///
/// * `-t TABLE` - The name of the table.
/// * `-k KEY` - The table's primary key.
/// * `-v VALUE` - The value of the item's primary key.
/// * `[-r REGION]` - The region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt {
        key,
        table,
        value,
        base,
    } = Opt::from_args();

    let shared_config = make_config(base).await?;
    let client = Client::new(&shared_config);

    delete_item(&client, &table, &key, &value).await
}
