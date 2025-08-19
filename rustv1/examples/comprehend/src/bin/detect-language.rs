// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_comprehend::{config::Region, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The text to analyze for language detection.
    #[structopt(short, long)]
    text: Option<String>,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// snippet-start:[comprehend.rust.detect-language]
/// Detects the dominant language in the provided text using Amazon Comprehend.
async fn detect_dominant_language(client: &aws_sdk_comprehend::Client, text: &str) -> Result<(), Error> {
    let response = client
        .detect_dominant_language()
        .text(text)
        .send()
        .await?;

    println!("Detected languages:");
    if let Some(languages) = response.languages {
        for language in languages {
            println!(
                "  Language: {} (confidence: {:.2}%)",
                language.language_code().unwrap_or("unknown"),
                language.score().unwrap_or(0.0) * 100.0
            );
        }
    } else {
        println!("  No languages detected.");
    }

    Ok(())
}
// snippet-end:[comprehend.rust.detect-language]

/// Detects the dominant language in text using Amazon Comprehend.
/// 
/// # Arguments
///
/// * `[-t TEXT]` - The text to analyze. If not provided, uses a default sample.
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt { text, region, verbose } = Opt::parse();

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

    let text_to_analyze = text.as_deref().unwrap_or(
        "Hello, how are you today? I hope you're having a wonderful day!"
    );

    println!("Analyzing text: \"{}\"", text_to_analyze);
    println!();

    detect_dominant_language(&client, text_to_analyze).await?;

    Ok(())
}