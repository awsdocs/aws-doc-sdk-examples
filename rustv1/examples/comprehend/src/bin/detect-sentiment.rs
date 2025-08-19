// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#![allow(clippy::result_large_err)]

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_comprehend::{config::Region, Client, Error};
use clap::Parser;

#[derive(Debug, Parser)]
struct Opt {
    /// The text to analyze for sentiment.
    #[structopt(short, long)]
    text: Option<String>,

    /// The language code (e.g., "en" for English).
    #[structopt(short, long)]
    language_code: Option<String>,

    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// snippet-start:[comprehend.rust.detect-sentiment]
/// Detects sentiment in the provided text using Amazon Comprehend.
async fn detect_sentiment(client: &aws_sdk_comprehend::Client, text: &str, language_code: &str) -> Result<(), Error> {
    let response = client
        .detect_sentiment()
        .text(text)
        .language_code(language_code.into())
        .send()
        .await?;

    println!("Sentiment analysis results:");
    println!("  Overall sentiment: {}", response.sentiment().unwrap().as_str());
    
    if let Some(sentiment_score) = response.sentiment_score {
        println!("  Sentiment scores:");
        println!("    Positive: {:.2}%", sentiment_score.positive().unwrap_or(0.0) * 100.0);
        println!("    Negative: {:.2}%", sentiment_score.negative().unwrap_or(0.0) * 100.0);
        println!("    Neutral:  {:.2}%", sentiment_score.neutral().unwrap_or(0.0) * 100.0);
        println!("    Mixed:    {:.2}%", sentiment_score.mixed().unwrap_or(0.0) * 100.0);
    }

    Ok(())
}
// snippet-end:[comprehend.rust.detect-sentiment]

/// Detects sentiment in text using Amazon Comprehend.
/// 
/// # Arguments
///
/// * `[-t TEXT]` - The text to analyze. If not provided, uses a default sample.
/// * `[-l LANGUAGE_CODE]` - The language code (e.g., "en"). If not provided, defaults to "en".
/// * `[-r REGION]` - The Region in which the client is created.
///   If not supplied, uses the value of the **AWS_REGION** environment variable.
///   If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();
    let Opt { text, language_code, region, verbose } = Opt::parse();

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
        "I absolutely love using Amazon Web Services! The cloud services are fantastic and make development so much easier."
    );
    let lang_code = language_code.as_deref().unwrap_or("en");

    println!("Analyzing text: \"{}\"", text_to_analyze);
    println!("Language code: {}", lang_code);
    println!();

    detect_sentiment(&client, text_to_analyze, lang_code).await?;

    Ok(())
}