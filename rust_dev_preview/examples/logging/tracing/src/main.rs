/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

// snippet-start:[tracing.rust.main]
use aws_config::meta::region::RegionProviderChain;
use aws_sdk_dynamodb::{config::Region, meta::PKG_VERSION, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

#[tokio::main]
async fn main() -> Result<(), Error> {
    // snippet-start:[tracing.rust.main-tracing-init]
    tracing_subscriber::fmt::init();
    // snippet-end:[tracing.rust.main-tracing-init]

    let Opt { region, verbose } = Opt::parse();

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

        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    let resp = client.list_tables().send().await?;

    println!("Tables:");

    let names = resp.table_names();
    let len = names.len();

    for name in names {
        println!("  {}", name);
    }

    println!("Found {} tables", len);
    Ok(())
}
// snippet-end:[tracing.rust.main]
