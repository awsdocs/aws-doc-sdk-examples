/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_dynamodb::model::AttributeValue;
use aws_sdk_dynamodb::{Client, Error, Region, PKG_VERSION};
use std::process;
use structopt::StructOpt;

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

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Add an item to a table.
// snippet-start:[dynamodb.rust.add-item]
async fn add_item(
    client: &Client,
    table: &str,
    username: &str,
    p_type: &str,
    age: &str,
    first: &str,
    last: &str,
) -> Result<(), Error> {
    let user_av = AttributeValue::S(username.into());
    let type_av = AttributeValue::S(p_type.into());
    let age_av = AttributeValue::S(age.into());
    let first_av = AttributeValue::S(first.into());
    let last_av = AttributeValue::S(last.into());

    let request = client
        .put_item()
        .table_name(table)
        .item("username", user_av)
        .item("account_type", type_av)
        .item("age", age_av)
        .item("first_name", first_av)
        .item("last_name", last_av);

    println!("Executing request [{:?}] to add item...", request);

    request.send().await?;

    println!(
        "Added user {}, {} {}, age {} as {} user",
        username, first, last, age, p_type
    );

    Ok(())
}
// snippet-end:[dynamodb.rust.add-item]

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
        region,
        verbose,
    } = Opt::from_args();

    if p_type != "standard_user" && p_type != "admin" {
        println!("\n{} is not a valid permission type", p_type);
        println!("You must specify a permission type value of 'admin' or 'standard_user':");
        println!("-p PERMISSION-TYPE\n");
        process::exit(1);
    }

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("DynamoDB client version: {}", PKG_VERSION);
        println!(
            "Region:                  {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!("Table:  {}", table);
        println!("User:   {}", username);
        println!("Type:   {}", p_type);
        println!("Age:    {}", age);
        println!("First:  {}", first);
        println!("Last:   {}\n", last);

        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    add_item(&client, &table, &username, &p_type, &age, &first, &last).await
}
