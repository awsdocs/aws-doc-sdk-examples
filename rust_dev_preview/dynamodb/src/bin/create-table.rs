/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::{types::DisplayErrorContext, Client};
use dynamodb_code_examples::{
    make_config, scenario::create::create_table, scenario::error::Error, Opt as BaseOpt,
};
use std::process;
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The table name
    #[structopt(short, long)]
    table: String,

    /// The primary key
    #[structopt(short, long)]
    key: String,

    #[structopt(flatten)]
    base: BaseOpt,
}

/// Creates a DynamoDB table.
/// # Arguments
///
/// * `-k KEY` - The primary key for the table.
/// * `-t TABLE` - The name of the table.
/// * `[-d DEFAULT-REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();

    if let Err(err) = run_example(Opt::from_args()).await {
        eprintln!("Error: {}", DisplayErrorContext(err));
        process::exit(1);
    }
}

async fn run_example(Opt { table, key, base }: Opt) -> Result<(), Error> {
    let shared_config = make_config(base).await?;
    let client = Client::new(&shared_config);

    create_table(&client, &table, &key).await?;

    Ok(())
}
