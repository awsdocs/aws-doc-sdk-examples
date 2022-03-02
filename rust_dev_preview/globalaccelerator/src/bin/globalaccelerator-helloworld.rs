/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_globalaccelerator::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;
use tokio_stream::StreamExt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// List accelerator names and ARNs.
// snippet-start:[globalaccelerator.rust.globalaccelerator-helloworld]
async fn show_accelerators(client: &Client) -> Result<(), Error> {
    println!("Welcome to the AWS Rust SDK Global Accelerator example!");
    println!();
    let mut paginator = client.list_accelerators().into_paginator().send();

    while let Some(page) = paginator.try_next().await? {
        for accelerator in page.accelerators().unwrap_or_default().iter() {
            let accelerator_arn = accelerator.name().unwrap_or_default();
            let accelerator_name = accelerator.accelerator_arn().unwrap_or_default();

            println!("Accelerator Name : {}", accelerator_name);
            println!("Accelerator ARN : {}", accelerator_arn);
            println!();
        }
    }

    Ok(())
}
// snippet-end:[globalaccelerator.rust.globalaccelerator-helloworld]

/// Lists your AWS Global Accelerator accelerator names and ARNs in the Region.
/// # Arguments
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { verbose } = Opt::from_args();

    // Global Accelerator is a global service with its API in us-west-2
    let region_provider = RegionProviderChain::first_try(Region::new("us-west-2"));
    println!();

    if verbose {
        println!("globalaccelerator client version: {}", PKG_VERSION);
        println!(
            "Region:                   {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_accelerators(&client).await
}
