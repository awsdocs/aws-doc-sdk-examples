/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0.
 */

use aws_config::meta::region::RegionProviderChain;
use aws_sdk_polly::model::{Engine, Voice};
use aws_sdk_polly::{Client, Error, Region, PKG_VERSION};
use structopt::StructOpt;

#[derive(Debug, StructOpt)]
struct Opt {
    /// The AWS Region.
    #[structopt(short, long)]
    region: Option<String>,

    /// Whether to display additional information.
    #[structopt(short, long)]
    verbose: bool,
}

// Lists the available voices and language.
// snippet-start:[polly.rust.polly-helloworld]
async fn show_voices(client: &Client) -> Result<(), Error> {
    let mut tok: Option<String> = None;
    let mut voices: Vec<Voice> = vec![];

    // Below is an an example of how pagination can be implemented manually.
    loop {
        let mut req = client.describe_voices();

        if let Some(tok) = tok {
            req = req.next_token(tok);
        }

        let resp = req.send().await?;

        for voice in resp.voices().unwrap_or_default() {
            println!(
                "I can speak as: {} in {:?}",
                voice.name().as_ref().unwrap(),
                voice.language_name().as_ref().unwrap()
            );
            voices.push(Voice::clone(voice));
        }

        tok = match &resp.next_token() {
            Some(next) => Some(next.to_string()),
            None => break,
        };
    }

    let neural_voices = voices
        .iter()
        .filter(|voice| {
            voice
                .supported_engines()
                .unwrap_or_default()
                .contains(&Engine::Neural)
        })
        .map(|voice| voice.id().unwrap())
        .collect::<Vec<_>>();

    println!();
    println!("Voices supporting a neural engine: {:?}", neural_voices);
    println!();

    Ok(())
}
// snippet-end:[polly.rust.polly-helloworld]

/// Displays a list of the voices and their language, and those supporting a neural engine, in the Region.
/// # Arguments
///
/// * `[-r REGION]` - The region in which the client is created.
///    If not supplied, uses the value of the **AWS_DEFAULT_REGION** environment variable.
///    If the environment variable is not set, defaults to **us-west-2**.
/// * `[-v]` - Whether to display additional information.
#[tokio::main]
async fn main() -> Result<(), Error> {
    tracing_subscriber::fmt::init();

    let Opt { region, verbose } = Opt::from_args();

    let region_provider = RegionProviderChain::first_try(region.map(Region::new))
        .or_default_provider()
        .or_else(Region::new("us-west-2"));

    println!();

    if verbose {
        println!("Polly client version: {}", PKG_VERSION);
        println!(
            "Region:               {}",
            region_provider.region().await.unwrap().as_ref()
        );
        println!();
    }

    let shared_config = aws_config::from_env().region(region_provider).load().await;
    let client = Client::new(&shared_config);

    show_voices(&client).await
}
