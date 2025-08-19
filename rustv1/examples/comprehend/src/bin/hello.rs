// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_comprehend::{config::Region, meta::PKG_VERSION, Client, Error};
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

// snippet-start:[comprehend.rust.hello]
/// A simple example demonstrating Amazon Comprehend by detecting the dominant language
/// in a sample text.
async fn detect_sample_language(client: &aws_sdk_comprehend::Client) {
    let sample_text = "Hello, how are you today? I hope you're having a great day!";
    
    println!("Analyzing sample text: \"{}\"", sample_text);
    
    let response = client
        .detect_dominant_language()
        .text(sample_text)
        .send()
        .await;

    match response {
        Ok(output) => {
            if let Some(languages) = output.languages {
                println!("Detected languages:");
                for language in languages {
                    println!(
                        "  Language: {} (confidence: {:.2}%)",
                        language.language_code().unwrap_or("unknown"),
                        language.score().unwrap_or(0.0) * 100.0
                    );
                }
            } else {
                println!("No languages detected.");
            }
        }
        Err(err) => {
            let err = err.into_service_error();
            let meta = err.meta();
            let message = meta.message().unwrap_or("unknown");
            let code = meta.code().unwrap_or("unknown");
            eprintln!("Error detecting language: ({code}) {message}");
        }
    }
}
// snippet-end:[comprehend.rust.hello]

/// Hello Amazon Comprehend - demonstrates basic language detection.
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
    
    println!();
    println!("Hello Amazon Comprehend!");
    println!();

    if verbose {
        println!("Comprehend client version: {}", PKG_VERSION);
        println!(
            "Region:                    {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    detect_sample_language(&client).await;

    println!();
    println!("Hello Amazon Comprehend completed successfully!");

    Ok(())
}