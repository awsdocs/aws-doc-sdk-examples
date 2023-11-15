/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_qldb::{config::Region, meta::PKG_VERSION, Client as QLDBClient, Error};
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

// List ledgers.
// snippet-start:[qldb.rust.list-ledgers]
async fn show_ledgers(client: &QLDBClient) -> Result<(), Error> {
    let mut pages = client.list_ledgers().into_paginator().page_size(2).send();

    while let Some(page) = pages.next().await {
        println!("* {:?}", page); //Prints an entire page of ledgers.
        for ledger in page.unwrap().ledgers() {
            println!("* {:?}", ledger); //Prints the LedgerSummary of a single ledger.
        }
    }

    Ok(())
}
// snippet-end:[qldb.rust.list-ledgers]

/// Lists your Amazon Quantum Ledger Database (Amazon QLDB) ledgers.
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///    If not supplied, uses the value of the **AWS_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("OLDB client version: {}", PKG_VERSION);
        println!(
            "Region:              {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = QLDBClient::new(&shared_config);

    show_ledgers(&client).await
}
