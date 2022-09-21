/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use std::fmt::Display;

use aws_sdk_dynamodb::{Client, Error};
use dynamodb_code_examples::{
    make_config,
    scenario::create::{add_item, Item},
    Opt as BaseOpt,
};
use structopt::StructOpt;

#[derive(Debug)]
struct PermissionError {
    p_type: String,
}

impl std::error::Error for PermissionError {}
impl Display for PermissionError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "\n{} is not a valid permission type.\nYou must specify a permission type value of 'admin' or 'standard_user':\n-p PERMISSION-TYPE\n", self.p_type)
    }
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
async fn main() -> Result<(), Error> {
    let Opt {
        table,
        username,
        p_type,
        age,
        first,
        last,
        base,
    } = Opt::from_args();

    if !["standard_user", "admin"].contains(&p_type.as_str()) {
        return Err(Error::Unhandled(Box::new(PermissionError { p_type })));
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
    .await
}
