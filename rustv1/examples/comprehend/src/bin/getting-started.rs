// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_comprehend::{config::Region, Client, Error};
use clap::Parser;
use comprehend_code_examples::getting_started::scenario::ComprehendScenario;

#[derive(Debug, Parser)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// snippet-start:[comprehend.rust.getting-started]
/// Amazon Comprehend Getting Started - demonstrates comprehensive text analysis capabilities.
/// 
/// This example shows how to use Amazon Comprehend to:
/// - Detect the dominant language in text
/// - Extract entities (people, places, organizations, etc.)
/// - Identify key phrases
/// - Analyze sentiment
/// - Detect personally identifiable information (PII)
/// - Analyze syntax and parts of speech
/// 
/// # Arguments
///
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt { region, verbose } = Opt::parse();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    if verbose {
        println!("Comprehend client version: {}", aws_sdk_comprehend::meta::PKG_VERSION);
        println!(
            "Region:                    {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    let scenario = ComprehendScenario::new(client);
    
    match scenario.run().await {
        Ok(()) => {
            println!("Comprehend getting started scenario completed successfully!");
        }
        Err(e) => {
            eprintln!("Error running Comprehend scenario: {}", e);
            std::process::exit(1);
        }
    }

    Ok(())
}
// snippet-end:[comprehend.rust.getting-started]