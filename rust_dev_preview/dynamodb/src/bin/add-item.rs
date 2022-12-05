/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_sdk_dynamodb::{types::DisplayErrorContext, Client};
use dynamodb_code_examples::{
    make_config,
    scenario::add::{add_item, Item},
    scenario::error::Error,
    Opt as BaseOpt,
};
use std::process;
use structopt::StructOpt;

#[derive(thiserror::Error, Debug)]
#[error(
    "{p_type} is not a valid permission type.\n\
        You must specify a permission type value of 'admin' or 'standard_user':\n\
        -p PERMISSION-TYPE\n"
)]
struct PermissionError {
    p_type: String,
}

#[derive(Debug, StructOpt)]
struct Opt {
    /// The permission type of the user, standard_user or admin.
    #[structopt(short, long)]
    p_type: String,

    /// The user's age.
    #[structopt(short, long)]
    age: String,

    /// The user's username.
    #[structopt(short, long)]
    username: String,

    /// The user's first name.
    #[structopt(short, long)]
    first: String,

    /// The user's last name.
    #[structopt(short, long)]
    last: String,

    /// The table name.
    #[structopt(short, long)]
    table: String,

    #[structopt(flatten)]
    base: BaseOpt,
}

/// Adds an item to an Amazon DynamoDB table.
/// The table schema must use one of username, p_type, age, first, or last as the primary key.
/// # Arguments
///
/// * `-t TABLE` - The name of the table.
/// * `-u USERNAME` - The username of the new table item.
/// * `-p PERMISSION-TYPE` - The type of user, either "standard_user" or "admin".
/// * `-a AGE` - The age of the user.
/// * `-f FIRST` - The first name of the user.
/// * `-l LAST` - The last name of the user.
/// * `[-r REGION]` - The region in which the table is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() {
    if let Err(err) = run_example(Opt::from_args()).await {
        eprintln!("Error: {}", DisplayErrorContext(err));
        process::exit(1);
    }
}

async fn run_example(
    Opt {
        table,
        username,
        p_type,
        age,
        first,
        last,
        base,
    }: Opt,
) -> Result<(), Error> {
    if !["standard_user", "admin"].contains(&p_type.as_str()) {
        return Err(Error::unhandled(PermissionError { p_type }));
    }

    let shared_config = make_config(base).await?;
    let client = Client::new(&shared_config);

    add_item(
        &client,
        Item {
            p_type,
            age,
            first,
            last,
            username,
        },
        &table,
    )
    .await?;

    Ok(())
}
